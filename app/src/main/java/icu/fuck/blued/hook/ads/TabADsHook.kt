package icu.fuck.blued.hook.ads

import android.app.Activity
import android.view.View
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import icu.fuck.blued.constant.ClassName
import icu.fuck.blued.hook.BaseHook

object TabADsHook : BaseHook() {
    override val name: String = "VisitorADsHook"

    override fun init() {
        MethodFinder.fromClass(ClassName.HOME_ACTIVITY).filterByName("a")
            .filterByParamTypes(android.content.Intent::class.java).first().createHook {
                after { hookParam ->
                    hookParam.thisObject.let {
                        (it as Activity).findViewByIdName("live_badge_container")!!.visibility = View.GONE
                    }
                }
            }
    }
}
