package icu.fuck.blued.hook

import android.content.ContextWrapper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder

object ApplicationHook : BaseHook() {
    override val name: String = "MainClassLoaderHook"

    override fun init() {
        MethodFinder.fromClass(ContextWrapper::class.java).filterByName("attachBaseContext").filterByParamCount(1)
            .first().createHook {
                after {
                    arrayOf(
                        ModuleInfoHook,
                        ADsHook,
                        BurnImageHook,
                        EventTrackHook,
                        RecallHook,
                        PornWarningHook,
                        RecallNotifyHook,
                        VipHook,
                        ).forEach { sub ->
                        runCatching {
                            if (!sub.isInit) {
                                sub.init()
                                sub.isInit = true
                                Log.ix("Sub hook initialized: ${sub.name}")
                            }
                        }.onFailure { err ->
                            when (err) {
                                is ClassNotFoundException -> {
                                    // ignored
                                }

                                else -> {
                                    Log.ex("Fail to initialize sub hook: ${sub.name}", err)
                                }
                            }
                        }
                    }
                }
            }
    }
}