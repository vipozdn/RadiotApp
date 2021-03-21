package com.example.radio_t

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.radio_t.presentation.App


class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      App()
    }
    com.example.remark.di.Graph.init(applicationContext)
  }

  override fun onResume() {
    super.onResume()
    Log.e("TAG_11", "on new intent ${intent?.data}")
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    Log.e("TAG_11", "on new intent ${intent?.data}")
  }
}
