package com.test.inventorymanagement.application


import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder
import com.google.android.gms.analytics.Tracker
import com.test.inventorymanagement.R


class InventoryManagementApplication : Application() {
    private var sAnalytics: GoogleAnalytics? = null
    private var sTracker: Tracker? = null

    init {
        instance = this
    }
    companion object {
        private var instance = InventoryManagementApplication()
        fun getInstance(): InventoryManagementApplication {
            return instance
        }
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        sAnalytics = GoogleAnalytics.getInstance(this)
    }

    /**
     * Gets the default [Tracker] for this [Application].
     * @return tracker
     */
    @Synchronized
    fun getDefaultTracker(context: Context): Tracker? { // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        sAnalytics = GoogleAnalytics.getInstance(context) // here pass your activity instance
        sAnalytics?.let {
            sTracker = it.newTracker(R.xml.global_tracker)
        }
        return sTracker
    }

    fun trackScreenView(screenName: String, context: Context) {
        Log.i("frag", "fragname $screenName")

        val screenTracker =
            getDefaultTracker(context)
        screenTracker?.setScreenName("Image~" + screenName)
        screenTracker?.send(ScreenViewBuilder().build())


        screenTracker?.setScreenName(screenName)
        screenTracker?.send(ScreenViewBuilder()
                .build()
        )
        GoogleAnalytics.getInstance(this).dispatchLocalHits()
    }

    fun trackEvent(category: String?, action: String?, label: String?, context: Context) {
        Log.i("frag", "eventname $category + $action")
        val eventTrackert = getDefaultTracker(context)
        eventTrackert ?. send(
            HitBuilders.EventBuilder()
                .setCategory(category).setAction(action).setLabel(label).build()
        )
    }

}