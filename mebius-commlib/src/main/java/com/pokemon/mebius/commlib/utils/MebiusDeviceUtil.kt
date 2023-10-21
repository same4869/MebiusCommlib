package com.pokemon.mebius.commlib.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.UUID

/**
 * @Description:    设备相关工具类，deviceID等
 * @Author:         xwang
 * @CreateDate:     2020/12/15
 */

private var uuid: UUID? = null
private const val PREFS_DEVICE_ID = "device_id"
private const val PREFS_FILE = "pre_device.xml"

object MebiusDeviceUtil {
    val deviceName: String
        get() {
            val manufacturer =
                if (Build.MANUFACTURER == null) "" else Build.MANUFACTURER
            val model = if (Build.MODEL == null) "" else Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

    val cpuName: String
        get() {
            var cpuName = ""
            try {
                val fr = FileReader("/proc/cpuinfo")
                val br = BufferedReader(fr)
                var line: String
                while (true) {
                    line = br.readLine() ?: break
                    val result = line.toLowerCase()
                    val array = result.split(":\\s+".toRegex(), 2).toTypedArray()
                    if (array[0].startsWith("hardware")) {
                        cpuName = array[1]
                        break
                    }
                }
            } catch (e: IOException) {
            }
            return if (cpuName.isNotEmpty()) cpuName else Build.HARDWARE
        }

    private fun capitalize(s: String): String {
        if (TextUtils.isEmpty(s) || s.trim { it <= ' ' }.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}

//是否阻止获取敏感性设备信息
var interruptDeviceIdGet: Boolean = false

fun getDeviceId(context: Context?): String {
    if (interruptDeviceIdGet || context == null) {
        return ""
    }
    if (uuid == null) {
        synchronized(Any::class.java) {
            if (uuid == null) {
                val prefs = context.getSharedPreferences(PREFS_FILE, 0)
                val id = prefs.getString(PREFS_DEVICE_ID, null)
                if (id != null) {
                    uuid = UUID.fromString(id)
                } else {
                    val androidId = getAndroidId()

                    try {
                        uuid =
                            if (!TextUtils.isEmpty(androidId) && "9774d56d682e549c" != androidId) {
                                //一般情况走这里
                                UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf8")))
                            } else {
                                val deviceId = getIMEIId(context)
                                if (deviceId != null && !TextUtils.equals(
                                        deviceId,
                                        "unknow"
                                    )
                                ) UUID.nameUUIDFromBytes(
                                    deviceId.toByteArray(
                                        charset("utf8")
                                    )
                                ) else UUID.randomUUID()
                            }
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    prefs.edit().putString(
                        PREFS_DEVICE_ID,
                        uuid.toString()
                    ).apply()
                }
            }
        }
    }
    return uuid.toString()
}

//获得rom或者ram的信息,isRomOrRam为true则返回rom，isFreeOrTotal为true则返回剩余的，单位为m
fun getSystemRomInfo(context: Context, isRomOrRam: Boolean, isFreeOrTotal: Boolean): Long {
    return when {
        isRomOrRam && isFreeOrTotal -> {
            getRomSpaceRemaining(context)
        }

        isRomOrRam && !isFreeOrTotal -> {
            getTotalRomSpace(context)
        }

        !isRomOrRam && isFreeOrTotal -> {
            getRamSpaceRemaining(context)
        }

        !isRomOrRam && !isFreeOrTotal -> {
            getTotalRamSpace(context)
        }

        else -> {
            -99L
        }
    }
}

private fun getRomSpaceRemaining(context: Context): Long {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id: UUID = StorageManager.UUID_DEFAULT
            val stats: StorageStatsManager? =
                context.getSystemService(StorageStatsManager::class.java)
            return stats?.getFreeBytes(id)?.div((1024 * 1024)) ?: -99
        }

    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return Environment.getDataDirectory().freeSpace / (1024 * 1024)
}

private fun getRamSpaceRemaining(context: Context): Long {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    val memInfo = ActivityManager.MemoryInfo()
    actManager?.let {
        actManager.getMemoryInfo(memInfo)
        return memInfo.availMem
    }
    return -99
}

private fun getTotalRamSpace(context: Context): Long {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    val memInfo = ActivityManager.MemoryInfo()
    actManager?.let {
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem
    }
    return -99
}

private fun getTotalRomSpace(context: Context): Long {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id: UUID = StorageManager.UUID_DEFAULT
            val stats: StorageStatsManager? =
                context.getSystemService(StorageStatsManager::class.java)
            return stats?.getTotalBytes(id)?.div((1024 * 1024)) ?: -99
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return Environment.getDataDirectory().totalSpace / (1024 * 1024)

}

@SuppressLint("MissingPermission", "HardwareIds")
private fun getIMEIId(context: Context?): String {
    if (context == null) return "unknow"
    var deviceId = ""
    try {
        if (ContextCompat.checkSelfPermission(
                context,
                "android.permission.READ_PHONE_STATE"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val telephony =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephony.imei
            } else {
                telephony.deviceId
            }
        }
    } catch (var3: Exception) {
        var3.printStackTrace()
    }
    return deviceId
}

@SuppressLint("HardwareIds")
private fun getAndroidId(): String {
    return Settings.Secure.getString(
        APPLICATION.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: ""
}