package icu.fuck.blued.hook

import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.robv.android.xposed.XposedHelpers
import icu.fuck.blued.constant.ClassName.CHAT_HELPER_V4
import icu.fuck.blued.constant.ClassName.MSG_CHATTING_PRESENT

object RecallHook : BaseHook() {
    override val name: String = "RecallHook"

    override fun init() {
        with(ClassUtils.loadClass(CHAT_HELPER_V4)) {
            val chatHelperV4 = ClassUtils.invokeStaticMethodBestMatch(this, "a", this)?.objectHelper()
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
                                            val originalMsgContent = chatModel.getObjectOrNullAs<String?>("msgContent")

                                            val msgExtra: Map<String, Any> = Gson().fromJson(
                                                chatModel.getObjectOrNullAs<String?>("msgExtra"),
                                                object : TypeToken<Map<String, Any>>() {}.type
                                            )

                                            if (!originalMsgContent?.isEmpty()!!) {
                                                msgTip = "对方尝试撤回这条消息"
                                                // 撤回的是iOS闪照
                                                if (originalMsgContent.startsWith("RU") && originalMsgContent.length > 10) {
                                                    newMsgType = 2
                                                    ctor = "a"
                                                } else {
                                                    newMsgType = when {
                                                        // 有图片宽度字段，则是图片消息
                                                        msgExtra["pic_width"] !== null -> 2
                                                        // 有视频宽度字段，则是视频消息
                                                        msgExtra["video_width"] !== null -> 5
                                                        // 其他类型的消息，当做文本消息处理
                                                        else -> 1
                                                    }
                                                }
                                            }
                                        }

                                        else ->/* 其他消息不作处理 */
                                            return@forEach
                                    }
                                    if (ctor !== null) {
                                        chatHelperV4!!.invokeMethodBestMatch(ctor, String::class.java, message)
                                            .let { msgContent ->
                                                chatModel.setObjectUntilSuperclass("msgContent", msgContent)
                                            }
                                    }
                                    // 这句话千万不能移到上面去，提前设置了msgType会导致解密被跳过，iOS发来的闪照就会解析失败
                                    chatModel.setObjectUntilSuperclass("msgType", newMsgType.toShort())
                                    XposedHelpers.setAdditionalInstanceField(message, "fuck_blued_notify", msgTip)
                                }
                            }
                        }
                    }
                }
        }
    }
}