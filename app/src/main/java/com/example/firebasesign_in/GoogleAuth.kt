package com.example.firebasesign_in

import android.content.IntentSender
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuth (
    val oneTapClient: SignInClient,
    val auth : FirebaseAuth
){
    val googleRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("308090011519-h5b1h0lgj2ege574nk54coiekgtkss98.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    suspend fun getIntentSender():IntentSender?{
        val result = try {
            oneTapClient.beginSignIn(googleRequest).await()
        }catch (e : Exception){
            e.printStackTrace()
            if (e is CancellationException) throw  e
            null
        }
        return result?.pendingIntent?.intentSender
    }
    fun signOut(){
        oneTapClient.signOut()
        auth.signOut()
    }
}