package netron90.personnalize.personnalize_co_admin;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import netron90.personnalize.personnalize_co_admin.Database.PersonnalizeDatabase;
import netron90.personnalize.personnalize_co_admin.Database.UserMessageDb;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText textMessage;
    private ImageView sendMessage;
    private ListenerRegistration registration;
    private FirebaseFirestore dbFirestore;
    private SharedPreferences sharedPreferences;
    private String userId;
    public static String teamId;
    private ChatMessageAdapter chatMessageAdapter = null;
    private final String MESSAGE_DATABASE = "messageDataBase";
    private UserMessage userNewMessage;
    private UserMessageDb userMessageDb;
    private boolean eventFirstLaunch = false;
    private List<UserMessageDb> listUserMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar      = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewChat);
        textMessage  = (EditText) findViewById(R.id.chat_text_msg);
        sendMessage  = (ImageView) findViewById(R.id.chat_send_btn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_title_chat));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        teamId = sharedPreferences.getString("teamId", "");
        userId = getIntent().getStringExtra("userId");

        dbFirestore = FirebaseFirestore.getInstance();

        listUserMessage = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(listUserMessage);
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textMessages = textMessage.getText().toString();
                textMessage.setText("");
                sendMessageMethod(textMessages, teamId);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getMessageListener();
        createListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        registration.remove();
    }

    private boolean isOnline()
    {
        boolean isConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
        {
            isConnected = true;
        }
        else
        {
            isConnected = false;
        }
        return isConnected;
    }

    private void sendMessageMethod(String message, String teamId)
    {
        String currentDateFormat = new SimpleDateFormat("HH:mm").format(new Date());
        UserMessage userMessage = new UserMessage(message, teamId, currentDateFormat);
        dbFirestore.collection("Message").add(userMessage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //textMessage.setText("");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful())
                        {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, getString(R.string.chat_send_message_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void getMessageListener()
//    {
//        registration = dbFirestore.collection("Message")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
//
//                        if(e != null)
//                        {
//                            return;
//                        }
//
//                        if(isOnline())
//                        {
//                            if(chatMessageAdapter == null)
//                            {
//                                Log.d("FIRST CALL", "First call: Adaptater null");
//                                Log.d("FIRST CALL", "Snapshot size: " + snapshots);
//                                if(!snapshots.isEmpty())
//                                {
//                                    Log.d("FIRST CALL", "First call: Load data");
//                                    loadDataFromFirebase(snapshots);
//                                }
//
//                            }
//                            else {
//                                //TODO: CHECK IF SOURCE OF EVENT COME FROM SERVE
//                                Log.d("NEW DOCUMENT", "One document just written on server");
//                                getOneNewMessage(snapshots);
//                            }
//                        }
//                        else
//                        {
//                            if(chatMessageAdapter == null)
//                            {
//                                //TODO: LOAD DATA FROM LOCAL DATABASE
//                                LoadDataFromLocalDb loadDataFromLocalDb = new LoadDataFromLocalDb();
//                                loadDataFromLocalDb.execute();
//                            }
//                        }
//                    }
//                });
//    }

//    private void createListener()
//    {
//        registration = dbFirestore.collection("Message").orderBy("messageTime", Query.Direction.ASCENDING)
//                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
//                        if(e != null)
//                        {
//                            return;
//                        }
//
//                        if(isOnline() && !snapshots.isEmpty())
//                        {
//                            //if(chatMessageAdapter != null && snapshots.size() == ChatMessageAdapter.listUserMessage.size())
////                            {
////                                Log.d("IS ONLINE", "No data change");
////                            }
////                            else
////                            {
//                                Log.d("IS ONLINE", "Is on line work");
//                                for(DocumentChange dc : snapshots.getDocumentChanges())
//                                {
//                                    switch (dc.getType())
//                                    {
//                                        case ADDED:
//                                            Log.d("userId", "UserId: " + dc.getDocument().get("userId") + " userId on phone: " + userId + " teamId on phone " + teamId);
//                                            userMessageDb = getNewChatMessage(dc.getDocument());
//                                            Log.d("Data", "Data: " + dc.getDocument().getData());
//                                            ChatMessageAdapter.listUserMessage.add(getNewChatMessage(dc.getDocument()));
//
//                                            break;
//                                    }
//                                }
//                                chatMessageAdapter.notifyDataSetChanged();
//                                Log.d("Adaptater size", "Taille adapter: " + ChatMessageAdapter.listUserMessage.size());
//                                SaveMessageInLocalDatabase saveMessageInLocalDatabase = new SaveMessageInLocalDatabase();
//                                saveMessageInLocalDatabase.execute();
//
//
//                        }
//                        else
//                        {
//                            LoadDataFromLocalDb loadDataFromLocalDb = new LoadDataFromLocalDb();
//                            loadDataFromLocalDb.execute();
//                        }
//                    }
//                });
//    }

    private void createListener()
    {


        registration = dbFirestore.collection("Message").orderBy("messageTime", Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable final QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                        //TODO: GET DATA FROM FIREBASE SERVER
                        if(isOnline())
                        {
                            getDataFromFirebaseServer(snapshots);
                        }
                        else
                        {
                            getDataFromFirebaseCache(snapshots);
                        }
                    }
                });

    }





    private UserMessageDb getNewChatMessage(QueryDocumentSnapshot dc)
    {
        UserMessageDb userMessage = new UserMessageDb();
        if(dc.get("userId") != null &&
                (dc.getString("userId").equals(userId) || dc.getString("userId").equals(teamId)))
        {
            String textMessage = "";
            String author = "";
            String time = "";

            if(dc.get("userTextMessage") != null)
            {
                textMessage = dc.getString("userTextMessage");
                Log.d("TextMessage", "Text Message: " + textMessage);
            }
            if(dc.get("userId") != null)
            {
                author = dc.getString("userId");
                Log.d("userId", "UserId: " + author);
            }
            if(dc.get("messageTime") != null)
            {
                time = dc.getString("messageTime");
                Log.d("messageTime", "messageTime: " + time);
            }

            userMessage.userTextMessage = textMessage;
            userMessage.userId = author;
            userMessage.messageTime = time;
        }
        else
        {
            Log.d("GET ONE NEW MSG", "No correspondance");
        }

        return userMessage;
    }

    private class SaveMessageInLocalDatabase extends AsyncTask<Void,Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {

            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PersonnalizeDatabase.class, "personnalize").build();

            //db.userDao().insertMessageUser(userMessageDb);

            if(sharedPreferences.getBoolean("firstMessage", false) == false)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstMessage", true).apply();
                for(int i = 0; i < listUserMessage.size(); i++)
                {
                    db.userDao().insertMessageUser(listUserMessage.get(i));
                }

            }
            else
            {
                //db.userDao().insertMessageUser(userMessageDb);
                List<UserMessageDb> listLocal = db.userDao().selectAllMessage();
                if(listLocal.size() != ChatMessageAdapter.listUserMessage.size())
                {
                    for(int i = 0; i < ChatMessageAdapter.listUserMessage.size(); i++)
                    {
                        if(i < ChatMessageAdapter.listUserMessage.size() - 1)
                        {}
                        else {
                            db.userDao().insertMessageUser(ChatMessageAdapter.listUserMessage.get(i));
                        }
                    }
                }
            }


            db.close();

            return null;
        }
    }

    private void getOneNewMessage(QuerySnapshot snapshots)
    {

//        if(snapshots.getMetadata().hasPendingWrites() == true)
//        {
            for(DocumentChange dc : snapshots.getDocumentChanges())
            {
                Log.d("NEW DOCUMENT", "One document just written on server");
                switch (dc.getType())
                {
                    case ADDED:
                        userMessageDb = null;
                        userMessageDb = getNewChatMessage(dc.getDocument());
                        ChatMessageAdapter.listUserMessage.add(userMessageDb);
                        chatMessageAdapter.notifyDataSetChanged();
                        SaveMessageInLocalDatabase saveMessageInLocalDatabase = new SaveMessageInLocalDatabase();
                        saveMessageInLocalDatabase.execute();
                        break;
                }
            }
        //}
    }

    private void loadDataFromFirebase(QuerySnapshot snapshots)
    {
        Log.d("ADAPTER NULL", "LoadDataFromFirebase Method fire");
        List<UserMessageDb> userMessagesList = new ArrayList<>();
        List<UserMessageDb> userMessagesListNew = new ArrayList<>();

        for(QueryDocumentSnapshot doc : snapshots)
        {
            userMessagesList.add(getNewChatMessage(doc));
            if(sharedPreferences.getBoolean("firstMessage", false) == false)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstMessage", true).apply();
                userMessageDb = getNewChatMessage(doc);
                SaveMessageInLocalDatabase saveMessageInLocalDatabase = new SaveMessageInLocalDatabase();
                saveMessageInLocalDatabase.execute();
            }else{}
        }
        for(int i = userMessagesList.size(); i > 0; i--)
        {
            userMessagesListNew.add(userMessagesList.get(i-1));
        }

        chatMessageAdapter = new ChatMessageAdapter(userMessagesListNew);
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public class LoadDataFromLocalDb extends AsyncTask<Void, Void, List<UserMessageDb>>
    {
        @Override
        protected List<UserMessageDb> doInBackground(Void... voids) {

            final PersonnalizeDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PersonnalizeDatabase.class, "personnalize").build();

//            List<UserMessageDb> userMessagesList = new ArrayList<>();
//            List<UserMessageDb> userMsgList = db.userDao().selectAllMessage();
//
//            for(int i = userMsgList.size(); i > 0; i--)
//            {
//                userMessagesList.add(userMsgList.get(i-1));
//            }

            List<UserMessageDb> userMessagesList;
            userMessagesList = db.userDao().selectAllMessage();

            return userMessagesList;
        }
        @Override
        protected void onPostExecute(List<UserMessageDb> userMessages) {
            super.onPostExecute(userMessages);

//            chatMessageAdapter = new ChatMessageAdapter(userMessages);
//            recyclerView.setAdapter(chatMessageAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
            if(chatMessageAdapter == null)
            {
                chatMessageAdapter = new ChatMessageAdapter(userMessages);
                recyclerView.setAdapter(chatMessageAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
            else {
                for(int i = 0; i < userMessages.size(); i++)
                {
                    ChatMessageAdapter.listUserMessage.add(userMessages.get(i));
                }
                chatMessageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getDataFromFirebaseServer(QuerySnapshot snapshots)
    {
        final QuerySnapshot snapshot = snapshots;
        dbFirestore.enableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {



                if(snapshot != null && !snapshot.isEmpty())
                {
                    if(ChatMessageAdapter.listUserMessage.size() != snapshot.size())
                    {
                        for(DocumentChange dc : snapshot.getDocumentChanges())
                        {
                            if(dc.getType() == DocumentChange.Type.ADDED)
                            {
                                ChatMessageAdapter.listUserMessage.add(getNewChatMessage(dc.getDocument()));
                            }
                        }
                        chatMessageAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    private void getDataFromFirebaseCache(QuerySnapshot snapshots)
    {
        final QuerySnapshot snapshot = snapshots;

        dbFirestore.disableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(ChatMessageAdapter.listUserMessage.size() != snapshot.size())
                {
                    for(DocumentChange dc : snapshot.getDocumentChanges())
                    {
                        if(dc.getType() == DocumentChange.Type.ADDED)
                        {
                            ChatMessageAdapter.listUserMessage.add(getNewChatMessage(dc.getDocument()));
                        }
                    }
                    chatMessageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
