package com.pokemon.mebius.commlib.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * @Description:    判断网络状态等工具类
 * @Author:         xwang
 * @CreateDate:     2020/12/15
 */

fun isNetworkAvailable(
    context: Context,
): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as? ConnectivityManager ?: return false
    //may crash @see https://issuetracker.google.com/issues/175055271
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork
            val actNw = connectivityManager.getNetworkCapabilities(
                networkCapabilities
            )
            when {
                networkCapabilities == null || actNw == null -> false
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    true
                }

                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    true
                }

                else -> false
            }
        } else {
            when (connectivityManager.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    true
                }

                ConnectivityManager.TYPE_MOBILE -> {
                    true
                }

                else -> false
            }
        }
    } catch (e: Exception) {
        false
    }
}

fun isWifi(
    context: Context,
): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as? ConnectivityManager ?: return false
    //may crash @see https://issuetracker.google.com/issues/175055271
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork
            val actNw = connectivityManager.getNetworkCapabilities(
                networkCapabilities
            )
            when {
                networkCapabilities == null || actNw == null -> false
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    true
                }

                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    false
                }

                else -> false
            }
        } else {
            when (connectivityManager.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    true
                }

                ConnectivityManager.TYPE_MOBILE -> {
                    false
                }

                else -> false
            }
        }
    } catch (e: Exception) {
        false
    }
}