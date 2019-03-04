package com.example.aaron.recipeassistant.common.audiocontroller

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

private const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1

class PermissionsHelper(private val activity: Activity) {

    fun requestRecordPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.RECORD_AUDIO
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        }
    }
}