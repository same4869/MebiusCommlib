package com.pokemon.mebius.commlib.utils

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * 通用扩展
 */

fun View.getActivity(): AppCompatActivity? {
    return context.getActivity()
}

fun Context.getActivity(): AppCompatActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}