package com.permissionx.fangfarongdev

import androidx.lifecycle.ViewModel
import java.util.LinkedList

class PermissionViewModel : ViewModel() {
    val callbackQueue = LinkedList<PermissionCallback>()
}