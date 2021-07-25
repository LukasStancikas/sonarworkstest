package com.lukasstancikas.sonarworkstest.bridge

import android.webkit.JavascriptInterface
import androidx.lifecycle.LiveData
import com.lukasstancikas.sonarworkstest.model.User
import io.reactivex.rxjava3.core.Observable

interface WebBridge {

    val nativeComponentName: String

    val htmlFilePath: String

    fun getWebUserStream() : Observable<User>

    fun getSubmitNativeUserEvaluation(user: User): String

    @JavascriptInterface
    fun onUserSubmitFromWeb(userJson: String)
}