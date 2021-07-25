package com.lukasstancikas.sonarworkstest.viewmodel

import com.lukasstancikas.sonarworkstest.RxImmediateSchedulerRule
import com.lukasstancikas.sonarworkstest.bridge.WebBridge
import com.lukasstancikas.sonarworkstest.model.User
import com.lukasstancikas.sonarworkstest.view.effect.MainUIEffect
import com.lukasstancikas.sonarworkstest.view.event.MainUIEvent
import com.lukasstancikas.sonarworkstest.view.state.MainUIState
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

class MainViewModelTest {

    companion object {
        private const val MOCK_URL = "www.mock.com"
        private const val MOCK_EVALUATION = "javascript: mock()"
        private const val MOCK_COMPONENT_NAME = "MockNativeComponent"
    }

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    private val webBridge: WebBridge = mock()
    private lateinit var viewModel: MainViewModel
    private val mockWebUserStream = BehaviorSubject.create<User>()

    @Before
    fun setup() {
        whenever(webBridge.nativeComponentName).thenReturn(MOCK_COMPONENT_NAME)
        whenever(webBridge.htmlFilePath).thenReturn(MOCK_URL)
        whenever(webBridge.getSubmitNativeUserEvaluation(any())).thenReturn(MOCK_EVALUATION)
        whenever(webBridge.getWebUserStream()).thenReturn(mockWebUserStream)
        viewModel = MainViewModel(webBridge)
    }

    @Test
    fun testDifferentNativeUserSubmit() {
        // given
        val testStream = viewModel.uiStates.test()
        val testEffectStream = viewModel.uiEffects.test()

        val user1 = User("John", 22)
        val user2 = User("Jim", 26)

        // when
        viewModel.dispatch(MainUIEvent.Init)
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user1))
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user2))

        // then
        testStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testStream.assertValueAt(1, MainUIState(null, user1, 0, 1))
        testStream.assertValueAt(2, MainUIState(null, user2, 0, 2))
        testStream.assertValueCount(3)
        testEffectStream.assertValueAt(0, MainUIEffect.LoadUrl(MOCK_URL, webBridge))
        testEffectStream.assertValueAt(1, MainUIEffect.EvaluateJavascript(MOCK_EVALUATION))
        testEffectStream.assertValueAt(2, MainUIEffect.EvaluateJavascript(MOCK_EVALUATION))
        testEffectStream.assertValueCount(3)
    }

    @Test
    fun testSameNativeUserSubmit() {
        // given
        val testUiStream = viewModel.uiStates.test()
        val testEffectStream = viewModel.uiEffects.test()

        val user1 = User("John", 22)
        val user2 = User("John", 22)

        // when
        viewModel.dispatch(MainUIEvent.Init)
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user1))
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user2))

        // then
        testUiStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testUiStream.assertValueAt(1, MainUIState(null, user1, 0, 1))
        testUiStream.assertValueCount(2)
        testEffectStream.assertValueAt(0, MainUIEffect.LoadUrl(MOCK_URL, webBridge))
        testEffectStream.assertValueAt(1, MainUIEffect.EvaluateJavascript(MOCK_EVALUATION))
        testEffectStream.assertValueAt(2, MainUIEffect.SameNativeUserMessage)
        testEffectStream.assertValueCount(3)
    }

    @Test
    fun testDifferentWebUserReceive() {
        // given
        val testStream = viewModel.uiStates.test()
        val testEffectStream = viewModel.uiEffects.test()

        val user1 = User("John", 22)
        val user2 = User("Jim", 26)

        // when
        viewModel.dispatch(MainUIEvent.Init)
        viewModel.dispatch(MainUIEvent.WebUserReceived(user1))
        viewModel.dispatch(MainUIEvent.WebUserReceived(user2))

        // then
        testStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testStream.assertValueAt(1, MainUIState(user1, null, 1, 0))
        testStream.assertValueAt(2, MainUIState(user2, null, 2, 0))
        testStream.assertValueCount(3)
        testEffectStream.assertValueAt(0, MainUIEffect.LoadUrl(MOCK_URL, webBridge))
        testEffectStream.assertValueCount(1)
    }

    @Test
    fun testSameWebUserReceive() {
        // given
        val testUiStream = viewModel.uiStates.test()
        val testEffectStream = viewModel.uiEffects.test()

        val user1 = User("John", 22)
        val user2 = User("John", 22)

        // when
        viewModel.dispatch(MainUIEvent.Init)
        viewModel.dispatch(MainUIEvent.WebUserReceived(user1))
        viewModel.dispatch(MainUIEvent.WebUserReceived(user2))

        // then
        testUiStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testUiStream.assertValueAt(1, MainUIState(user1, null, 1, 0))
        testUiStream.assertValueCount(2)
        testEffectStream.assertValueAt(0, MainUIEffect.LoadUrl(MOCK_URL, webBridge))
        testEffectStream.assertValueAt(1, MainUIEffect.SameWebUserMessage)
        testEffectStream.assertValueCount(2)
    }
}