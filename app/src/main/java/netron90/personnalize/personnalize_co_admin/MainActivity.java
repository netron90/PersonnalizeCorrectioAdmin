package netron90.personnalize.personnalize_co_admin;

import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        dbFirestore  = FirebaseFirestore.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(sharedPreferences.getBoolean(FIRST_CALL_SERVE_LISTENER, false) == true)
        {

        }
        else
        {
            //TODO: MAKE A FIRST CALL LISTENER TO DOCUMENT IN SERVER
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
                        documentAvailable = new DocumentAvailable();
                        for(QueryDocumentSnapshot doc : value)
                        {
                            saveFirstDocumentOnServer(doc);
                        }
                    }
                    SaveFirstDocument saveFirstDocument = new SaveFirstDocument();
                    saveFirstDocument.execute();
                }
            });
        }

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

            return documentAvailableList;
        }

        @Override
        protected void onPostExecute(List<DocumentAvailable> documentAvailables) {
            super.onPostExecute(documentAvailables);

            if(documentAvailables.size() != 0)
            {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

}
