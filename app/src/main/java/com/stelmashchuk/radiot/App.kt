package com.stelmashchuk.radiot

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
  }
}
