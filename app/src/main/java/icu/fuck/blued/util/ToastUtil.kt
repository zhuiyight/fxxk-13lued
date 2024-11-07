package icu.fuck.blued.util

import com.github.kyuubiran.ezxhelper.ClassUtils
import icu.fuck.blued.constant.ClassName.TOAST_UTILS

object ToastUtil {
    fun show(content: String, longTime: Boolean = false) {
        ClassUtils.invokeStaticMethodBestMatch(
            ClassUtils.loadClass(TOAST_UTILS), (if (longTime) "a" else "b"), null, content
        )
    }
}