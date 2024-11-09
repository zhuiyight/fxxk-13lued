package icu.fuck.blued.hook.ads

import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import icu.fuck.blued.constant.ClassName.VISITOR_LIST_ADAPTER
import icu.fuck.blued.constant.ClassName.VISITOR_LIST_RECYCLE_VIEW_ADAPTER
import icu.fuck.blued.hook.BaseHook

object VisitorADsHook : BaseHook() {
    override val name: String = "VisitorADsHook"
    private var filteredList = HashMap<String, List<*>>()

    override fun init() {/* 处理列表数据 */
        arrayOf(VISITOR_LIST_ADAPTER, VISITOR_LIST_RECYCLE_VIEW_ADAPTER).forEach { adapterClass ->
            MethodFinder.fromClass(adapterClass).filterByName("a")
                .filterByParamTypes(java.util.List::class.java, Int::class.java).first().createHook {
                    before { hookParam ->
                        // 在执行广告埋点方法前，筛选掉标记为1的元素
                        filteredList[adapterClass] = (hookParam.args[0] as List<*>).filter { element ->
                            (element?.objectHelper()?.getObjectOrNullUntilSuperclassAs<Int>("is_ads")?.toInt()
                                ?: 0) != 1
                        }
                    }
                    after { hookParam ->
                        // 执行方法后的返回结果是空数组，应用之前过滤的结果
                        hookParam.thisObject?.objectHelper()?.let { helper ->
                            when (adapterClass) {
                                // “查看”没有单独的设置列表方法，直接设置字段
                                VISITOR_LIST_ADAPTER -> helper.setObject("j", filteredList[adapterClass])
                                else -> helper.invokeMethodBestMatch(
                                    "setNewData", null, filteredList[adapterClass]
                                )
                            }
                            // 刷新列表展示
                            helper.invokeMethodBestMatch("notifyDataSetChanged", null)
                            Log.ix("Filtered ADs in visitor list")
                        }
                    }
                }
        }
    }
}
