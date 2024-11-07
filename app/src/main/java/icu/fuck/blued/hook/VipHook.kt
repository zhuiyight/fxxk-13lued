package icu.fuck.blued.hook

import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import icu.fuck.blued.constant.ClassName.LOGIN_RESULT
import icu.fuck.blued.constant.ClassName.USER_INFO

/* 本地会员拦截 */
object VipHook : BaseHook() {
    override val name: String = "VipHook"

    private fun hookFields(helper: ObjectHelper) {
        helper.setObject("vip_grade", 2)
        helper.setObject("is_vip_annual", 1)
    }

    override fun init() {
        MethodFinder.fromClass(USER_INFO).filterByReturnType(ClassUtils.loadClass(LOGIN_RESULT)).forEach { method ->
            method.createHook {
                after { hookParam ->
                    hookParam.result?.objectHelper()?.let {
                        hookFields(helper = it)
                    }
                }
            }
        }
    }
}
