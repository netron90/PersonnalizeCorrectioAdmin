package netron90.personnalize.personnalize_co_admin;

import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nullable;

import netron90.personnalize.personnalize_co_admin.Database.DocumentAvailable;
import netron90.personnalize.personnalize_co_admin.Database.PersonnalizeDatabase;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore dbFirestore;
    public static final String FIRST_CALL_SERVE_LISTENER = "firstCallServerListener";
    private SharedPreferences sharedPreferences;
    private ListenerRegistration registration;
    private DocumentAvailable documentAvailable;
    private DocumentAvailableAdapter documentAvailableAdapter;
    private int serverFirstTimeListener = 1;
    public static boolean onCreateFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        dbFirestore  = FirebaseFirestore.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        documentAvailable = new DocumentAvailable();

        if(sharedPreferences.getBoolean(FIRST_CALL_SERVE_LISTENER, false) == true)
        {
            //TODO: LOADDOCUMENT FROM LOCAL DATABASE
            onCreateFlag = true;
            LoadData loadData = new LoadData();
            loadData.execute();

        }
        else
        {
            //TODO: MAKE A FIRST CALL LISTENER TO DOCUMENT IN SERVER
            registration = dbFirestore.collection("Document").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                    if(sharedPreferences.getBoolean(FIRST_CALL_SERVE_LISTENER, false) == false)
                    {
                        if(isOnline())
                        {
                            if(e != null)
                            {
                                Log.d("EXCEPTION FIRES LIST", "Exception occured when listening document: " + e);
                                return;
                            }

                            Log.d("CONNEXION", "connexion");
                            Log.d("DOC DATA", "Document data: " + value);
                            if(value.isEmpty())
                            {}
                            else
                            {

                                for(QueryDocumentSnapshot doc : value)
                                {
                                    Log.d("DOC DATA", "Document data: " + doc);
                                    saveFirstDocumentOnServer(doc);
                                }
                            }

                            SaveFirstDocument saveFirstDocument = new SaveFirstDocument();
                            saveFirstDocument.execute();
                        }
                        else {
                            Log.d("CONNEXION", "Pas de connexion");
                        }

                    }
                    else
                    {
                        if(isOnline())
                        {
                            if(e != null)
                            {
                                Log.d("EXCEPTION FIRES LIST", "Exception occured when listening document: " + e);
                                return;
                            }

                            Log.d("DOC DATA", "Document data: " + value);
                            if(value.isEmpty())
                            {}
                            else
                            {
                                if(serverFirstTimeListener == 1)
                                {

                                }else
                                {
                                    documentAvailable = new DocumentAvailable();
                                    for(QueryDocumentSnapshot doc : value)
                                    {
                                        saveFirstDocumentOnServer(doc);
                                    }
                                    SaveDataUpdate saveDataUpdate = new SaveDataUpdate();
                                    saveDataUpdate.execute();
                                }

                            }
                        }

                    }

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(onCreateFlag){}
        else
        {
            LoadData loadData = new LoadData();
            loadData.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        registration.remove();
    }

    private void saveFirstDocumentOnServer(QueryDocumentSnapshot doc)
    {
        if (doc.get("documetName") != null)
        {
            documentAvailable.documentName = doc.getString("documetName");
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
    }

    public class SaveFirstDocument extends AsyncTask<Void, Void, List<DocumentAvailable>>
    {
        @Override
        protected List<DocumentAvailable> doInBackground(Void... voids) {

            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PersonnalizeDatabase.class, "personnalize").build();

            db.userDao().insertNewDocAvailable(documentAvailable);

            List<DocumentAvailable> documentAvailableList = db.userDao().selectListDocAvailable();

            db.close();
            return documentAvailableList;
        }

        @Override
        protected void onPostExecute(List<DocumentAvailable> documentAvailables) {
            super.onPostExecute(documentAvailables);

            if(documentAvailables.size() != 0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                documentAvailableAdapter = new DocumentAvailableAdapter(documentAvailables);
                recyclerView.setAdapter(documentAvailableAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_CALL_SERVE_LISTENER, true).apply();
                serverFirstTimeListener++;
            }
        }
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

            if(documentAvailables.size() != 0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                documentAvailableAdapter = new DocumentAvailableAdapter(documentAvailables);
                recyclerView.setAdapter(documentAvailableAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                //TODO: ADD LISTER FOR NEW DOCUMENT

                registration = dbFirestore.collection("Document").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        if(e != null)
                        {
                            Log.d("EXCEPTION FIRES LIST", "Exception occured when listening document: " + e);
                            return;
                        }

                        Log.d("DOC DATA", "Document data: " + value);
                        if(value.isEmpty())
                        {}
                        else
                        {
                            if(serverFirstTimeListener == 1)
                            {

                            }else
                            {
                                documentAvailable = new DocumentAvailable();
                                for(QueryDocumentSnapshot doc : value)
                                {
                                    saveFirstDocumentOnServer(doc);
                                }
                                SaveDataUpdate saveDataUpdate = new SaveDataUpdate();
                                saveDataUpdate.execute();
                            }

                        }


                    }
                });
            }
        }
    }

    public class SaveDataUpdate extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PersonnalizeDatabase.class, "personnalize").build();

            db.userDao().insertNewDocAvailable(documentAvailable);

            DocumentAvailableAdapter.docAvailable.add(documentAvailable);

            db.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            documentAvailableAdapter.notifyDataSetChanged();
        }
    }

    private boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


}
