package edu.neu.madcourse.buoy;

/*
Sources:
https://firebase.google.com/docs/cloud-messaging/android/receive

 */
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class StickerMessagingService extends FirebaseMessagingService {
    private Handler handler = new Handler();
    private static final String TAG="StickerMessagingService";
    private DatabaseReference mdataBase;
    FirebaseAuth mFirebaseAuth;
    private String uid;//this user's id


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    /**
     * Send new token to DB on token change.
     * @param token
     */
    private void sendRegistrationToServer(String token){
        uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference();
        mdataBase.child("Users").child("uid").child("email").setValue(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "You got a new Sticker!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        Log.d(TAG, "From: " + remoteMessage.getFrom());
    }


}
