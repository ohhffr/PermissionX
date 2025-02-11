package com.permissionx.fangfarongdev

import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

typealias PermissionCallback = (Boolean, List<String>) -> Unit

class InvisibleFragment : Fragment() {
    private lateinit var permissionViewModel: PermissionViewModel
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取 ViewModel 实例
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]

        // 注册权限请求启动器
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { handlePermissionResult(it) }
    }

    fun requestNow(callback: PermissionCallback, vararg permissions: String) {
        val unGranted = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (unGranted.isEmpty()) {
            callback(true, emptyList())
            return
        }

        // 将回调添加到队列
        permissionViewModel.callbackQueue.add(callback)

        // 启动权限请求
        requestPermissionLauncher.launch(unGranted.toTypedArray())
    }

    private fun handlePermissionResult(permissionsResult: Map<String, Boolean>) {
        val deniedList = permissionsResult.filter { !it.value }.keys.toMutableList()
        val allGranted = deniedList.isEmpty()

        val finalDeniedList = deniedList.filter {
            if (isAdded) !shouldShowRequestPermissionRationale(it) else true
        }

        // 获取并调用队列中的回调
        permissionViewModel.callbackQueue.poll()?.invoke(allGranted, finalDeniedList)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清理 ViewModel 中的回调队列
        permissionViewModel.callbackQueue.clear()
    }
}
