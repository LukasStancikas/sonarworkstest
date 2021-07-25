package com.lukasstancikas.sonarworkstest.view.event

import com.lukasstancikas.sonarworkstest.model.User

sealed class MainUIEvent {
    object Init: MainUIEvent()
    data class NativeUserSubmitted(val user: User): MainUIEvent()
    data class WebUserReceived(val user: User): MainUIEvent()
}