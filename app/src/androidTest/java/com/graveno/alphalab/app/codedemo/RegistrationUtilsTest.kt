package com.graveno.alphalab.app.codedemo

import com.google.common.truth.Truth
import org.junit.Test

class RegistrationUtilsTest {
    @Test
    fun empty_username_returns_false() {
        val result = RegistrationUtil.validateRegistration(
            username = "",
            password = "1234",
            confirmedPassword = "1234"
        )
        Truth.assertThat(result).isFalse()
    }
}