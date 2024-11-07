package icu.fuck.blued.hook

import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers
import icu.fuck.blued.constant.ClassName.CHAT_HELPER_V4
import icu.fuck.blued.constant.ClassName.MSG_CHATTING_PRESENT

object RecallHook : BaseHook() {
    override val name: String = "RecallHook"

    override fun init() {
        with(ClassUtils.loadClass(CHAT_HELPER_V4)) {
            val chatHelperV4Instance = ClassUtils.invokeStaticMethodBestMatch(this, "a", this)
            MethodFinder.fromClass(MSG_CHATTING_PRESENT).filterByName("onMsgDataChanged")
                .filterByParamTypes(java.util.List::class.java).first().createHook {
                    before { hookParam ->
                        /* 遍历每一条消息 */
                        (hookParam.args[0]!! as List<*>).forEach { message ->
                            message?.objectHelper()?.let { chatModel ->
                                val isFromSelf =
                                    chatModel.invokeMethodBestMatch("isFromSelf", Boolean::class.java) as Boolean
                                val msgType = chatModel.getObjectOrNullUntilSuperclassAs<Short>("msgType")!!.toInt()

                                if (!isFromSelf) {
                                    var ctor: String? = null
                                    var newMsgType: Int = -1
                                    var msgTip = ""
                                    val originalMsgContent = chatModel.getObjectOrNullAs<String?>("msgContent")

                                    chatHelperV4Instance?.objectHelper()?.let { chatHelper ->
                                        when (msgType) {
                                            24 -> {/* 闪照 */
                                                ctor = "a"
                                                newMsgType = 2
                                                msgTip = "已转换闪照消息"
                                            }

                                            25 -> {/* 闪拍 */
                                                ctor = "b"
                                                newMsgType = 5
                                                msgTip = "已转换闪拍消息"
                                            }

                                            55 -> {/* 撤回 */
                                                if (!originalMsgContent?.isEmpty()!!) {
                                                    newMsgType = 1
                                                    msgTip = "对方尝试撤回这条消息"
                                                }
                                            }

                                            else ->/* 其他消息不作处理 */
                                                return@forEach
                                        }
                                        chatModel.setObjectUntilSuperclass("msgType", newMsgType.toShort())
                                        ctor?.let { ctor ->
                                            chatHelper.invokeMethodBestMatch(ctor, String::class.java, message)
                                                .let { msgContent ->
                                                    chatModel.setObjectUntilSuperclass("msgContent", msgContent)
                                                }
                                        }
                                        XposedHelpers.setAdditionalInstanceField(message, "fuck_blued_notify", msgTip)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}