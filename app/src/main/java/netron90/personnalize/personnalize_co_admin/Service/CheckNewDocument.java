package netron90.personnalize.personnalize_co_admin.Service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import netron90.personnalize.personnalize_co_admin.MainActivity;
import netron90.personnalize.personnalize_co_admin.R;

/**
 * Created by CHRISTIAN on 29/03/2019.
 */

public class CheckNewDocument extends IntentService {

    private FirebaseFirestore dbFirestore;
    private ListenerRegistration registration;
    private final long TIME_ELLAPSE = 5*60*1000;
    public static final String CHANNEL_ID = "channel_id";


    public CheckNewDocument() {
        super("name");
        dbFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        registration = dbFirestore.collection("Document")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if(e != null)
                        {
                            return;
                        }

                        if(isOnline())
                        {
                            if(!snapshots.isEmpty())
                            {
                                for(DocumentChange dc : snapshots.getDocumentChanges())
                                {
                                    switch (dc.getType())
                                    {
                                        case ADDED:
                                            if(snapshots.getMetadata().hasPendingWrites() == false)
                                            {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.ic_insert_drive_file_black_24dp)
                                                        .setContentTitle("Nouveau document")
                                                        .setContentText("Vous avez recu un nouveau document")
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                        .setContentIntent(pendingIntent)
                                                        .setAutoCancel(true);

                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
                                                notificationManager.notify(1, builder.build());
                                            }

                                            break;
                                    }
                                }
                            }
//                            if(snapshots.getMetadata().hasPendingWrites() == false)
//                            {
//                                for(QueryDocumentSnapshot doc : snapshots)
//                                {
//                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//
//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                                            .setSmallIcon(R.drawable.ic_insert_drive_file_black_24dp)
//                                            .setContentTitle("Nouveau document")
//                                            .setContentText("Vous avez recu un nouveau document")
//                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                            .setContentIntent(pendingIntent)
//                                            .setAutoCancel(true);
//
//                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//
//// notificationId is a unique int for each notification that you must define
//                                    notificationManager.notify(1, builder.build());
////                                    switch (dc.getType())
////                                    {
////                                        case ADDED:
////                                            // Create an explicit intent for an Activity in your app
////                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
////
////                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
////                                                    .setSmallIcon(R.drawable.ic_insert_drive_file_black_24dp)
////                                                    .setContentTitle("Nouveau document")
////                                                    .setContentText("Vous avez recu un nouveau document")
////                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                                                    .setContentIntent(pendingIntent)
////                                                    .setAutoCancel(true);
////
////                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
////
////// notificationId is a unique int for each notification that you must define
////                                            notificationManager.notify(1, builder.build());
////                                    }
//                                }
//                            }
                            if(!isOnline())
                            {
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), CheckNewDocument.class);
                                PendingIntent pendingIntent = PendingIntent
                                        .getService(getApplicationContext(),
                                                0, intent, 0);

                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_ELLAPSE, pendingIntent);
                            }
//                            else
//                            {
//
//                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                                Intent intent = new Intent(getApplicationContext(), CheckNewDocument.class);
//                                PendingIntent pendingIntent = PendingIntent
//                                        .getService(getApplicationContext(),
//                                                0, intent, 0);
//
//                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_ELLAPSE, pendingIntent);
//                                Log.d("SERVICE", "Service launch after alarm");
//
//
//                            }
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), CheckNewDocument.class);
                            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.ELAPSED_REALTIME, TIME_ELLAPSE, pendingIntent);
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
}
