package com.lukasstancikas.sonarworkstest.view.effect

import com.lukasstancikas.sonarworkstest.bridge.UserJavascriptInterface

sealed class MainUIEffect {
    object SameNativeUserMessage: MainUIEffect()
    object SameWebUserMessage: MainUIEffect()
    data class EvaluateJavascript(val evaluation: String): MainUIEffect()
    data class LoadUrl(val url: String, val jsInterface: UserJavascriptInterface): MainUIEffect()
}