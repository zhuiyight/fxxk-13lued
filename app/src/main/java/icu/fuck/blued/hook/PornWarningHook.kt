package icu.fuck.blued.hook

import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import icu.fuck.blued.constant.ClassName.MSG_CHATTING_PRESENT
import java.lang.reflect.Modifier

/* 拦截黄图发送警告 */
object PornWarningHook : BaseHook() {
    override val name: String = "PornWarningHook"

    override fun init() {
        MethodFinder.fromClass(MSG_CHATTING_PRESENT).filterByName("a").filterByParamCount(7)
            .filterByModifiers(Modifier.PRIVATE).first().createHook {
                before { hookParam ->
                    Log.ix("Block warning dialog: [${(hookParam.args[1] as Array<*>).joinToString(", ")}]")
                    hookParam.args[1] = null
                }
            }
    }
}