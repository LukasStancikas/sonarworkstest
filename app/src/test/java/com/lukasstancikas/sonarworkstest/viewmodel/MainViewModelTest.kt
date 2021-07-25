package com.lukasstancikas.sonarworkstest.viewmodel

import com.lukasstancikas.sonarworkstest.RxImmediateSchedulerRule
import com.lukasstancikas.sonarworkstest.model.User
import com.lukasstancikas.sonarworkstest.view.effect.MainUIEffect
import com.lukasstancikas.sonarworkstest.view.event.MainUIEvent
import com.lukasstancikas.sonarworkstest.view.state.MainUIState
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    private val viewModel = MainViewModel()

    @Test
    fun testDifferentNativeUserSubmit() {
        // given
        val testStream = viewModel.uiStates.test()

        val user1 = User("John", 22)
        val user2 = User("Jim", 26)

        // when
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user1))
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user2))

        // then
        testStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testStream.assertValueAt(1, MainUIState(null, user1, 0, 1))
        testStream.assertValueAt(2, MainUIState(null, user2, 0, 2))
        testStream.assertValueCount(3)
    }

    @Test
    fun testSameNativeUserSubmit() {
        // given
        val testUiStream = viewModel.uiStates.test()
        val testEffectStream = viewModel.uiEffects.test()

        val user1 = User("John", 22)
        val user2 = User("John", 22)

        // when
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user1))
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user2))

        // then
        testUiStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testUiStream.assertValueAt(1, MainUIState(null, user1, 0, 1))
        testUiStream.assertValueCount(2)
        testEffectStream.assertValueAt(0, MainUIEffect.SameNativeUserMessage)
        testEffectStream.assertValueCount(1)
    }

    @Test
    fun testDifferentWebUserReceive() {
        // given
        val testStream = viewModel.uiStates.test()

        val user1 = User("John", 22)
        val user2 = User("Jim", 26)

        // when
        viewModel.dispatch(MainUIEvent.WebUserReceived(user1))
        viewModel.dispatch(MainUIEvent.WebUserReceived(user2))

        // then
        testStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testStream.assertValueAt(1, MainUIState(user1, null, 1, 0))
        testStream.assertValueAt(2, MainUIState(user2, null, 2, 0))
        testStream.assertValueCount(3)
    }

    @Test
    fun testSameWebUserReceive() {
        // given
        val testUiStream = viewModel.uiStates.test()
        val testEffectStream = viewModel.uiEffects.test()

        val user1 = User("John", 22)
        val user2 = User("John", 22)

        // when
        viewModel.dispatch(MainUIEvent.WebUserReceived(user1))
        viewModel.dispatch(MainUIEvent.WebUserReceived(user2))

        // then
        testUiStream.assertValueAt(0, MainUIState(null, null, 0, 0))
        testUiStream.assertValueAt(1, MainUIState(user1, null, 1, 0))
        testUiStream.assertValueCount(2)
        testEffectStream.assertValueAt(0, MainUIEffect.SameWebUserMessage)
        testEffectStream.assertValueCount(1)
    }
}