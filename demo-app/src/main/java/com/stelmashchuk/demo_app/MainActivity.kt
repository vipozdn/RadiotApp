package com.stelmashchuk.demo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.stelmashchuk.remark.RemarkSettings
import com.stelmashchuk.remark.di.RemarkComponent
import com.stelmashchuk.remark.feature.root.RemarkView

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      RemarkView(postUrl = "https://remark42.com/demo/")
    }
    RemarkComponent.init(applicationContext, RemarkSettings("remark", "https://demo.remark42.com/"))
  }
}
