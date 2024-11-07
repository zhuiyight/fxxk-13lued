package icu.fuck.blued.hook

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import icu.fuck.blued.MainHook.Companion.hookVersion
import icu.fuck.blued.constant.ClassName
import icu.fuck.blued.constant.ClassName.MINE_PAGE_MODEL
import icu.fuck.blued.util.ToastUtil
import icu.fuck.blued.util.ViewUtil.enumerateChildViews

object ModuleInfoHook : BaseHook() {
    override val name: String = "ModuleInfoHook"

    private fun cleanService() {
        MethodFinder.fromClass(ClassName.MINE_NEW_FRAGMENT).filterByName("a").filterByParamTypes(
            ClassUtils.loadClass(MINE_PAGE_MODEL)
        ).first().createHook {
            after { hookParam ->
                hookParam.thisObject.objectHelper().let { mineNewFragmentInstance ->
                    val bindingInstance = mineNewFragmentInstance.invokeMethodBestMatch("p")
                    val fragmentView =
                        mineNewFragmentInstance.invokeMethodBestMatch("getView", View::class.java) as View
                    bindingInstance?.objectHelper()?.let { binding ->
                        (binding.getObjectOrNullAs<TextView>("aI"))?.let { btn ->
                            btn.text = hookVersion
                            (fragmentView.findViewByIdName("layout_vip"))?.let { vipBlock ->
                                val views = enumerateChildViews((vipBlock as ViewGroup))
                                for (i in views.indices) {
                                    views[i].let {
                                        /* 屏蔽所有子视图的点击事件，确保点击VIP栏目后可以弹出窗口 */
                                        it.isClickable = false
                                        it.isLongClickable = false
                                    }
                                }
                                vipBlock.setOnClickListener {
                                    ToastUtil.show("仅供学习交流使用\n严禁非法商业用途\nBy:CoolBreeze", true)
                                }
                            }
                        }
                        binding.getObjectOrNullAs<TextView>("aJ")!!.text = "FuckBlued已成功启用"
                    }
                }

            }
        }
    }

    override fun init() {
        this.cleanService()
    }
}
