package com.example.firebasesign_in

sealed class Routes(val route : String){

    object signIn : Routes("signIn")
    object signUp : Routes("signUp")
    object home : Routes("home")

}