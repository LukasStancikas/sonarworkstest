package com.lukasstancikas.sonarworkstest.bridge.android

import android.webkit.JavascriptInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lukasstancikas.sonarworkstest.bridge.WebBridge
import com.lukasstancikas.sonarworkstest.model.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.reactivestreams.Publisher

class WebBridgeImpl : WebBridge {
    private val _userStream = BehaviorSubject.create<User>()

    override val componentName = "NativeComponent"

    override val htmlFilePath: String = "file:///android_res/raw/sonarwork_page.html"

    override fun getUserStream(): Observable<User> = _userStream.hide()

    override fun submitNativeUserEvaluation(user: User): String {
        return "javascript: " + "nativeUserSubmit(\'" + Json.encodeToString(user) +
                "\')"
    }
    @JavascriptInterface
    override fun onUserSubmitFromWeb(userJson: String) {
        val webPlayer = Json.decodeFromString<User>(userJson)
        _userStream.onNext(webPlayer)
    }
}