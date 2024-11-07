package icu.fuck.blued.hook

import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import icu.fuck.blued.constant.ClassName.FLASH_NUMBER_MODEL
import icu.fuck.blued.constant.ClassName.FLASH_PHOTO_MANAGER

object BurnImageHook : BaseHook() {
    override val name: String = "BurnImageHook"

    override fun init() {/* 拦截所有获取闪照数据的方法 */
        MethodFinder.fromClass(FLASH_PHOTO_MANAGER).filterByReturnType(ClassUtils.loadClass(FLASH_NUMBER_MODEL))
            .forEach { method ->
                method.createHook {
                    after { hookParam ->
                        hookParam.result?.objectHelper()?.let { helper ->
                            mapOf(
                                "flash_left_times" to 99,
                                "stimulate_flash" to 0,
                                "is_vip" to 1,
                                "flash_prompt" to "(不限次数)"
                            ).forEach { kv ->
                                helper.setObject(kv.key, kv.value)
                            }
                            Log.ix("BurnImage Hooked")
                        }
                    }
                }
            }

        /* 直接对服务器上传请求下手，把闪照flag改成false即可强制上传成功，接收端如为Android则没有任何问题，如为iOS则无法正常查看 */
        MethodFinder.fromClass("com.soft.blued.http.ChatHttpUtils").filterByName("a").filterByParamTypes(
            ClassUtils.loadClass("com.blued.android.chat.model.ChattingModel")
        ).first().createHook {
            after {
                it.result?.let { str -> it.result = (str as String).replace("isBurn=1", "isBurn=0") }
            }
        }
    }
}