package com.pokemon.mebius.commlib.utils.md5

import java.io.File
import java.io.InputStream


fun String.mebiusMD5(): String {
    return MebiusMD5Util2.md5(this)
}

fun ByteArray.mebiusMD5(): String {
    return MebiusMD5Util2.md5(this)
}

/**
 *
 * 获取file文件对应的MD5
 *
 * @param isSampling 是否是抽样计算md5。默认情况下，文件大小大于100M会采用抽样计算
 * @param partSize 抽样计算MD5时，每个分片的大小
 */
fun File.mebiusMD5(
    isSampling: Boolean = this.length() > 100 * 1024 * 1024,
    partSize: Int = 2 * 1024 * 1024
): String {
    return MebiusMD5Util2.md5(
        file = this,
        isSampling = isSampling,
        partSize = partSize
    ) ?: ""
}

/**
 *
 * 获取InputStream流对应的MD5
 * @param isSampling 是否是抽样计算md5。默认情况下，为true。由于InputStream获取文件的大小存在不稳定的情况。
 * @param partSize 抽样计算MD5时，每个分片的大小
 */
fun InputStream.mebiusMD5(
    isSampling: Boolean = true,
    partSize: Int = 2 * 1024 * 1024
): String {
    return if (isSampling) {
        MebiusMD5Util2.samplingMd5(this, partSize)
    } else {
        MebiusMD5Util2.md5(this)
    }
}
