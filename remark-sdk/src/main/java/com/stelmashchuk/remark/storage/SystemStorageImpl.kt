package com.stelmashchuk.remark.storage

import android.content.SharedPreferences
import com.stelmashchuk.remark.api.SystemStorage

internal class SystemStorageImpl(private val sharedPreferences: SharedPreferences) : SystemStorage {
  override fun putString(key: String, value: String) {
    putStrings(mapOf(key to value))
  }

  override fun putStrings(values: Map<String, String>) {
    val editor = sharedPreferences.edit()
    values.forEach { (key, value) ->
      editor.putString(key, value)
    }
    editor.apply()

  }

  override fun getString(key: String): String {
    return sharedPreferences.getString(key, "") ?: ""
  }

  override fun onValueChanges(onChange: () -> Unit) {
    sharedPreferences.registerOnSharedPreferenceChangeListener { _, _ ->
      onChange()
    }
  }
}