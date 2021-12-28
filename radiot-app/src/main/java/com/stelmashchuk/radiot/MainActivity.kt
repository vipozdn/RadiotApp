package com.stelmashchuk.radiot

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.stelmashchuk.radiot.presentation.App
import com.stelmashchuk.remark.api.RemarkSettings
import com.stelmashchuk.remark.di.RemarkComponent

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      App()
    }
    RemarkComponent.init(applicationContext, com.stelmashchuk.remark.api.RemarkSettings("radiot", "https://remark42.radio-t.com/"))
  }
}
