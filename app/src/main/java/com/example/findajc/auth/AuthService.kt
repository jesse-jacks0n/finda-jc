package com.example.findajc.auth//package com.example.findajc.auth
//
//import androidx.compose.runtime.*
//import com.google.firebase.auth.EmailAuthProvider
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.userProfileChangeRequest
//import kotlinx.coroutines.tasks.await
//
//class AuthService {
//
//    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    private var _user by mutableStateOf<FirebaseUser?>(firebaseAuth.currentUser)
//    var user: FirebaseUser? by mutableStateOf(_user)
//        private set
//
//    var isDarkMode by mutableStateOf(false)
//        private set
//
//    init {
//        firebaseAuth.addAuthStateListener {
//            _user = it.currentUser
//            user = _user
//        }
//    }
//
//    fun toggleTheme() {
//        isDarkMode = !isDarkMode
//    }
//
//    suspend fun signInWithEmail(email: String, password: String) {
//        firebaseAuth.signInWithEmailAndPassword(email, password).await()
//    }
//
//    suspend fun signUpWithEmail(email: String, password: String, displayName: String) {
//        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
//        result.user?.updateProfile(userProfileChangeRequest {
//            displayName = displayName
//        })?.await()
//        user = firebaseAuth.currentUser
//    }
//
//    suspend fun signOut() {
//        firebaseAuth.signOut()
//    }
//
//    suspend fun sendPasswordResetEmail(email: String) {
//        firebaseAuth.sendPasswordResetEmail(email).await()
//    }
//
//    suspend fun updateEmail(email: String) {
//        try {
//            firebaseAuth.currentUser?.updateEmail(email)?.await()
//            user = firebaseAuth.currentUser
//        } catch (e: Exception) {
//            println("Failed to update email: $e")
//        }
//    }
//
//    suspend fun updateProfile(displayName: String? = null, phoneNumber: String? = null) {
//        try {
//            val userProfileChangeRequest = userProfileChangeRequest {
//                displayName?.let { displayName = it }
//                // phoneNumber?.let { // phone number update is more complex }
//            }
//            firebaseAuth.currentUser?.updateProfile(userProfileChangeRequest)?.await()
//            user = firebaseAuth.currentUser
//        } catch (e: Exception) {
//            println("Failed to update profile: $e")
//        }
//    }
//
//    suspend fun changePassword(currentPassword: String, newPassword: String) {
//        val user = firebaseAuth.currentUser ?: return
//        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
//        user.reauthenticate(credential).await()
//        user.updatePassword(newPassword).await()
//    }
//}
