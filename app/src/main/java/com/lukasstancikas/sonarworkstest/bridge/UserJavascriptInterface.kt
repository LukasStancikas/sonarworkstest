package com.lukasstancikas.sonarworkstest.bridge

import android.webkit.JavascriptInterface
import androidx.lifecycle.LiveData
import com.lukasstancikas.sonarworkstest.model.User
import io.reactivex.rxjava3.core.Observable

interface UserJavascriptInterface {

    val nativeComponentName: String

    @JavascriptInterface
    fun onUserSubmitFromWeb(userJson: String)
}