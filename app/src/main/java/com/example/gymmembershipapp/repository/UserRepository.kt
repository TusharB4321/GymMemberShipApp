package com.example.gymmembershipapp.repository

import com.example.gymmembershipapp.utils.Constant
import com.example.gymmembershipapp.utils.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserRepository(private val firebaseAuth: FirebaseAuth,private val database: FirebaseDatabase) {

    fun registerUser(
        email:String,
        password:String,
        userDetails:Map<String,Any>,
        callback: (Resources<String>) -> Unit
    ){


        if (Constant.isEmailValid(email,callback)&& Constant.isPasswordValid(password,callback)){
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->

                if (task.isSuccessful) {
                    val userId=firebaseAuth.currentUser?.uid?:""
                    database.reference.child("users").child(userId).setValue(userDetails)
                        .addOnCompleteListener {db->
                            if (db.isSuccessful) {
                                callback(Resources.Success("Registration Successful"))
                            }else {
                                callback(Resources.Error("Failed to save user: ${db.exception?.message}"))
                            }
                        }
                }else {
                    callback(Resources.Error("Failed to register: ${task.exception?.message}"))
                }

            }
        }


    }

    fun loginUser(email: String,password: String,callback: (Resources<String>) -> Unit){

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->

                if (task.isSuccessful){
                    callback(Resources.Success("Login Successful"))
                }else{
                    callback(Resources.Error("Invalid email or password"))
                }

            }

    }
}