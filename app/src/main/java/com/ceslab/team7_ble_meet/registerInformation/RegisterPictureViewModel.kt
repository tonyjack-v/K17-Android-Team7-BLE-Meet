package com.ceslab.team7_ble_meet.registerInformation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.firebase.firestore.SetOptions

class RegisterPictureViewModel: ViewModel() {
    var selected: ByteArray? = null
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

//    fun uploadImage(){
//        if(selectedImage == null){
//            userResp.postValue(
//                UsersFireStoreHandler.Resp(
//                    "NONE",
//                    "FAILED",
//                    "Not selected image!"
//                )
//            )
//        }else{
//            instance.updateAvatar(selectedImage!!)
//        }
//    }

    fun uploadImage(){
        Log.d("TAG", "image array: ${selected?.size}")
        if(selected == null || selected!!.isEmpty()){
            userResp.postValue(
                UsersFireStoreHandler.Resp(
                    "NONE",
                    "FAILED",
                    "Not selected image!"
                )
            )
        }else{
            instance.updateAvatar(selected!!)
        }
    }
}