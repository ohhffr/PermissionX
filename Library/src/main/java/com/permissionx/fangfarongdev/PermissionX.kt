package com.permissionx.fangfarongdev

import androidx.fragment.app.FragmentActivity

object PermissionX {
    private const val TAG = "InvisibleFragment"
    //接收一个FragmentActivity参数、一个可变长度的permissions参数列表，以及一个callback回调。
    fun request(activity: FragmentActivity, vararg permissions: String, callback: PermissionCallback) {
        val fragmentManager = activity.supportFragmentManager//获取FragmentManager的实例
        val existedFragment = fragmentManager.findFragmentByTag(TAG) //判断传入的Activity参数中是否已经包含了指定TAG的Fragment
        val fragment = if (existedFragment != null) {
            existedFragment as InvisibleFragment//直接使用该Fragment
        }else{
            val invisibleFragment = InvisibleFragment()//创建一个新的InvisibleFragment实例
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()//并将它添加到Activity中，同时指定一个TAG
            invisibleFragment
        }

        fragment.requestNow(callback, *permissions)//*表示将一个数组转换成可变长度参数传递过去
    }
}