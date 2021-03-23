package com.stelmashchuk.remark.feature.auth.ui

import com.stelmashchuk.remark.data.repositories.RemarkCredentials

class CredentialCreator {

    private data class CookiesItem(
        val key: String,
        val value: String
    )

    fun tryCreate(cookies: String): RemarkCredentials? {
        val items = cookies.split(';')
            .map { item ->
                val key = item.substringBefore('=').trim()
                val value = item.substringAfter('=').trim()
                CookiesItem(key, value)
            }

        print(items)
        val jwt = items.find { it.key == "JWT" }?.value
        val xsrf = items.find { it.key == "XSRF-TOKEN" }?.value

        return if (jwt != null && xsrf != null) {
            RemarkCredentials(jwt, xsrf)
        } else {
            null
        }
    }
}
