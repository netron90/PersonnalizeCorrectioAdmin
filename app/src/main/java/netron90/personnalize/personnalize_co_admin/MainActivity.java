package netron90.personnalize.personnalize_co_admin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.UUID;

import javax.annotation.Nullable;

import netron90.personnalize.personnalize_co_admin.Database.DocumentAvailable;
import netron90.personnalize.personnalize_co_admin.Database.PersonnalizeDatabase;
import netron90.personnalize.personnalize_co_admin.Service.CheckNewDocument;
import netron90.personnalize.personnalize_co_admin.Service.CheckNewMessage;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    public static FirebaseFirestore dbFirestore;
    public static final String FIRST_CALL_SERVE_LISTENER = "firstCallServerListener";
    public static SharedPreferences sharedPreferences;
    private ListenerRegistration registration;
    private DocumentAvailable documentAvailable;
    private DocumentAvailableAdapter documentAvailableAdapter;
    private int serverFirstTimeListener = 1;
    public static boolean onCreateFlag = false;
    private List<DocumentAvailable> listDocuments, documentUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(CheckNewDocument.registration != null)
        {
            Log.d("ACTIVITY START", "registration document remove");
            CheckNewDocument.registration.remove();
            CheckNewDocument.registration= null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CheckNewDocument.CHECK_NEW_DOCUMENT, false).apply();
        }
        if(CheckNewMessage.registration != null)
        {
            Log.d("ACTIVITY START", "registration message remove");
            CheckNewMessage.registration.remove();
            CheckNewMessage.registration= null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CheckNewMessage.CHECK_NEW_MESSAGE, false).apply();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        dbFirestore  = FirebaseFirestore.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPreferences.getString("teamId", "").isEmpty())
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("teamId", UUID.randomUUID().toString()).apply();
        }
        if(registration != null)
        {
            registration.remove();
            registration = null;
        }

        listDocuments = new ArrayList<>();
        documentAvailableAdapter = new DocumentAvailableAdapter(listDocuments);
        recyclerView.setAdapter(documentAvailableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onStart() {
        super.onStart();
        //createRealtimeListener();
        createListener();
        createNotificationChannel();
        if(CheckNewDocument.registration != null)
        {
            Log.d("ACTIVITY START", "registration document remove");
            CheckNewDocument.registration.remove();
            CheckNewDocument.registration= null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CheckNewDocument.CHECK_NEW_DOCUMENT, false).apply();
        }
        if(CheckNewMessage.registration != null)
        {
            Log.d("ACTIVITY START", "registration message remove");
            CheckNewMessage.registration.remove();
            CheckNewMessage.registration= null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CheckNewMessage.CHECK_NEW_MESSAGE, false).apply();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        registration.remove();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), CheckNewDocument.class);
        Intent intent2 = new Intent(getApplicationContext(), CheckNewMessage.class);
        startService(intent);
        startService(intent2);
    }

    private DocumentAvailable getQueryResult(QueryDocumentSnapshot doc)
    {
        documentAvailable = new DocumentAvailable();
        if (doc.get("documentName") != null)
        {
            documentAvailable.documentName = doc.getString("documentName");
        }

        if (doc.get("deliveryDate") != null)
        {
            documentAvailable.deliveryDate = doc.getString("deliveryDate");
        }

        if (doc.get("docEnd") != null)
        {
            documentAvailable.docEnd = (Boolean) doc.get("docEnd");
        }

        if (doc.get("documentPaid") != null)
        {
            documentAvailable.documentPaid = (Boolean) doc.get("documentPaid");
        }

        if (doc.get("documentPath") != null)
        {
            documentAvailable.documentPath = doc.getString("documentPath");
        }

        if (doc.get("emailUser") != null)
        {
            documentAvailable.emailUser = doc.getString("emailUser");
        }

        if (doc.get("miseEnForme") != null)
        {
            documentAvailable.miseEnForme = (Boolean) doc.get("miseEnForme");
        }

        if (doc.get("powerPoint") != null)
        {
            documentAvailable.powerPoint = (Boolean) doc.get("powerPoint");
        }

        if (doc.get("nameUser") != null)
        {
            documentAvailable.nameUser = doc.getString("nameUser");
        }

        if (doc.get("pageNumber") != null)
        {
            documentAvailable.pageNumber = (Long)doc.get("pageNumber");
        }

        if (doc.get("phoneUser") != null)
        {
            documentAvailable.phoneUser = doc.getString("phoneUser");
        }

        if (doc.get("userId") != null)
        {
            documentAvailable.userId = doc.getString("userId");
        }

        documentAvailable.docRef = doc.getId();

        return documentAvailable;
    }

    private void createRealtimeListener()
    {
        registration = dbFirestore.collection("Document")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null)
                        {
                            return;
                        }

                        if (isOnline())
                        {
                            if(documentAvailableAdapter != null)
                            {
                                documentUpdate = new ArrayList<>();
                                if(snapshots.size() != DocumentAvailableAdapter.docAvailable.size())
                                {
                                    if(snapshots.getMetadata().hasPendingWrites() == false)
                                    {
                                        for(DocumentChange dc : snapshots.getDocumentChanges())
                                        {
                                            switch (dc.getType())
                                            {
                                                case ADDED:
                                                    DocumentAvailable docAvailable = new DocumentAvailable();
                                                    getQueryResult(dc.getDocument());
                                                    documentUpdate.add(docAvailable);
                                                    break;
                                            }
                                        }
                                    }
                                    for (int i = 0; i < documentUpdate.size(); i++)
                                    {
                                        DocumentAvailableAdapter.docAvailable.add(documentUpdate.get(i));
                                    }
                                    documentAvailableAdapter.notifyDataSetChanged();
                                    SaveDataUpdate saveDataUpdate = new SaveDataUpdate();
                                    saveDataUpdate.execute();
                                }

                            }
                            else
                            {
                                Log.d("DATA", "document null");
                                if(sharedPreferences.getBoolean(FIRST_CALL_SERVE_LISTENER, false) == false)
                                {
                                    Log.d("DATA", "Premier appel");
                                    if(!snapshots.isEmpty())
                                    {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        //TODO: GET ALL DOCUMENT FROM FIRESTORE
                                        listDocuments = new ArrayList<>();

                                        int i = 0;
                                        Log.d("DATA", "document snapshot taille " + snapshots.size());
                                        for (QueryDocumentSnapshot doc : snapshots)
                                        {
                                            documentAvailable = new DocumentAvailable();
                                            getQueryResult(doc);
                                            listDocuments.add(documentAvailable);
                                            Log.d("DATA", "document Reference: " + snapshots.getDocuments().get(i).getId());
                                            i++;
                                        }
                                        Log.d("DATA", "document list taille " + listDocuments.size());

                                        documentAvailableAdapter = new DocumentAvailableAdapter(listDocuments);
                                        recyclerView.setAdapter(documentAvailableAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        //TODO:SAVE DOCUMENT GET INTO LOCAL DATABASE

                                        SaveFirstDocument saveFirstDocument = new SaveFirstDocument();
                                        saveFirstDocument.execute();
                                    }
                                }
                                else
                                {
                                    Log.d("DATA", "Autre appel");
                                    if(snapshots.isEmpty() == false)
                                    {
                                        //TODO: GET ALL DOCUMENT FROM FIRESTORE
                                        listDocuments = new ArrayList<>();
                                        int i = 0;
                                        for (QueryDocumentSnapshot doc : snapshots)
                                        {
                                            documentAvailable = new DocumentAvailable();
                                            getQueryResult(doc);
                                            listDocuments.add(documentAvailable);
                                            Log.d("DATA", "document Reference: " + snapshots.getDocuments().get(i).getId());
                                            i++;
                                        }
                                        Log.d("DATA", "Autre appel. taille list document: " + listDocuments.size());
                                        documentAvailableAdapter = new DocumentAvailableAdapter(listDocuments);
                                        recyclerView.setAdapter(documentAvailableAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());

//                                        //TODO:SAVE DOCUMENT GET INTO LOCAL DATABASE
//                                        SaveFirstDocument saveFirstDocument = new SaveFirstDocument();
//                                        saveFirstDocument.execute();
                                    }
                                }

                            }

                        }
                        else
                        {
                            //TODO: LOAD DATA FORM LOCAL DATABASE
                            if(onCreateFlag == false)
                            {
                                onCreateFlag = true;
                                LoadData loadData = new LoadData();
                                loadData.execute();
                            }
                        }

                    }
                });
    }

//    private void createListener()
//    {
//        registration = dbFirestore.collection("Document")
//                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
//                        if(e != null)
//                        {
//                            return;
//                        }
//
//                        if(isOnline() &&  !snapshots.isEmpty())
//                        {
//
//                            if(snapshots.size() == DocumentAvailableAdapter.docAvailable.size())
//                            {}
//                            else
//                            {
//                                for(DocumentChange dc : snapshots.getDocumentChanges())
//                                {
//                                    switch (dc.getType())
//                                    {
//                                        case ADDED:
//                                            DocumentAvailableAdapter.docAvailable.add(getQueryResult(dc.getDocument()));
//                                            break;
//                                    }
//                                }
//                                documentAvailableAdapter.notifyDataSetChanged();
//                                SaveFirstDocument saveFirstDocument = new SaveFirstDocument();
//                                saveFirstDocument.execute();
//                            }
//
//
//
//                        }
//                        else
//                        {
//                            if(snapshots.size() == DocumentAvailableAdapter.docAvailable.size())
//                            {}
//                            else {
//                                LoadData loadData = new LoadData();
//                                loadData.execute();
//                            }
//
//                        }
//
//                    }
//                });
//    }

    private void createListener()
    {
        registration = dbFirestore.collection("Document")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                        if(e != null)
                        {
                            return;
                        }

                        if(isOnline())
                        {
                            final QuerySnapshot snapshot = snapshots;
                            //TODO: ACTIVE NETWORK ACCESS
                            dbFirestore.enableNetwork()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //TODO: GET DATA FROM FIREBASE SERVER
                                            if(snapshot != null && !snapshot.isEmpty())
                                            {
                                                if(DocumentAvailableAdapter.docAvailable.size() == snapshot.size())
                                                {}
                                                else
                                                {
                                                    Log.d("LISTER", "All doc Load. Document Added");
                                                    Log.d("LISTER", "List document changed: " + snapshot.getDocumentChanges().size());
                                                    for(DocumentChange dc : snapshot.getDocumentChanges())
                                                    {
                                                        if(dc.getType() == DocumentChange.Type.ADDED)
                                                        {
                                                            DocumentAvailableAdapter.docAvailable.add(getQueryResult(dc.getDocument()));
                                                        }

                                                    }
                                                    documentAvailableAdapter.notifyDataSetChanged();
                                                    Log.d("LISTER", "List notify");
                                                }

                                            }

                                        }
                                    });


                        }
                        else
                        {

                            final QuerySnapshot snapshot = snapshots;
                            //TODO: GET DATA FROM CACHE
                            dbFirestore.disableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("LISTER", "List document changed on Cache: " + snapshot.getDocumentChanges().size());
                                    if(!snapshot.isEmpty() && snapshot != null)
                                    {
                                        if(DocumentAvailableAdapter.docAvailable.size() == snapshot.size())
                                        {}
                                        else
                                        {
                                            Log.d("LISTER", "List document changed on Cache: " + snapshot.getDocumentChanges().size());
                                            for(DocumentChange dc : snapshot.getDocumentChanges())
                                            {
                                                if(dc.getType() == DocumentChange.Type.ADDED)
                                                {
                                                    DocumentAvailableAdapter.docAvailable.add(getQueryResult(dc.getDocument()));
                                                }
                                            }
                                            documentAvailableAdapter.notifyDataSetChanged();
                                            Log.d("LISTER", "List notify off line");
                                        }

                                    }

                                }
                            });
                        }

                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        onCreateFlag = false;
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

    public class SaveFirstDocument extends AsyncTask<Void, Void, List<DocumentAvailable>>
    {
        @Override
        protected List<DocumentAvailable> doInBackground(Void... voids) {

            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PersonnalizeDatabase.class, "personnalize").build();


            if(sharedPreferences.getBoolean(FIRST_CALL_SERVE_LISTENER, false) == false)
            {
                for(int i = 0; i < DocumentAvailableAdapter.docAvailable.size(); i++)
                {
                    db.userDao().insertNewDocAvailable(DocumentAvailableAdapter.docAvailable.get(i));
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_CALL_SERVE_LISTENER, true).apply();
            }
            else
            {
                List<DocumentAvailable> docAvailable = db.userDao().selectListDocAvailable();
                if(docAvailable.size() != DocumentAvailableAdapter.docAvailable.size())
                {
                    for(int i = 0; i < DocumentAvailableAdapter.docAvailable.size(); i++)
                    {
                        if(i == DocumentAvailableAdapter.docAvailable.size() - 1)
                        {
                            db.userDao().insertNewDocAvailable(DocumentAvailableAdapter.docAvailable.get(i));
                        }
                    }
                }
            }
//            if (sharedPreferences.getBoolean(FIRST_CALL_SERVE_LISTENER, false) == false)
//            {
//                Log.d("DATA", "Premier appel. Tache darriere plan");
//                for(int i = 0; i < listDocuments.size(); i++)
//                {
//                    db.userDao().insertNewDocAvailable(listDocuments.get(i));
//                }
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean(FIRST_CALL_SERVE_LISTENER, true).apply();
//            }
//            else
//            {
//                Log.d("DATA", "Autre appel. Tache darriere plan");
//                List<DocumentAvailable> list = db.userDao().selectListDocAvailable();
//
//                Log.d("DATA", "Autre appel. Tache darriere plan.List taille: " + list.size()
//                        + " Liste document taille: " + listDocuments.size());
//                if (list.size() != listDocuments.size())
//                {
//                    for(int i = 0; i < listDocuments.size(); i++)
//                    {
//                        if(i < listDocuments.size() - 1)
//                        {}
//                        else
//                        {
//                            db.userDao().insertNewDocAvailable(listDocuments.get(i));
//                        }
//                    }
//                }
//            }

            //List<DocumentAvailable> docList = db.userDao().selectListDocAvailable();


            db.close();
            return null;
        }

//        @Override
//        protected void onPostExecute(List<DocumentAvailable> documentAvailables) {
//            super.onPostExecute(documentAvailables);
//
//            documentAvailableAdapter = new DocumentAvailableAdapter(listDocuments);
//            recyclerView.setAdapter(documentAvailableAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//        }
    }

    public class LoadData extends AsyncTask<Void, Void, List<DocumentAvailable>>
    {
        @Override
        protected List<DocumentAvailable> doInBackground(Void... voids) {

            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PersonnalizeDatabase.class, "personnalize").build();

            List<DocumentAvailable> documentAvailableList = db.userDao().selectListDocAvailable();

            db.close();
            return documentAvailableList;
        }

        @Override
        protected void onPostExecute(List<DocumentAvailable> documentAvailables) {
            super.onPostExecute(documentAvailables);

            if(documentAvailableAdapter != null)
            {
                for(int i = 0; i < documentAvailables.size(); i++)
                {
                    DocumentAvailableAdapter.docAvailable.add(documentAvailables.get(i));
                }
                documentAvailableAdapter.notifyDataSetChanged();
            }

//            if(documentAvailables.size() != 0)
//            {
//                recyclerView.setVisibility(View.VISIBLE);
//                documentAvailableAdapter = new DocumentAvailableAdapter(documentAvailables);
//                recyclerView.setAdapter(documentAvailableAdapter);
//                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                recyclerView.setHasFixedSize(true);
//                recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//
//            }
        }
    }

    public class SaveDataUpdate extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PersonnalizeDatabase.class, "personnalize").build();

            for (int i = 0; i < documentUpdate.size(); i++)
            {
                db.userDao().insertNewDocAvailable(documentUpdate.get(i));
            }

            db.close();

            return null;
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CheckNewDocument.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
