package com.lukasstancikas.sonarworkstest.bridge

import com.lukasstancikas.sonarworkstest.model.User
import io.reactivex.rxjava3.core.Observable

interface WebBridge:  UserJavascriptInterface {
    val htmlFilePath: String

    fun getWebUserStream(): Observable<User>

    fun getSubmitNativeUserEvaluation(user: User): String
}