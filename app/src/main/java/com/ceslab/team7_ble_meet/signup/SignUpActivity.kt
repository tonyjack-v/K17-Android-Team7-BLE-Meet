package com.ceslab.team7_ble_meet.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivitySignUpBinding
import com.ceslab.team7_ble_meet.dialog.LoadingDialog
import com.ceslab.team7_ble_meet.registerInformation.name.RegisterUserNameActivity
import com.ceslab.team7_ble_meet.toast
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private var loadingDialog = LoadingDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpSignUpActivity()
        setAction()

    }

    private fun setUpSignUpActivity(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this@SignUpActivity).get(SignUpViewModel::class.java)
        binding.signUpViewModel = viewModel
    }

    private fun setAction(){
        binding.apply {
            SignUp_btnSignUp.setOnClickListener{
                loadingDialog.show(supportFragmentManager,"loading")
                Log.d("SignUpActivity","sign up button")
                viewModel.register()
            }
        }

        viewModel.userResp.observe(this, { response ->
            loadingDialog.dismiss()
            Log.d("SignUpActivity","signup observer")
            if (response != null) {
                if(response.type == "NONE" && response.status == "SUCCESS"){
                    toast(response.message)
                    goToRegisterGender()
                }
                if(response.type == "NONE" && response.status == "FAILED"){
                    toast(response.message)
                }

            }
        })
    }

    private fun goToRegisterGender(){
        val intent = Intent(this, RegisterUserNameActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

    }

}