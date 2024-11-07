package icu.fuck.blued

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import icu.fuck.blued.hook.ApplicationHook

private const val PACKAGE_NAME_HOOKED = "com.soft.blued"
private const val TAG = "Fuck-Blued"

class MainHook : IXposedHookLoadPackage {
    companion object {
        val hookVersion: String = "v1.0.0-alpha"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == PACKAGE_NAME_HOOKED) {
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
            EzXHelper.setToastTag(TAG)
            runCatching {
                Log.i("Begin fuck Blued...")
                ApplicationHook.init()
                ApplicationHook.isInit = true
            }.logexIfThrow("Cannot fuck Blued")
        }
    }
}