package com.lukasstancikas.sonarworkstest.bridge

import android.webkit.JavascriptInterface
import androidx.lifecycle.LiveData
import com.lukasstancikas.sonarworkstest.model.User
import io.reactivex.rxjava3.core.Observable

interface WebBridge {

    val componentName: String

    val htmlFilePath: String

    fun getUserStream() : Observable<User>

    fun submitNativeUserEvaluation(user: User): String

    @JavascriptInterface
    fun onUserSubmitFromWeb(userJson: String)
}