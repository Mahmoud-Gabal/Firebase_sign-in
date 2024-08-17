package com.example.firebasesign_in

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onGoogleClick: () -> Unit = {},
    activity: MainActivity,
    auth: FirebaseAuth
) {

    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var email by remember {
            mutableStateOf("")
        }
        var password  by remember {
            mutableStateOf("")
        }
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(text = "Email") },
            modifier = Modifier.padding(bottom = 10.dp)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text(text = "Password") },
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "signed in successfully", Toast.LENGTH_SHORT)
                                .show()
                            val user = task.result.user?.email
                            navController.navigate(Routes.home.route)
                        } else {
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(context, "empty places are not allowed", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Text(text = "login")
        }
        Button(onClick = {
            navController.navigate(Routes.signUp.route)
        }) {
            Text(text = "sign up")
        }
        Button(onClick = {
            onGoogleClick()
        }) {
            Text(text = "sign in with google")
        }
        var phone by remember {
            mutableStateOf("")
        }
        var otp by remember {
            mutableStateOf("")
        }
        var verificationId by remember {
            mutableStateOf("")
        }
        TextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = { Text(text = "+20") },
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Button(onClick = {
            PhoneAuth.sendVerification(
                phoneNumber = phone,
                auth = auth,
                activity = activity,
                context = context,
                setVerificationId = {
                    verificationId = it
                }
            )
        }
        ) {
            Text(text = "send otp")
        }
        TextField(
            value = otp,
            onValueChange = { otp = it },
            placeholder = { Text(text = "otp") },
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Button(onClick = {
                    try {
                        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "signed in successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val user = task.result?.user?.phoneNumber
                                    navController.navigate(Routes.home.route + "/$user")
                                } else {
                                    // Sign in failed, display a message and update the UI
                                    Toast.makeText(
                                        context,
                                        "fail to sign in  :${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(context, "otp is wrong", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    // Update UI
                                }
                            }
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }

        }

        ) {
            Text(text = "verify and sign in")
        }


    }

}