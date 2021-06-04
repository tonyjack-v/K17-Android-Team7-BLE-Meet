package com.ceslab.team7_ble_meet.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.chat.ChatActivity
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MessagingService", "token: $token")
        if(KeyValueDB.getUserShortId() != ""){
            updateToken(token)
        }

    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("MessagingService","message  ${message.data}")
        if(message.data.isNotEmpty()){
            Log.d("MessagingService", "hell no")
            val map: Map<String, String> = message.data
            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]
            val hisImage = map["hisImage"]
            if(!KeyValueDB.isChat()){
                sendNotification(title!!,message!!,hisId!!,hisImage!!)
            }

        }
    }



    companion object {
        fun updateToken(newToken:String){
            UsersFireStoreHandler().getUserToken { tokens ->
                if(tokens.isEmpty()){
                    tokens.add(newToken)
                    UsersFireStoreHandler().setUserToken(tokens)
                }else{
                    if(tokens.contains(newToken)){
                        return@getUserToken
                    }else{
                        tokens.add(newToken)
                        UsersFireStoreHandler().setUserToken(tokens)
                    }
                }

            }
        }

    }

    private fun sendNotification(title: String
                                 , message: String
                                 , hisId: String
                                 , hisImage: String){
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra(AppConstants.USER_ID,hisId)
            putExtra(AppConstants.AVATAR,hisImage)
            putExtra(AppConstants.USER_NAME,title)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        Log.d("MessagingService","pending intent: $hisId")
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, "channel_message")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_layout)
            .setContentIntent(pendingIntent)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(hisId.toInt(), builder.build())
        }

    }
}