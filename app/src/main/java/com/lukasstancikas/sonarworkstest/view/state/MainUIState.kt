package com.lukasstancikas.sonarworkstest.view.state

import com.lukasstancikas.sonarworkstest.model.User

data class MainUIState(
    val currentWebUser: User? = null,
    val currentNativeUser: User? = null,
    val webUserSubmitCount: Int = 0,
    val nativeUserSubmitCount: Int = 0
)