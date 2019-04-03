package netron90.personnalize.personnalize_co_admin.Service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import netron90.personnalize.personnalize_co_admin.Database.DocumentAvailable;
import netron90.personnalize.personnalize_co_admin.Database.PersonnalizeDatabase;
import netron90.personnalize.personnalize_co_admin.Database.UserMessageDb;
import netron90.personnalize.personnalize_co_admin.MainActivity;
import netron90.personnalize.personnalize_co_admin.R;

/**
 * Created by CHRISTIAN on 02/04/2019.
 */

public class CheckNewMessage extends IntentService {

    private FirebaseFirestore dbFirestore;
    public static ListenerRegistration registration;
    private final long TIME_ELLAPSE = 5*60*1000;
    public static final String CHANNEL_ID = "channel_id";
    private List<UserMessageDb> listMessage;
    private SharedPreferences sharedPreferences;
    public static final String CHECK_NEW_MESSAGE = "check_new_message";
    private boolean flagMessage = false;

    public CheckNewMessage() {
        super("service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        listMessage = new ArrayList<>();
        dbFirestore = FirebaseFirestore.getInstance();
//        GetAllMessage getAllMessage = new GetAllMessage();
//        getAllMessage.execute();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flagMessage = sharedPreferences.getBoolean(CHECK_NEW_MESSAGE, false);

        registration = dbFirestore.collection("Message")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (isOnline())
                        {
                            checkNewMessage(snapshots, e, flagMessage);

                        }
                    }
                });
    }

    private boolean isOnline()
    {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            isConnected = true;
        else
            isConnected = false;

        return isConnected;
    }

//    public class GetAllMessage extends AsyncTask<Void, Void, List<UserMessageDb>>
//    {
//        @Override
//        protected List<UserMessageDb> doInBackground(Void... voids) {
//
//            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
//                    PersonnalizeDatabase.class, "personnalize").build();
//
//            List<UserMessageDb> message = db.userDao().selectAllMessage();
//
//            db.close();
//            return message;
//        }
//
//        @Override
//        protected void onPostExecute(List<UserMessageDb> messageList) {
//            super.onPostExecute(messageList);
//
//            listMessage = messageList;
//
//            registration = dbFirestore.collection("Message")
//                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//
//                            notificationForNewMessage(snapshots, e, );
//                        }
//                    });
//        }
//    }
//
//    private void notificationForNewMessage(QuerySnapshot snapshots, FirebaseFirestoreException e, boolean flagMessage)
//    {
//        if(e != null)
//        {
//            return;
//        }
//
//        if(snapshots != null && !snapshots.isEmpty())
//        {
//            if(listMessage.size() == snapshots.size())
//            {
//                Log.d("SERVICE", "No notification. No new document");
//            }
//            else
//            {
//                Log.d("SERVICE", "New document Added");
//                for(DocumentChange dc : snapshots.getDocumentChanges())
//                {
//                    switch (dc.getType())
//                    {
//                        case ADDED:
//                            Log.d("SERVICE", "Service is running.New Doc Receive");
//                            // Create an explicit intent for an Activity in your app
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                                    .setSmallIcon(R.drawable.ic_chat_black_24dp)
//                                    .setContentTitle("Nouveau Message")
//                                    .setContentText("Vous avez recu un nouveau message")
//                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                    .setContentIntent(pendingIntent)
//                                    .setAutoCancel(true);
//
//                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//
//// notificationId is a unique int for each notification that you must define
//                            notificationManager.notify(1, builder.build());
//                            break;
//                    }
//                }
//            }
//        }
//    }

    private void checkNewMessage(QuerySnapshot snapshots, FirebaseFirestoreException e, boolean flagMessage)
    {
        Log.d("SERVICE", "Service is running");

        boolean firstCall = flagMessage;
        if(firstCall)
        {
            if(e != null)
            {
                return;
            }

            if(snapshots != null && !snapshots.isEmpty())
            {
                for(DocumentChange dc : snapshots.getDocumentChanges())
                {
                    if(dc.getType() == DocumentChange.Type.ADDED)
                    {
                        Log.d("SERVICE", "Service is running.New Doc Receive");
                        // Create an explicit intent for an Activity in your app
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                                .setContentTitle("Nouveau Message")
                                .setContentText("Vous avez recu un nouveau message")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
                        notificationManager.notify(1, builder.build());
                    }
                }
            }
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHECK_NEW_MESSAGE, true).apply();
        }
    }
}
