package com.example.security_detectors.detectors


import android.content.Context
import android.os.Build
import android.os.Debug
import android.provider.Settings
import java.io.File

object SecurityDetectors {

    /** Détecteur de débogueur */
    fun isDebuggerConnected(): Boolean {
        return Debug.isDebuggerConnected()
    }

    /** Détecteur de mode développeur */
    fun isDeveloperModeEnabled(context: Context): Boolean {
        return try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED) == 1
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }

    /** Détecteur de root */
    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    /** Détecteur d'émulateur */
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.lowercase().contains("vbox")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE == "goldfish"
                || Build.HARDWARE == "ranchu"
                || Build.BOARD.lowercase().contains("nox")
                || Build.BOOTLOADER.lowercase().contains("nox")
                || Build.PRODUCT.lowercase().contains("sdk")
                || Build.PRODUCT.lowercase().contains("google_sdk")
                )
    }
}
