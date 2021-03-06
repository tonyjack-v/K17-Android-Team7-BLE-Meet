package com.ceslab.team7_ble_meet.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.isValidEmail
import com.ceslab.team7_ble_meet.isValidPasswordFormat

class SignUpViewModel: ViewModel() {
    var email: String = ""
    var password: String = ""
    var rePassword: String = ""
    private var usersFireStoreHandler = UsersFireStoreHandler()
    var userResp : MutableLiveData<UsersFireStoreHandler.Resp?> = usersFireStoreHandler.userResp


    fun register(){
        Log.d("TAG","user: $email , pass: $password")
        if(email.isEmpty() || password.isEmpty() || rePassword.isEmpty()){
            userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED", "Empty field!"))
            return
        }else {
            if (!isValidEmail(email)) {
                userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED", "Wrong email format!"))
                return
            }
            if (!isValidPasswordFormat(password)) {
                userResp.postValue(
                    UsersFireStoreHandler.Resp(
                        "NONE",
                        "FAILED",
                        "Wrong password format, must contain /@$#._"
                    )
                )
                return
            }
            if (password != rePassword) {
                userResp.postValue(
                    UsersFireStoreHandler.Resp(
                        "NONE",
                        "FAILED",
                        "Password and confirm password are not the same!"
                    )
                )
                return
            }
            usersFireStoreHandler.createUser(email,password)
        }

    }

}