package edu.neu.madcourse.buoy;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class StickerMessagingService extends FirebaseMessagingService {
    private static final String TAG="StickerMessagingService";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    /**
     * Need to write code in this.
     * @param token
     */
    private void sendRegistrationToServer(String token){
        //update current user's "email" token
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

    }
}
