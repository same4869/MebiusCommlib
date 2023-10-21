package com.pokemon.mebius.commlib.demo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pokemon.mebius.commlib.demo.databinding.ActivityMainBinding
import com.pokemon.mebius.commlib.utils.MebiusDeviceUtil
import com.pokemon.mebius.commlib.utils.MebiusSPUtil
import com.pokemon.mebius.commlib.utils.getDeviceId
import com.pokemon.mebius.commlib.utils.getScreenHeight
import com.pokemon.mebius.commlib.utils.getScreenRatio
import com.pokemon.mebius.commlib.utils.getScreenWidth
import com.pokemon.mebius.commlib.utils.isNetworkAvailable
import com.pokemon.mebius.commlib.utils.isWifi
import com.pokemon.mebius.commlib.utils.onClick
import com.pokemon.mebius.commlib.utils.putSyn
import com.pokemon.mebius.commlib.utils.showToast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.commTipsTv.text =
            "屏幕高度：${getScreenHeight()} 屏幕宽度：${getScreenWidth()} 屏幕比例：${getScreenRatio()} \n" +
                    "设备名为:${MebiusDeviceUtil.deviceName} 设备CPU名为：${MebiusDeviceUtil.cpuName} \n设备ID为：${
                        getDeviceId(
                            applicationContext
                        )
                    } \n" +
                    "当前有网吗？${isNetworkAvailable(applicationContext)} 是不是wifi?${
                        isWifi(
                            applicationContext
                        )
                    }"

        binding.mShowToastBtn.onClick {
            showToast("我是一个Toast~~~")
        }

        binding.mSpTv.text = MebiusSPUtil.getInstance("test").getString("testKey", "")

        binding.mSpSaveBtn.onClick {
            MebiusSPUtil.getInstance("test")
                .putSyn("testKey", "SP存的值，持久化${System.currentTimeMillis()}")
        }

        binding.mSpGetBtn.onClick(1000L) {
            binding.mSpTv.text = MebiusSPUtil.getInstance("test").getString("testKey", "")
        }
    }
}