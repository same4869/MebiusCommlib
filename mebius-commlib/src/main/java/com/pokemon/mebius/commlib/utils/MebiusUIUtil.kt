package com.pokemon.mebius.commlib.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat

/**
 * @Description:    view相关，屏幕相关的通用工具类
 * @Author:         xwang
 * @CreateDate:     2020/12/4
 */

//显示
fun View.show() {
    this.visibility = View.VISIBLE
}

//隐藏
fun View.gone() {
    this.visibility = View.GONE
}

//不可见
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

//获得屏幕高度
fun getScreenHeight(): Int {
    val outMetrics = DisplayMetrics()
    (APPLICATION.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(
        outMetrics
    )
    return outMetrics.heightPixels
}

//获得屏幕宽度
fun getScreenWidth(): Int {
    val outMetrics = DisplayMetrics()
    (APPLICATION.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(
        outMetrics
    )
    return outMetrics.widthPixels
}

fun getScreenRatio(): Int {
    return getScreenHeight() / getScreenWidth()
}

//dp单位转换
val Number.dp2px
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

//通过字符串获得颜色
fun getColorByString(colorStr: String): Int {
    return try {
        Color.parseColor(colorStr)
    } catch (e: Exception) {
        Color.parseColor("#777777")
    }
}

fun getDrawable(context: Context?, resId: Int): Drawable? {
    return if (null == context || context.resources == null || resId <= -1) {
        ColorDrawable()
    } else ContextCompat.getDrawable(context, resId)
}

fun getColor(context: Context?, resId: Int): Int {
    return if (null == context || context.resources == null || resId <= -1) {
        Color.TRANSPARENT
    } else ContextCompat.getColor(context, resId)
}

fun getResString(resId: Int): String {
    return APPLICATION.getString(resId)
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE

fun View.setVisibleOrGone(flag: Boolean) {
    visibility = if (flag) View.VISIBLE else View.GONE
}

fun View.setVisibleOrInvisible(flag: Boolean) {
    visibility = if (flag) View.VISIBLE else View.INVISIBLE
}
