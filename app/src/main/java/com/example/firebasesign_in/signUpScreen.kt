package com.example.firebasesign_in

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    auth: FirebaseAuth
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ){
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var coPassword by remember {
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
        TextField(
            value = coPassword,
            onValueChange = { coPassword = it },
            placeholder = { Text(text = "coPassword") },
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank() && coPassword.isNotBlank()) {
                    if (password == coPassword) {
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "email created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    auth.signOut()
                                    navController.navigate(Routes.signIn.route)
                                } else {
                                    Toast.makeText(
                                        context,
                                        task.exception?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "pass not matching", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "empty places are not allowed", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Text(text = "sign up")
        }
    }

}