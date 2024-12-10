package com.example.gymmembershipapp.utils


object Constant {

    private const val REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    fun isNumberValid(number:String,callback:(Resources<String>)->Unit): Boolean {
        return if (number.length == 10) {
            true
        } else {
            callback(Resources.Error("Number should contain 10 numbers"))
            false
        }
    }
    fun isNameValid(name:String,callback:(Resources<String>)->Unit): Boolean {
        return if (name.isNotEmpty()) {
            true
        } else {
            callback(Resources.Error("Your name field is empty"))
            false
        }
    }
    fun isPasswordValid(password:String,callback:(Resources<String>)->Unit):Boolean{
        return if (password.toString().length>= 8){
            true
        }else{
            callback(Resources.Error("Password should contain at least 8 characters"))
            false
        }
    }

     fun isEmailValid(email:String,callback: (Resources<String>) -> Unit): Boolean {
        return if (email.isNotEmpty() && email.matches(Regex(REGEX))
        ) {
            true
        } else {
            callback(Resources.Error("Enter a valid email"))
            false
        }
    }


}