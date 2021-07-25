package com.lukasstancikas.sonarworkstest.viewmodel

import androidx.lifecycle.ViewModel
import com.lukasstancikas.sonarworkstest.bridge.WebBridge
import com.lukasstancikas.sonarworkstest.model.User
import com.lukasstancikas.sonarworkstest.view.effect.MainUIEffect
import com.lukasstancikas.sonarworkstest.view.effect.MainUIEffect.*
import com.lukasstancikas.sonarworkstest.view.event.MainUIEvent
import com.lukasstancikas.sonarworkstest.view.state.MainUIState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber

class MainViewModel(private val webBridge: WebBridge) : ViewModel() {
    private val _uiStates = BehaviorSubject.createDefault(MainUIState())
    private val _uiEffects = PublishSubject.create<MainUIEffect>()

    val uiStates: Observable<MainUIState> = _uiStates.hide()
    val uiEffects: Observable<MainUIEffect> = _uiEffects.hide()

    private val compositeDisposable = CompositeDisposable()

    init {
        subscribeToWebBridge()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun dispatch(event: MainUIEvent) = when (event) {
        is MainUIEvent.NativeUserSubmitted -> onNativeUserSubmitted(event.user)
        is MainUIEvent.WebUserReceived -> onWebUserReceived(event.user)
        MainUIEvent.Init -> loadWebPage()
    }


    private fun loadWebPage() {
        _uiEffects.onNext(LoadUrl(webBridge.htmlFilePath, webBridge))
    }

    private fun onWebUserReceived(user: User) {
        if (user != _uiStates.value.currentWebUser) {
            val newCount = _uiStates.value.webUserSubmitCount + 1
            _uiStates.onNext(
                _uiStates.value.copy(
                    currentWebUser = user,
                    webUserSubmitCount = newCount
                )
            )
        } else {
            _uiEffects.onNext(SameWebUserMessage)
        }
    }

    private fun subscribeToWebBridge() {
        webBridge.getWebUserStream()
            .subscribeBy(
                onNext = { dispatch(MainUIEvent.WebUserReceived(it)) },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    private fun onNativeUserSubmitted(user: User) {
        if (user != _uiStates.value.currentNativeUser) {
            val newCount = _uiStates.value.nativeUserSubmitCount + 1
            _uiStates.onNext(
                _uiStates.value.copy(
                    currentNativeUser = user,
                    nativeUserSubmitCount = newCount
                )
            )
            _uiEffects.onNext(EvaluateJavascript(webBridge.getSubmitNativeUserEvaluation(user)))
        } else {
            _uiEffects.onNext(SameNativeUserMessage)
        }
    }
}