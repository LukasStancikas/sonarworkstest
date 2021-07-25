package com.lukasstancikas.sonarworkstest.bridge.android

import com.lukasstancikas.sonarworkstest.model.User
import junit.framework.Assert.assertEquals
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class WebBridgeImplTest {
    private val bridge = WebBridgeImpl()

    @Test
    fun testUserSubmit() {
        // given
        val testStream = bridge.getWebUserStream().test()
        val user = User("John", 22)
        val userJson = Json.encodeToString(user)

        // when
        bridge.onUserSubmitFromWeb(userJson)

        // then
        testStream.assertValueAt(0, user)
        testStream.assertValueCount(1)
    }

    @Test
    fun testNativeUserEvaluation() {
        // given
        val user = User("John", 22)
        val userJson = Json.encodeToString(user)
        val expectedEvaluation = "javascript: nativeUserSubmit(\'" + Json.encodeToString(user) +
                "\')"

        // when
        val evaluation = bridge.getSubmitNativeUserEvaluation(user)

        // then
        assertEquals(evaluation, expectedEvaluation)
    }
}