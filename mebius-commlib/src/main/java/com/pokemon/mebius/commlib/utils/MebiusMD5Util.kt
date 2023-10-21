package com.pokemon.mebius.commlib.utils

import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest

/**
 *  Description: 给对应字符串md5，如果是对大文件md5的话，可以使用md5库
 *  author: xun.wang on 2019/5/29
 **/
object MebiusMD5Utils {

    /**
     * md5加密字符串
     * md5使用后转成16进制变成32个字节
     */
    fun md5(str: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(str.toByteArray())
        //没转16进制之前是16位
        //转成16进制后是32字节
        return md5(result)
    }

    fun md5(byteArray: ByteArray): String {
        //转成16进制后是32字节
        return with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append("0").append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            toString()
        }
    }

    fun md5(file: File): String? {
        if (!file.isFile) {
            return null
        }
        var digest: MessageDigest? = null
        var `in`: FileInputStream? = null
        val buffer = ByteArray(1024)
        var len: Int
        try {
            digest = MessageDigest.getInstance("MD5")
            `in` = FileInputStream(file)
            len = `in`.read(buffer, 0, 1024)
            while (len != -1) {
                digest!!.update(buffer, 0, len)
                len = `in`.read(buffer, 0, 1024)
            }
            `in`.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val bigInt = BigInteger(1, digest!!.digest())
        val md5Str = bigInt.toString(16)
        return if (md5Str.length < 32) {
            val temp0 = 32 - md5Str.length
            val sb = StringBuilder()
            for (i in 0 until temp0) {
                sb.append("0")
            }
            sb.append(md5Str)
            sb.toString()
        } else {
            md5Str
        }
    }
}
