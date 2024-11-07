package icu.fuck.blued.hook

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers
import icu.fuck.blued.constant.ClassName.MSG_CHATTING_ADAPTER
import icu.fuck.blued.util.ToastUtil

/* 撤回提示拦截 */
object RecallNotifyHook : BaseHook() {
    override val name: String = "RecallHook"

    override fun init() {
        MethodFinder.fromClass(MSG_CHATTING_ADAPTER).filterByName("a").filterByParamTypes(
            Int::class.java, android.view.View::class.java, android.view.ViewGroup::class.java
        ).forEach { method ->
            method.createHook {
                after { hookParam ->
                    /* args[0]是消息索引，从整个消息记录取到对应的记录 */
                    val chattingModel = (XposedHelpers.getObjectField(
                        hookParam.thisObject, "a"
                    ) as List<*>)[(hookParam.args[0] as Int)];

                    /* 获取提示文本 */
                    (XposedHelpers.getAdditionalInstanceField(
                        chattingModel, "fuck_blued_notify"
                    ) as String?)?.let { notify ->
                        ToastUtil.show(notify, false)
                        val viewGroup = hookParam.result as ViewGroup;
                        viewGroup.findViewWithTag<View>(1)?.let { v ->
                            viewGroup.removeView(v)
                        }
                        viewGroup.addView(
                            makeNotifyTextView(
                                EzXHelper.appContext, notify
                            )
                        )
                        XposedHelpers.removeAdditionalInstanceField(chattingModel, "fuck_blued_notify")
                    }
                }
            }
        }
    }

    /* 生成提示文本 */
    private fun makeNotifyTextView(context: Context, str: String?): TextView {
        return TextView(context).apply {
            tag = 1
            textSize = 12f
            text = str
            setTextColor(Color.parseColor("#ADAFB0"))
            gravity = Gravity.CENTER
            setPadding(20, 0, 20, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }
}