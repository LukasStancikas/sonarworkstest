package com.lukasstancikas.sonarworkstest.view.effect

sealed class MainUIEffect {
    object SameNativeUserMessage: MainUIEffect()
    object SameWebUserMessage: MainUIEffect()
}