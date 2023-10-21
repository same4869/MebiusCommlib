package com.pokemon.mebius.commlib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.ConcurrentHashMap

/**
 * @Description:    通用sp类
 * @Author:         xwang
 * @CreateDate:     2020/12/4
 */
@SuppressLint("StaticFieldLeak")
object MebiusSPUtil {
    private const val SP_TABLE_DEFAULT = "global"

    private val sSPMap = ConcurrentHashMap<String, SharedPreferences>()

    private val context: Context = APPLICATION

    private var isMMKVInit = false

    /**
     * 获取SP实例
     * @param spName sp名
     * *
     * @return [SPUtils]
     */
    fun getInstance(name: String = SP_TABLE_DEFAULT): SharedPreferences {
        var sp = sSPMap[name]
        if (sp == null) {
            sp = getSharedPreferences(name)
            sSPMap[name] = sp
        }
        return sp
    }

    private fun getSharedPreferences(name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

//    /**
//     * 初始化MMKV
//     */
//    fun initializeHoYoMMKV(context: Context,rootPath: String? = null) {
//        initializeWithHoYoMMKV(context, rootPath)
//        isMMKVInit = true
//    }
//
//    /**
//     * 获取MMKV实例
//     */
//    fun getMMKVInstance(
//        mmapID: String? = SP_TABLE_DEFAULT,
//        mode: ProcessMode = ProcessMode.SingleProcess,
//        cryptKey: String? = null,
//        rootPath: String? = null
//    ): IHoYoMMKVHandler {
//        check(isMMKVInit) {
//            "error,plz call initializeHoYoMMKV to initialize MMKV..."
//        }
//        val mmkv = if (cryptKey != null) {
//            if (mmapID != null) {
//                requireHoYoMMKV(mmapID, mode, cryptKey, rootPath)
//            } else {
//                requireHoYoMMKV(cryptKey)
//            }
//        } else {
//            requireHoYoMMKV()
//        }
//        return mmkv
//    }

}

/**
 * SP中写入String

 * @param key   键
 * *
 * @param value 值
 */
fun SharedPreferences.put(key: String, value: String?) {
    this.edit().putString(key, value).apply()
}

/**
 * SP中写入String

 * @param key   键
 * *
 * @param value 值
 */
@SuppressLint("ApplySharedPref")
fun SharedPreferences.putSyn(key: String, value: String?) {
    this.edit().putString(key, value).commit()
}

/**
 * SP中写入Int

 * @param key   键
 * *
 * @param value 值
 */
@SuppressLint("ApplySharedPref")
fun SharedPreferences.putSyn(key: String, value: Int) {
    this.edit().putInt(key, value).commit()
}

/**
 * SP中读取String

 * @param key          键
 * *
 * @param defaultValue 默认值
 * *
 * @return 存在返回对应值，不存在返回默认值`defaultValue`
 */
@JvmOverloads
fun SharedPreferences.getString(key: String, defaultValue: String = ""): String {
    return this.getString(key, defaultValue) ?: ""
}

/**
 * SP中写入int

 * @param key   键
 * *
 * @param value 值
 */
fun SharedPreferences.put(key: String, value: Int) {
    this.edit().putInt(key, value).apply()
}

/**
 * SP中读取int

 * @param key          键
 * *
 * @param defaultValue 默认值
 * *
 * @return 存在返回对应值，不存在返回默认值`defaultValue`
 */
@JvmOverloads
fun SharedPreferences.getInt(key: String, defaultValue: Int = -1): Int {
    return this.getInt(key, defaultValue)
}

/**
 * SP中写入long

 * @param key   键
 * *
 * @param value 值
 */
fun SharedPreferences.put(key: String, value: Long) {
    this.edit().putLong(key, value).apply()
}

/**
 * SP中读取long

 * @param key          键
 * *
 * @param defaultValue 默认值
 * *
 * @return 存在返回对应值，不存在返回默认值`defaultValue`
 */
@JvmOverloads
fun SharedPreferences.getLong(key: String, defaultValue: Long = -1L): Long {
    return this.getLong(key, defaultValue)
}

/**
 * SP中写入float

 * @param key   键
 * *
 * @param value 值
 */
fun SharedPreferences.put(key: String, value: Float) {
    this.edit().putFloat(key, value).apply()
}

/**
 * SP中读取float

 * @param key          键
 * *
 * @param defaultValue 默认值
 * *
 * @return 存在返回对应值，不存在返回默认值`defaultValue`
 */
@JvmOverloads
fun SharedPreferences.getFloat(key: String, defaultValue: Float = -1f): Float {
    return this.getFloat(key, defaultValue)
}

/**
 * SP中写入boolean

 * @param key   键
 * *
 * @param value 值
 */
fun SharedPreferences.put(key: String, value: Boolean) {
    this.edit().putBoolean(key, value).apply()
}

/**
 * SP中读取boolean

 * @param key          键
 * *
 * @param defaultValue 默认值
 * *
 * @return 存在返回对应值，不存在返回默认值`defaultValue`
 */
@JvmOverloads
fun SharedPreferences.getBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return this.getBoolean(key, defaultValue)
}

/**
 * SP中写入String集合

 * @param key    键
 * *
 * @param values 值
 */
fun SharedPreferences.put(key: String, values: Set<String>) {
    this.edit().putStringSet(key, values).apply()
}

/**
 * SP中移除该key

 * @param key 键
 */
fun SharedPreferences.remove(key: String) {
    this.edit().remove(key).apply()
}

/**
 * SP中清除所有数据
 */
fun SharedPreferences.clear() {
    this.edit().clear().apply()
}