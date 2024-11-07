package icu.fuck.blued.hook.ads

import android.view.View
import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import icu.fuck.blued.constant.ClassName
import icu.fuck.blued.constant.ClassName.MINE_PAGE_MODEL
import icu.fuck.blued.hook.BaseHook

/* “我的”页面广告清理 */
object MineADsHook : BaseHook() {
    override val name: String = "MineADsHook"

    /* 清理视图 */
    private fun hideViews(hookParam: MethodHookParam) {
        hookParam.thisObject.objectHelper().let { mineNewFragmentInstance ->
            val fragmentView = mineNewFragmentInstance.invokeMethodBestMatch("getView", View::class.java) as View
            arrayOf("ll_beans", "ll_live", "ll_yy", "ll_other").forEach { field ->
                fragmentView.findViewByIdName(field)?.visibility = View.GONE
            }
        }
    }

    /* 阻止部分视图初始化 */
    override fun init() {
        MethodFinder.fromClass(ClassName.MINE_NEW_FRAGMENT).filterByName("a").filterByParamTypes(
            ClassUtils.loadClass(MINE_PAGE_MODEL)
        ).first().createHook {
            before { hookParam ->
                hookParam.args[0]?.objectHelper()?.let { model ->
                    arrayOf(
                        "anchor",
                        "banner",
                        "emotions",
                        "healthy",
                        "healthy_ad",
                        "healthy_banner",
                        "service",
                        "vip_broadcast"
                    ).forEach {
                        model.setObject(it, null)
                    }
                }
                hideViews(hookParam)
            }
            after { hideViews(hookParam = it) }
        }
    }
}
