package com.ceslab.team7_ble_meet.login

import android.content.Intent
import android.content.LocusId
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.databinding.ActivityLogInBinding
import com.ceslab.team7_ble_meet.registerInformation.RegisterBirthdayActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterGenderActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterTagActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterUserNameActivity
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.signup.SignUpActivity
import com.ceslab.team7_ble_meet.toast

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var viewModel: LogInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLogIn()
        binding.apply {
            btnSignUp.setOnClickListener {
                goToSignUp()
            }
            btnLogin.setOnClickListener{
                viewModel.logIn()
            }
        }
    }

    private fun setLogIn() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_log_in)
        viewModel = ViewModelProvider(this).get(LogInViewModel::class.java)
        binding.logInViewModel = viewModel

        viewModel.userResp.observe(this, Observer { response ->
            if(response != null){
                if(response.type == "LOGIN" && response.status == "SUCCESS" && response.message == "USERNAME"){
                    goToRegisterName()
                }else
                if(response.type == "LOGIN" && response.status == "SUCCESS"&& response.message == "GENDER"){
                    goToRegisterGender()
                }else
                if(response.type == "LOGIN" && response.status == "SUCCESS"&& response.message == "DOB"){
                    goToRegisterDoB()
                }else
                if(response.type == "LOGIN" && response.status == "SUCCESS"&& response.message == "TAG"){
                    goToRegisterTag()
                }else
                if(response.type == "LOGIN" && response.status == "SUCCESS"&& response.message == "DASHBOARD"){
                    goToDashBoard()
                }

                toast(response.message)
            }
        })
    }

    private fun goToRegisterName(){
        val intent = Intent(this, RegisterUserNameActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun goToRegisterDoB(){
        val intent = Intent(this, RegisterBirthdayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun goToRegisterGender(){
        val intent = Intent(this, RegisterGenderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun goToRegisterTag(){
        val intent = Intent(this, RegisterTagActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun goToSignUp(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
    private fun goToDashBoard(){
        val intent = Intent(this, DashBoardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

}