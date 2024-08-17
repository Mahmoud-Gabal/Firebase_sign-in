package com.example.firebasesign_in

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun navGraph(
    modifier: Modifier = Modifier,
    googleAuth: GoogleAuth,
    activity: MainActivity,
    auth: FirebaseAuth,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = Routes.signIn.route) {
        composable(route = Routes.signIn.route) {
            if (auth.currentUser != null) {
                val user = if (auth.currentUser?.email?.isEmpty() == true ||auth.currentUser?.email == null) auth.currentUser?.phoneNumber else auth.currentUser?.email
                navController.navigate(Routes.home.route + "/$user")
            }else{
                val intentLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if (result.resultCode == RESULT_OK) {
                            try {
                                val credential =
                                    googleAuth.oneTapClient.getSignInCredentialFromIntent(
                                        result.data ?: return@rememberLauncherForActivityResult
                                    )
                                val idToken = credential.googleIdToken
                                when {
                                    idToken != null -> {
                                        val firebaseCredential =
                                            GoogleAuthProvider.getCredential(idToken, null)
                                        auth.signInWithCredential(firebaseCredential)
                                            .addOnCompleteListener(activity) { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "signed in sccessfully",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                    val user = task.result.user?.email
                                                    navController.navigate(Routes.home.route + "/$user")
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "fail to sign in",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                }
                                            }
                                    }

                                    else -> {
                                        Toast.makeText(context, "no id token", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
                SignInScreen(
                    navController = navController,
                    onGoogleClick = {
                        GlobalScope.launch {
                            intentLauncher.launch(
                                IntentSenderRequest.Builder(
                                    googleAuth.getIntentSender() ?: return@launch
                                ).build()
                            )
                        }
                    },
                    activity = activity,
                    auth = auth
                )
            }


        }
        composable(route = Routes.signUp.route) {
//            SignUpScreen(
//                navController = navController,
//                auth = auth
//            )
        }
        composable(
            route = Routes.home.route +"/{user}",
            arguments = listOf(
                navArgument("user"){type = NavType.StringType}
            )
        ) {
            val user = it.arguments?.getString("user")
            HomeScreen(
                signOut = {
                    googleAuth.signOut()
                    navController.navigate(Routes.signIn.route)
                    Toast.makeText(context, "signed out", Toast.LENGTH_SHORT).show()
                },
                user = user
            )
        }
    }
}