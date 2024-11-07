package icu.fuck.blued.hook

import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import icu.fuck.blued.constant.ClassName.STATISTIC_BIZ_CLIENT

object EventTrackHook : BaseHook() {
    override val name: String = "EventTrackHook"

    override fun init() {
        MethodFinder.fromClass(STATISTIC_BIZ_CLIENT)
            .filterByName("a")
            .filterByParamTypes(
                ClassUtils.loadClass("com.google.protobuf.Message"),
                Long::class.java
            ).first().createHook {
                before { hookParam ->
                    Log.i("Block event track, original message: ${hookParam.args[0]}")
                    hookParam.args[0] = null
                }
            }
    }
}