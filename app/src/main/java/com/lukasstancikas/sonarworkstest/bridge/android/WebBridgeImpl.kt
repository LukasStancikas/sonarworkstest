package com.lukasstancikas.sonarworkstest.bridge.android

import android.webkit.JavascriptInterface
import com.lukasstancikas.sonarworkstest.bridge.WebBridge
import com.lukasstancikas.sonarworkstest.model.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WebBridgeImpl : WebBridge {
    private val _userStream = BehaviorSubject.create<User>()

    override val nativeComponentName = "NativeComponent"

    override val htmlFilePath: String = "file:///android_res/raw/sonarwork_page.html"

    override fun getWebUserStream(): Observable<User> = _userStream.hide()

    override fun getSubmitNativeUserEvaluation(user: User): String {
        return "javascript: " + "nativeUserSubmit(\'" + Json.encodeToString(user) +
                "\')"
    }

    @JavascriptInterface
    override fun onUserSubmitFromWeb(userJson: String) {
        val webPlayer = Json.decodeFromString<User>(userJson)
        _userStream.onNext(webPlayer)
    }
}