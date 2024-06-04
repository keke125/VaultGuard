package com.keke125.vaultguard.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import com.keke125.vaultguard.MainActivity

class VaultGuardTileService : TileService() {

    override fun onClick() {
        super.onClick()
        launchVaultGuard()
    }

    private fun launchVaultGuard() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 400, intent, PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(pendingIntent)
        } else {
            startActivityAndCollapse(intent)
        }

    }
}