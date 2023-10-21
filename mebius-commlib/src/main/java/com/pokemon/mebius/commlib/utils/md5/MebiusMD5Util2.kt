package com.pokemon.mebius.commlib.utils.md5

import com.pokemon.mebius.commlib.utils.MebiusFileUtil
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

/**
 * @author xwang
 * 2023/5/29 13:17
 * @description 除了普通的MD5计算，对大文件还有抽样计算，来提高md5的效率
 **/
object MebiusMD5Util2 {

    /**
     * 计算字符串的md5
     */
    fun md5(str: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(str.toByteArray())
        //没转16进制之前是16位
        //转成16进制后是32字节
        return md5(result)
    }

    /**
     * 计算字节数组的md5
     */
    fun md5(byteArray: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(
            1, md.digest(byteArray)
        ).toString(
            16
        ).padStart(
            32, '0'
        )
    }

    /**
     * 计算一个文件的md5
     *
     *@param isSampling 是否是抽样计算md5。默认情况下，文件大小大于100M会采用抽样计算
     */
    fun md5(
        file: File,
        isSampling: Boolean = file.length() > 100 * 1024 * 1024,
        partSize: Int = 2 * 1024 * 1024
    ): String? {
        if (!file.isFile) {
            return null
        }
        return if (isSampling) {
            samplingMd5(
                BufferedInputStream(
                    FileInputStream(file),
                    partSize
                ),
                partSize
            )
        } else {
            md5(
                BufferedInputStream(
                    FileInputStream(file),
                    partSize
                )
            )
        }
    }

    /**
     * 计算输入流的md5
     */
    fun md5(
        inputStream: InputStream
    ): String {
        val hexDigits = arrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        )
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(1024)
        var len: Int
        inputStream.use {
            do {
                len = it.read(buffer, 0, 1024)
                if (len > 0) {
                    digest.update(buffer, 0, len)
                }
            } while (len != -1)

            digest.digest().let {
                val b = StringBuffer(2 * it.size)
                for (index in it.indices) {
                    val bt = it[index]
                    val c0 = (bt.toInt() and 0xf0).let {
                        it shr 4
                    }.let {
                        hexDigits[it]
                    }
                    val c1 = hexDigits[
                            bt.toInt() and 0xf
                    ]
                    b.append(c0)
                    b.append(c1)
                }
                return b.toString()
            }
        }
    }

    /**
     *
     * 抽样计算大文件的md5
     *
     * 实现思路: https://juejin.cn/post/6844904055819468808#heading-4
     *
     */
    fun samplingMd5(
        inputStream: InputStream,
        partSize: Int = 2 * 1024 * 1024
    ): String {
        inputStream.use {
            val numSize = inputStream.available()
            var currentIndex = partSize.toLong()

            val chunks = Stack<ByteArray>()
            // 把第一块push进去
            chunks.push(
                MebiusFileUtil.getFileBlock(inputStream, 0, partSize)
            )
            while (currentIndex < numSize) {
                if (currentIndex + partSize >= numSize) {
                    // 最后一块全部加进来
                    chunks.push(
                        MebiusFileUtil.getFileBlock(inputStream, currentIndex, partSize)
                    )
                } else {
                    // 中间的 前中后去两个字节
                    val mid = currentIndex + partSize / 2
                    val end = currentIndex + partSize
                    chunks.push(
                        MebiusFileUtil.getFileBlock(inputStream, currentIndex, 2)
                    )
                    chunks.push(
                        MebiusFileUtil.getFileBlock(inputStream, mid, 2)
                    )
                    chunks.push(
                        MebiusFileUtil.getFileBlock(inputStream, end - 2, 2)
                    )
                }
                // 进入下一个分片
                currentIndex += partSize
            }
            val arrayBuffer = ByteArrayOutputStream()
            chunks.forEach {
                arrayBuffer.write(
                    it ?: return@forEach
                )
            }
            return arrayBuffer.toByteArray().mebiusMD5()
        }
    }
}
