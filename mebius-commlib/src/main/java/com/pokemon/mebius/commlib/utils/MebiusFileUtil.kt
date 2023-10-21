package com.pokemon.mebius.commlib.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * @Description:    文件相关工具类
 * @Author:         xwang
 * @CreateDate:     2020/12/4
 */
fun getCacheDirPath(): String {
    val externalStorageAvailable =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    return if (externalStorageAvailable) {
        APPLICATION.externalCacheDir?.path ?: APPLICATION.cacheDir.path
    } else {
        APPLICATION.cacheDir?.path ?: ""
    }
}

fun getFileDirPath(): String {
    val externalStorageAvailable =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    return if (externalStorageAvailable) {
        APPLICATION.getExternalFilesDir(null)?.path ?: APPLICATION.filesDir.path
    } else {
        APPLICATION.filesDir?.path ?: ""
    }
}

object MebiusFileUtil {
    fun copyImageFile(
        context: Context,
        source: File,
        to: File
    ): Boolean {
        val resolver: ContentResolver = context.contentResolver
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, to.name)
        values.put(MediaStore.Images.Media.DESCRIPTION, to.name)
        values.put(
            MediaStore.Images.Media.MIME_TYPE,
            "image/${getImageMimeType(source.absolutePath)}"
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                to.parent
            )
        } else {
            values.put(
                MediaStore.Images.Media.DATA,
                to.absolutePath
            )
        }
        try {
            val insertUri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            ) ?: return false
            val inputStream = BufferedInputStream(
                FileInputStream(source.absolutePath)
            )
            inputStream.use {
                val out = resolver.openOutputStream(insertUri) ?: return false
                out.use {
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        out.write(buffer, 0, len)
                    }
                    out.flush()
                }
                return true
            }
        } catch (e: Exception) {
            return false
        }
    }

    private fun getImageMimeType(
        absolutePath: String
    ): String? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(absolutePath, options)
        return options.outMimeType
    }

    /**
     * @param file 需要读取分片的源文件
     * @param skipSize 读取时，开头需要跳过的内容长度
     * @param partSize 分片的大小
     */
    fun getFileBlock(
        file: File,
        skipSize: Long,
        partSize: Int
    ): ByteArray? {
        val result = ByteArray(partSize)
        //TODO 是否可以优化。
        // 一般取分片的时候，会取很多次，不需要每次都创建一个RandomAccessFile
        RandomAccessFile(file, "r").use { accessFile ->
            // 跳过不需要读取的
            accessFile.seek(skipSize)
            // 读取一个分片的大小
            // 看读取的结果
            return when (val readSize = accessFile.read(result)) {
                -1 -> {
                    //文件结束
                    null
                }

                partSize -> {
                    result
                }

                else -> {
                    val readByte = ByteArray(readSize)
                    System.arraycopy(result, 0, readByte, 0, readSize)
                    readByte
                }
            }
        }
    }

    /**
     * 获取输入流中的某一块数据
     *
     * 但是要注意的是[InputStream.skip]方法是会重叠生效的，是无法，跳到后面的部分又调回原来的部分
     *
     * 这并不是类似[RandomAccessFile.seek]方法。
     *
     * 所以外面传入的输入流对象在使用的时候要注意这一点，一般来说，不需要考虑这个问题
     */
    fun getFileBlock(
        fileInputStream: InputStream,
        skipSize: Long,
        partSize: Int
    ): ByteArray? {
        val result = ByteArray(partSize)
        // 跳过字节数，可能不符合我们的预期
        // 需要循环跳过直到跳过这么多字节数
        var currentSkipSize = skipSize
        while (currentSkipSize > 0) {
            val skipSuccessSize = fileInputStream.skip(currentSkipSize)
            if (skipSuccessSize == -1L) {
                return null
            }
            currentSkipSize -= skipSuccessSize
        }
        return when (val readSize = fileInputStream.read(result)) {
            -1 -> {
                //文件结束
                null
            }

            partSize -> {
                result
            }

            else -> {
                val readByte = ByteArray(readSize)
                System.arraycopy(result, 0, readByte, 0, readSize)
                readByte
            }
        }
    }
}
