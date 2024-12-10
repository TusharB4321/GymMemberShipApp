package com.example.gymmembershipapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymmembershipapp.repository.UserRepository
import com.example.gymmembershipapp.utils.Resources

class UserViewModel(private val userRepository: UserRepository):ViewModel(){

    private val _status=MutableLiveData<Resources<String>>()
    val status:LiveData<Resources<String>>get() = _status

    fun registerUser(email:String,password:String,userDetails:Map<String,Any>){
        _status.value=Resources.Loading()
        userRepository.registerUser(email,password,userDetails){data->
            _status.postValue(data)
        }
    }
    fun loginUser(email: String, password: String) {
        _status.value=Resources.Loading()
        userRepository.loginUser(email, password) { data ->
            _status.postValue(data)
        }
    }
}