package com.pokemon.mebius.commlib.utils

import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * @Description:
 * 支持按任务的优先级去执行,
 * 支持线程池暂停.恢复(批量文件下载，上传) ，
 * 异步结果主动回调主线程
 * @Author:         xwang
 * @CreateDate:     2020/8/17
 */

object MebiusExecutor {
    private var mebiusExecutor: ThreadPoolExecutor
    private var isPaused: Boolean = false
    private var lock: ReentrantLock = ReentrantLock()
    private var pauseCondition: Condition
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        pauseCondition = lock.newCondition()

        val cpuCount = Runtime.getRuntime().availableProcessors()
        val corePoolSize = cpuCount + 1
        val maxPoolSize = cpuCount * 2 + 1
        val blockQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()
        val keepAliveTime = 30L
        val unit = TimeUnit.SECONDS

        val seq = AtomicLong()
        val threadFactory = ThreadFactory {
            val thread = Thread(it)
            thread.name = "mebius-executor-" + seq.getAndIncrement()
            return@ThreadFactory thread
        }

        mebiusExecutor = object : ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            unit,
            blockQueue as BlockingQueue<Runnable>,
            threadFactory
        ) {
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                if (isPaused) {
                    lock.lock()
                    try {
                        pauseCondition.await()
                    } finally {
                        lock.unlock()
                    }
                }
            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                //监控线程池耗时任务,线程创建数量,正在运行的数量
//                LogUtil.d("已执行完的任务的优先级是：" + (r as PriorityRunnable).priority)
            }
        }
    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Runnable) {
        mebiusExecutor.execute(PriorityRunnable(priority, runnable))
    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Callable<*>) {
        mebiusExecutor.execute(PriorityRunnable(priority, runnable))
    }

    abstract class Callable<T> : Runnable {
        override fun run() {
            mainHandler.post { onPrepare() }

            val t: T = onBackground();

            //移除所有消息.防止需要执行onCompleted了，onPrepare还没被执行，那就不需要执行了
            //但是在多任务并发时进行全部任务移除会导致其他任务中断，先不移除消息
//            mainHandler.removeCallbacksAndMessages(null)
//            LogUtil.d("onCompleted~~")
            mainHandler.post { onCompleted(t) }
        }

        open fun onPrepare() {}

        abstract fun onBackground(): T
        abstract fun onCompleted(t: T)
    }

    class PriorityRunnable(val priority: Int, private val runnable: Runnable) : Runnable,
        Comparable<PriorityRunnable> {
        override fun compareTo(other: PriorityRunnable): Int {
            return if (this.priority < other.priority) 1 else if (this.priority > other.priority) -1 else 0
        }

        override fun run() {
            runnable.run()
        }
    }

    fun pause() {
        lock.lock()
        try {
            if (isPaused) return
            isPaused = true
        } finally {
            lock.unlock()
        }
//        LogUtil.d("SodaExecutor is paused")
    }

    fun resume() {
        lock.lock()
        try {
            if (!isPaused) return
            isPaused = false
            pauseCondition.signalAll()
        } finally {
            lock.unlock()
        }
//        LogUtil.d("SodaExecutor is resumed")
    }
}