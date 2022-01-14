package com.graveno.alphalab.app.codedemo

object RegistrationUtil {

    private val existingUsers = listOf("karen","Peter", "Carl")

    /**
     * the input is not valid if :
     * - the user is empty
     * - the user is already taken
     * - confirmed password is not same as password
     * - password is less then 2 digits
     */
    fun validateRegistration(
        username : String,
        password : String,
        confirmedPassword : String
    ) : Boolean {
        return when {
            username.isEmpty() || username.isBlank() || username in existingUsers-> {
                false
            }
            password.isEmpty() || password.isBlank() || password.length <= 2 -> {
                false
            }
            confirmedPassword.isBlank() || confirmedPassword.isEmpty() || confirmedPassword != password -> {
                false
            }
            else -> true
        }
    }
}