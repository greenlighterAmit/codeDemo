package com.graveno.alphalab.app.codedemo

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest {

    @Test
    fun `empty username returns false`() {
        val result = RegistrationUtil.validateRegistration(
            username = "",
            password = "1234",
            confirmedPassword = "1234"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `valid user and correct both password`() {
        val result = RegistrationUtil.validateRegistration(
            username = "phil",
            password = "1234",
            confirmedPassword = "1234"
        )
        assertThat(result).isTrue()
    }

    @Test
    fun `username already taken returns false`() {
        val result = RegistrationUtil.validateRegistration(
            username = "karen",
            password = "1234",
            confirmedPassword = "1234"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `password is empty returns false`() {
        val result = RegistrationUtil.validateRegistration(
            username = "loki",
            password = "",
            confirmedPassword = "1234"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `password did not match returns false`() {
        val result = RegistrationUtil.validateRegistration(
            username = "karl",
            password = "1234",
            confirmedPassword = "12345"
        )
        assertThat(result).isFalse()
    }
}