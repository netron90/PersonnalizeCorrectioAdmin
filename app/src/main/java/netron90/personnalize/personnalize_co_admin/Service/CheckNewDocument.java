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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import netron90.personnalize.personnalize_co_admin.Database.DocumentAvailable;
import netron90.personnalize.personnalize_co_admin.Database.PersonnalizeDatabase;
import netron90.personnalize.personnalize_co_admin.MainActivity;
import netron90.personnalize.personnalize_co_admin.R;

/**
 * Created by CHRISTIAN on 29/03/2019.
 */

public class CheckNewDocument extends IntentService {

    private FirebaseFirestore dbFirestore;
    public static ListenerRegistration registration;
    private final long TIME_ELLAPSE = 1*60*1000;
    public static final String CHANNEL_ID = "channel_id";
    private List<DocumentAvailable> listDoc;
    private SharedPreferences sharedPreferences;
    public static final String CHECK_NEW_DOCUMENT = "check_new_document";
    private boolean flagDocument = false;


    public CheckNewDocument() {
        super("name");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        listDoc = new ArrayList<>();
        dbFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flagDocument = sharedPreferences.getBoolean(CHECK_NEW_DOCUMENT, false);

//        GetAllDocAvailable getAllDocAvailable = new GetAllDocAvailable();
//        getAllDocAvailable.execute();

        registration = dbFirestore.collection("Document")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (isOnline())
                        {
                            checkNewDocument(snapshots, e, flagDocument);

                        }
                    }
                });

//        registration = dbFirestore.collection("Document")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//
//                        if(e != null)
//                        {
//                            return;
//                        }
//
//                        if(isOnline() && !snapshots.isEmpty())
//                        {
//                            for(DocumentChange dc : snapshots.getDocumentChanges())
//                            {
//                                switch (dc.getType())
//                                {
//                                    case ADDED:
//
//                                        break;
//                                }
//                            }
////                                for(DocumentChange dc : snapshots.getDocumentChanges())
////                                {
////                                    switch (dc.getType())
////                                    {
////                                        case ADDED:
////                                            if(snapshots.getMetadata().hasPendingWrites() == false)
////                                            {
////                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
////
////                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
////                                                        .setSmallIcon(R.drawable.ic_insert_drive_file_black_24dp)
////                                                        .setContentTitle("Nouveau document")
////                                                        .setContentText("Vous avez recu un nouveau document")
////                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                                                        .setContentIntent(pendingIntent)
////                                                        .setAutoCancel(true);
////
////                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
////
////// notificationId is a unique int for each notification that you must define
////                                                notificationManager.notify(1, builder.build());
////                                            }
////
////                                            break;
////                                    }
////                                }
//
////                            if(snapshots.getMetadata().hasPendingWrites() == false)
////                            {
////                                for(QueryDocumentSnapshot doc : snapshots)
////                                {
////                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
////
////                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
////                                            .setSmallIcon(R.drawable.ic_insert_drive_file_black_24dp)
////                                            .setContentTitle("Nouveau document")
////                                            .setContentText("Vous avez recu un nouveau document")
////                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                                            .setContentIntent(pendingIntent)
////                                            .setAutoCancel(true);
////
////                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
////
////// notificationId is a unique int for each notification that you must define
////                                    notificationManager.notify(1, builder.build());
//////                                    switch (dc.getType())
//////                                    {
//////                                        case ADDED:
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
//////                                    }
////                                }
////                            }
//                            if(!isOnline())
//                            {
//                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                                Intent intent = new Intent(getApplicationContext(), CheckNewDocument.class);
//                                PendingIntent pendingIntent = PendingIntent
//                                        .getService(getApplicationContext(),
//                                                0, intent, 0);
//
//                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_ELLAPSE, pendingIntent);
//                            }
////                            else
////                            {
////
////                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
////                                Intent intent = new Intent(getApplicationContext(), CheckNewDocument.class);
////                                PendingIntent pendingIntent = PendingIntent
////                                        .getService(getApplicationContext(),
////                                                0, intent, 0);
////
////                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_ELLAPSE, pendingIntent);
////                                Log.d("SERVICE", "Service launch after alarm");
////
////
////                            }
//                        }
//                        else
//                        {
//                            Intent intent = new Intent(getApplicationContext(), CheckNewDocument.class);
//                            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
//                            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//                            alarmManager.set(AlarmManager.ELAPSED_REALTIME, TIME_ELLAPSE, pendingIntent);
//                        }
//                    }
//                });
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

//    public class GetAllDocAvailable extends AsyncTask<Void, Void, List<DocumentAvailable>>
//    {
//        @Override
//        protected List<DocumentAvailable> doInBackground(Void... voids) {
//
//            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
//                    PersonnalizeDatabase.class, "personnalize").build();
//
//            List<DocumentAvailable> doc = db.userDao().selectListDocAvailable();
//
//            db.close();
//            return doc;
//        }
//
//        @Override
//        protected void onPostExecute(List<DocumentAvailable> documentAvailables) {
//            super.onPostExecute(documentAvailables);
//
//            listDoc = documentAvailables;
//
//            registration = dbFirestore.collection("Document")
//                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//
//                            notificationForNewDocument(snapshots, e);
//
//                        }
//                    });
//        }
//    }

    private void checkNewDocument(QuerySnapshot snapshots, FirebaseFirestoreException e, boolean flagDoc)
    {
        Log.d("SERVICE", "Service is running");

        boolean firstCall = flagDoc;
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
                }
            }
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHECK_NEW_DOCUMENT, true).apply();
        }
    }
}
