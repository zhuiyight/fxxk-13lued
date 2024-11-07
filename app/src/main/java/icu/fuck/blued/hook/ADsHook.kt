package icu.fuck.blued.hook

import androidx.collection.ArrayMap
import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import icu.fuck.blued.constant.ClassName.AD_DOWNLOAD_MANAGER
import icu.fuck.blued.constant.ClassName.AD_UTILS
import icu.fuck.blued.constant.ClassName.BLUED_UI_HTTP_RESPONSE
import icu.fuck.blued.constant.ClassName.FLEX_DEBUG_SEV_CONFIG
import icu.fuck.blued.constant.ClassName.I_REQUEST_HOST
import icu.fuck.blued.constant.ClassName.LOGIN_REGISTER_HTTP_UTILS
import icu.fuck.blued.hook.ads.TabADsHook
import icu.fuck.blued.hook.ads.MineADsHook
import icu.fuck.blued.hook.ads.VisitorADsHook

// 广告拦截
object ADsHook : BaseHook() {
    override val name: String = "ADsHook"

    private fun disableADsByConfig() {
        MethodFinder.fromClass(FLEX_DEBUG_SEV_CONFIG).filterByName("b").first().createHook {
            after { hookParam ->
                arrayOf(
                    "android_forbidden_splash_ad",
                    "android_forbidden_banner1_ad",
                    "android_forbidden_banner2_ad",
                    "android_forbidden_origin_ad"
                ).forEach { field ->
                    hookParam.result.objectHelper().setObject(field, 1)
                }
            }
        }
        MethodFinder.fromClass(AD_DOWNLOAD_MANAGER).filterByName("a").filterByParamCount(0).first()
            .createHook {
                interrupt()
            }
        // 禁止登录结果中处理广告SDK埋点信息
        // 调用点位于：com.soft.blued.ui.welcome.WelcomeFragment.a(SplashAdListener)的最后
        MethodFinder.fromClass(LOGIN_REGISTER_HTTP_UTILS).filterByName("a").filterByParamTypes(
            android.content.Context::class.java,
            String::class.java,
            ClassUtils.loadClass(BLUED_UI_HTTP_RESPONSE),
            String::class.java,
            ClassUtils.loadClass(I_REQUEST_HOST)
        ).first().createHook {
            before {
                // HttpResponseHandler<?> httpResponseHandler
                it.args[2] = null
                // IRequestHost iRequestHost
                it.args[4] = null
            }
        }
        // 可能是禁止广告SDK初始化
        MethodFinder.fromClass(AD_UTILS).filterByName("a")
            .filterByParamTypes(java.util.Map::class.java, String::class.java, Boolean::class.java).first().createHook {
                replace {
                    return@replace ArrayMap<String, String>()
                }
            }
    }

    override fun init() {
        this.disableADsByConfig()
        arrayOf(TabADsHook, MineADsHook, VisitorADsHook).forEach { sub ->
            runCatching {
                if (!sub.isInit) {
                    sub.init()
                    sub.isInit = true
                    Log.ix("Enable ADs hook: ${sub.name}")
                }
            }.onFailure { err ->
                when (err) {
                    is ClassNotFoundException -> {
                        // ignored
                    }

                    else -> {
                        Log.ex("Fail to initialize ADs hook: ${sub.name}", err)
                    }
                }
            }
        }
    }
}
