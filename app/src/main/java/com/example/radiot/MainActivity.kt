package com.example.radiot

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.radiot.presentation.App
import com.example.remark.di.Graph as RemarkGraph


class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      App()
    }
    RemarkGraph.init(applicationContext)
  }
}
