package com.meshchat.watch

import android.app.Application
import com.meshchat.android.mesh.PowerManager
import com.meshchat.watch.notification.WearNotificationCoordinator
import com.meshchat.watch.ui.WearPeerIdentityState

class BitchatWatchApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PowerManager.getInstance(applicationContext)
        WearNotificationCoordinator.getInstance(applicationContext)
        WearPeerIdentityState.initialize(applicationContext)
    }
}
