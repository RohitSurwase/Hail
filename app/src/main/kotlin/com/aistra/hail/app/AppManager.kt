package com.aistra.hail.app

import android.content.Intent
import com.aistra.hail.BuildConfig
import com.aistra.hail.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppManager {
    val lockScreen: Boolean
        get() = when {
            HailData.workingMode.startsWith(HailData.OWNER) -> HPolicy.lockScreen
            else -> false
        }

    fun isAppFrozen(packageName: String): Boolean = when {
        HailData.workingMode.endsWith(HailData.STOP) -> HPackages.isAppStopped(packageName)
        HailData.workingMode.endsWith(HailData.DISABLE) -> HPackages.isAppDisabled(packageName)
        HailData.workingMode.endsWith(HailData.HIDE) -> HPackages.isAppHidden(packageName)
        HailData.workingMode.endsWith(HailData.SUSPEND) -> HPackages.isAppSuspended(packageName)
        else -> HPackages.isAppDisabled(packageName)
                || HPackages.isAppHidden(packageName)
                || HPackages.isAppSuspended(packageName)
    }

    fun setListFrozen(frozen: Boolean, vararg appInfo: AppInfo): String? {
        val excludeMe = appInfo.filter { it.packageName != BuildConfig.APPLICATION_ID }
        var i = 0
        var denied = false
        var name = String()
        when (HailData.workingMode) {
            // call setListFrozen for some batch-style working mode here
            // fallback to setAppFrozen otherwise
            else -> {
                excludeMe.forEach {
                    when {
                        setAppFrozen(it.packageName, frozen) -> {
                            i++
                            name = it.name.toString()
                        }

                        it.applicationInfo != null -> denied = true
                    }
                }
            }
        }
        return if (denied && i == 0) null else if (i == 1) name else i.toString()
    }

    fun setAppFrozen(packageName: String, frozen: Boolean): Boolean =
        packageName != BuildConfig.APPLICATION_ID && when (HailData.workingMode) {
            HailData.MODE_OWNER_HIDE -> HPolicy.setAppHidden(packageName, frozen)
            HailData.MODE_OWNER_SUSPEND -> HPolicy.setAppSuspended(packageName, frozen)
            else -> false
        }

    fun uninstallApp(packageName: String): Boolean {
        when {
            HailData.workingMode.startsWith(HailData.OWNER) ->
                if (HPolicy.uninstallApp(packageName)) return true
        }
        HUI.startActivity(Intent.ACTION_DELETE, HPackages.packageUri(packageName))
        return false
    }

    suspend fun execute(command: String): Pair<Int, String?> = withContext(Dispatchers.IO) {
        when {
            else -> 0 to null
        }
    }
}