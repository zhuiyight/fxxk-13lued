package icu.fuck.blued.hook.ads

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import icu.fuck.blued.MainHook.Companion.hookVersion
import icu.fuck.blued.constant.ClassName.FLEX_DEBUG_SEV_CONFIG
import icu.fuck.blued.constant.ClassName.VISITOR_LIST_RECYCLE_VIEW_ADAPTER
import icu.fuck.blued.hook.BaseHook

object VisitorADsHook : BaseHook() {
    override val name: String = "VisitorADsHook"
    private var filteredList: List<*> = listOf(null)

    override fun init() {
        MethodFinder.fromClass(VISITOR_LIST_RECYCLE_VIEW_ADAPTER).filterByName("a")
            .filterByParamTypes(java.util.List::class.java, Int::class.java).first().createHook {
                before { hookParam ->
                    /* 在执行广告埋点方法前，筛选掉标记为1的元素 */
                    filteredList = (hookParam.args[0] as List<*>).filter { element ->
                        (element?.objectHelper()?.getObjectOrNullUntilSuperclassAs<Int>("is_ads")?.toInt() ?: 0) != 1
                    }
                }
                after { hookParam ->
                    /* 执行方法后的返回结果是空数组，应用之前过滤的结果 */
                    hookParam.thisObject?.objectHelper()?.let { helper ->
                        helper.invokeMethodBestMatch("setNewData", null, filteredList)/* 不调用则不会刷新列表 */
                        helper.invokeMethodBestMatch("notifyDataSetChanged", null)
                        Log.ix("Filtered ADs in visitor list")
                    }
                }
            }
    }
}
