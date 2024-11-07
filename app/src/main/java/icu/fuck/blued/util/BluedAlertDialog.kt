package icu.fuck.blued.util

import android.app.Dialog
import android.content.Context
import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder

class BluedAlertDialog(context: Context) {
    private var builder: Any? = null;

    init {
        builder = ConstructorFinder.fromClass("com.blued.android.module.common.widget.dialog.BluedAlertDialog\$Builder")
            .filterByParamTypes(android.content.Context::class.java).first().newInstance(context)
    }

    private fun getDialog(): Dialog? {
        return builder?.objectHelper()?.invokeMethodBestMatch(
            "a", ClassUtils.loadClass("com.blued.android.module.common.widget.dialog.BluedAlertDialog")
        ) as Dialog?
    }

    fun show() {
        getDialog()?.show()
    }
}