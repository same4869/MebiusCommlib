package com.pokemon.mebius.commlib.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import com.jakewharton.rxbinding3.view.clicks
import com.pokemon.mebius.commlib.databinding.ViewToastBinding
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * @Description:    通用业务工具类，这个需要rxbind
 * @Author:         xwang
 * @CreateDate:     2020/12/4
 */

lateinit var APPLICATION: Application

typealias SimpleOnClickListener = () -> Unit

//点击事件，防抖500ms
fun View.onClick(onClick: SimpleOnClickListener) {
    throttleFirstClick(Consumer { onClick() })
}

fun View.onClick(duration: Long, onClick: SimpleOnClickListener) {
    throttleFirstClick(duration, Consumer { onClick() })
}

@SuppressLint("CheckResult")
internal fun View.throttleFirstClick(duration: Long, action: Consumer<Any?>) {
    this.clicks().throttleFirst(duration, TimeUnit.MILLISECONDS)
        .subscribe({
            try {
                action.accept(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, {
            it.printStackTrace()
        }).disposeOnDestroyByContext(context)
}

//点击事件，防抖500ms
@SuppressLint("CheckResult")
internal fun View.throttleFirstClick(action: Consumer<Any?>) {
    this.clicks().throttleFirst(500L, TimeUnit.MILLISECONDS)
        .subscribe({
            try {
                action.accept(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, {
            it.printStackTrace()
        }).disposeOnDestroyByContext(context)
}

fun getAppVersionName(): String {
    val manager = APPLICATION.packageManager
    var name = ""
    try {
        val info =
            manager.getPackageInfo(APPLICATION.packageName, 0)
        name = info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return name
}

fun getAppVersionCode(): Int {
    val manager = APPLICATION.packageManager
    var code = 0
    try {
        val info =
            manager.getPackageInfo(APPLICATION.packageName, 0)
        code = info.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return code
}

//运营商 isWantCode只返回码不返回运营商名字
fun getSubscriptionOperatorType(context: Context, isWantCode: Boolean = false): String {
    var opeType = "Unknown"
    // No sim
    if (!hasSim(context)) {
        return opeType
    }
    val tm =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val operator = tm.simOperator
    if (isWantCode) return operator
    // 中国联通
    opeType = if ("46001" == operator || "46006" == operator || "46009" == operator) {
        "中国联通"
    } else if ("46000" == operator || "46002" == operator || "46004" == operator || "46007" == operator) {
        "中国移动"
    } else if ("46003" == operator || "46005" == operator || "46011" == operator) {
        "中国电信"
    } else {
        "Unknown"
    }
    return opeType
}

private fun hasSim(context: Context): Boolean {
    val tm =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val operator = tm.simOperator
    return !TextUtils.isEmpty(operator)
}

//通用toast显示方法
private var mToast: Toast? = null

fun showToast(
    @StringRes message: Int,
    isCancelLast: Boolean = true,
    isShowBackground: Boolean = false
) {
    showToast(
        APPLICATION.getString(message),
        isCancelLast,
        isShowBackground
    )
}

/**
 * isCancelLast:显示前是否先取消掉之前的（如果有，防止toast叠加
 * isShowBackground:是否允许应用后台是弹toast
 */
fun showToast(
    message: String?,
    isCancelLast: Boolean = true,
    isShowBackground: Boolean = false
) {
    if (message.isNullOrEmpty()) {
        return
    }
    if (!isShowBackground && !isAppForeground(APPLICATION)) {
        return
    }
    if (isCancelLast) {
        mToast?.cancel()
    }

    val binding = ViewToastBinding.inflate(LayoutInflater.from(APPLICATION))
    binding.mToastTv.text = message

    mToast = Toast.makeText(APPLICATION, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        view =  binding.root
    }
    mToast?.show()
}

/**
 * 应用是否处于前台
 * @return
 */
fun isAppForeground(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcessInfos = manager.runningAppProcesses
    appProcessInfos?.forEach {
        //当前应用处于运行中，并且在前台
        if (it.processName == context.packageName
            && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        ) {
            return true
        }
    }
    return false
}

/**
 * 软键盘是否打开
 */
fun hasSoftKeys(windowManager: WindowManager): Boolean {
    val d = windowManager.defaultDisplay
    val realDisplayMetrics = DisplayMetrics()
    d.getRealMetrics(realDisplayMetrics)
    val realHeight = realDisplayMetrics.heightPixels
    val realWidth = realDisplayMetrics.widthPixels
    val displayMetrics = DisplayMetrics()
    d.getMetrics(displayMetrics)
    val displayHeight = displayMetrics.heightPixels
    val displayWidth = displayMetrics.widthPixels
    return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
}


fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(this.windowToken, 0)
}