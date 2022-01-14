package com.graveno.alphalab.app.codedemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.util.Log

object AppCloning {

    fun checkAppCloning(activity: Activity) : Boolean {
        val path: String = activity.filesDir.path
        Log.e(TAG,"found path as $path")
        return when {
            //test 1 : checking package and dir path...
            path.contains(DUAL_APP_ID_999) -> {
                Log.e(TAG,"found cloned app...")
                false
            }
            else -> {
                val count: Int = getDotCount(path)
                when {
                    count > APP_PACKAGE_DOT_COUNT -> {
                        false
                    }
                    else -> {
                        //test 2 : checking package name...
                        Log.e(TAG,"Pack as ${activity.packageName}")
                        when {
                            activity.packageName != APP_PACKAGE -> false
                            else -> {
                                //test 3 : checking signature of app... final nail to the coffin
                                checkCertificate(activity = activity)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getDotCount(path: String): Int {
        var count = 0
        for (element in path) {
            if (count > APP_PACKAGE_DOT_COUNT) {
                break
            }
            if (element == DOT) {
                count++
            }
        }
        return count
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun getCertificateValue(activity: Activity): Int {
        try {
            var signatures: Array<Signature?>? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    signatures = activity.packageManager.getPackageInfo(
                        activity.packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    ).signingInfo.apkContentsSigners
                } catch (ignored: Throwable) {
                }
            }
            if (signatures == null) {
                signatures = activity.packageManager
                    .getPackageInfo(activity.packageName, PackageManager.GET_SIGNATURES).signatures
            }
            return when {
                signatures?.isNotEmpty() == true -> {
                    var temp = 1
                    signatures.forEach { signature ->
                        temp *= signature.hashCode()
                    }
                    temp
                }
                else -> 1
            }
        } catch (e: Exception) {
            Log.e(TAG, "error occurred as ${e.localizedMessage}")
            e.printStackTrace()
        }
        return 0
    }

    private fun checkCertificate(activity : Activity): Boolean {
        return when(getCertificateValue(activity)) {
            trustedValue -> {
                Log.e(TAG,"success, still in coffin...")
                true
            }
            else -> {
                Log.e(TAG,"fail, the space pried it open...")
                false
            }
        }
    }

    fun killProcess(activity: Activity) {
        Log.e(TAG, "Finishing activity, killing space, once forever...")
        activity.finish()
        android.os.Process.killProcess( android.os.Process.myPid())
    }

    private const val trustedValue: Int = -721979263
    private const val TAG : String = "AppCloning"
    private const val APP_PACKAGE_DOT_COUNT = 4 // number of dots present in package name
    private const val DUAL_APP_ID_999 = "999"
    private const val DOT = '.'
    private const val APP_PACKAGE = "com.graveno.alphalab.app.codedemo"
}