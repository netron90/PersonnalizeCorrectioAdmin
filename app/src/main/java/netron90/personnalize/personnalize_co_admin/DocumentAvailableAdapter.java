package netron90.personnalize.personnalize_co_admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.auth.User;

import java.util.List;

import netron90.personnalize.personnalize_co_admin.Database.DocumentAvailable;

/**
 * Created by CHRISTIAN on 25/03/2019.
 */

public class DocumentAvailableAdapter extends RecyclerView.Adapter<DocumentAvailableAdapter.ViewHolder> {

    public static List<DocumentAvailable> docAvailable;
    private Context context;
    public static String userName = "";

    public DocumentAvailableAdapter(List<DocumentAvailable> docAvailable) {
        this.docAvailable = docAvailable;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.document_model_activity, null, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.documentName.setText(docAvailable.get(position).documentName);
        holder.documentPage.setText(String.valueOf(docAvailable.get(position).pageNumber));

        if(docAvailable.get(position).miseEnForme == true)
            holder.documentMiseEnForme.setText("Oui");
        else
            holder.documentPage.setText("Non");

        if(docAvailable.get(position).powerPoint == true)
            holder.documentPowerPoint.setText("Oui");
        else
            holder.documentPowerPoint.setText("Non");

        holder.deliveryDate.setText(docAvailable.get(position).deliveryDate);

    }

    @Override
    public int getItemCount() {
        return docAvailable.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView documentName, documentPage, documentPowerPoint, documentMiseEnForme,
        deliveryDate;
        private RelativeLayout docReady, docPaid, userDetail, diapoDetail, docChat;

        public ViewHolder(View itemView) {
            super(itemView);

            documentName        = (TextView) itemView.findViewById(R.id.document_name);
            documentPage        = (TextView) itemView.findViewById(R.id.document_pages);
            documentMiseEnForme = (TextView) itemView.findViewById(R.id.switch_mise_en_forme_option);
            documentPowerPoint  = (TextView) itemView.findViewById(R.id.switch_power_point_option);
            deliveryDate        = (TextView) itemView.findViewById(R.id.text_date);

            docReady            = (RelativeLayout) itemView.findViewById(R.id.docment_ready);
            docPaid             = (RelativeLayout) itemView.findViewById(R.id.document_paid);
            userDetail          = (RelativeLayout) itemView.findViewById(R.id.user_detail);
            diapoDetail         = (RelativeLayout) itemView.findViewById(R.id.diapo_detail);
            docChat             = (RelativeLayout) itemView.findViewById(R.id.doc_chat);


            userDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seeDocumentInfo(getLayoutPosition());
                }
            });

            docReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    docReadyFinish(getLayoutPosition());
                }
            });

            docPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    docPaidFinish(getLayoutPosition());
                }
            });

            docChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userName = docAvailable.get(getLayoutPosition()).nameUser;
                    Intent messageIntent = new Intent(context, ChatActivity.class);
                    messageIntent.putExtra("userId", docAvailable.get(getLayoutPosition()).userId);
                    context.startActivity(messageIntent);
                }
            });

        }
    }

    private boolean isOnline()
    {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            isConnected = true;
        else
            isConnected = false;

        return isConnected;
    }
    private void seeDocumentInfo(int position)
    {
        MainActivity.onCreateFlag = false;
        Intent intent = new Intent(context, UserDetail.class);
        intent.putExtra(UserDetail.USER_NAME_KEY, docAvailable.get(position).nameUser);
        intent.putExtra(UserDetail.USER_EMAIL_KEY, docAvailable.get(position).emailUser);
        intent.putExtra(UserDetail.USER_PHONE_KEY, docAvailable.get(position).phoneUser);
        context.startActivity(intent);

    }

    private void docReadyFinish(int position)
    {
        final int layoutPosition = position;
        if(isOnline())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Confirmation correction terminée")
                    .setMessage("Cette action informera l'utilisateur de la finition de" +
                            " correction de son document. Voulez-vous poursuivre?")
                    .setNegativeButton("Non", null)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final DocumentReference docRef = MainActivity.dbFirestore.collection("Document")
                                    .document(docAvailable.get(layoutPosition).docRef);

                            docRef.update("docEnd", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Confirmation envoyée avec succès", Toast.LENGTH_SHORT).show();
                                            docRef.update("teamId", MainActivity.sharedPreferences.getString("teamId", ""))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Team Id sent", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Team Id not sent", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Impossible d'effectuer cette action. Serveur indisponible. Réessayer plus tard.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
            builder.create();
            builder.show();
        }
        else
        {
            Toast.makeText(context, "Vérifier votre connexion internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void docPaidFinish(int position)
    {
        final int layoutPosition = position;
        if(isOnline())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Confirmation correction terminée")
                    .setMessage("Cette action informera l'utilisateur de la finalisation" +
                            " de payement de son document. Voulez-vous poursuivre?")
                    .setNegativeButton("Non", null)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DocumentReference docRef = MainActivity.dbFirestore.collection("Document")
                                    .document(docAvailable.get(layoutPosition).docRef);

                            docRef.update("documentPaid", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Confirmation envoyée avec succès", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Impossible d'effectuer cette action. Serveur indisponible. Réessayer plus tard.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
            builder.create();
            builder.show();
        }
        else
        {
            Toast.makeText(context, "Vérifier votre connexion internet.", Toast.LENGTH_SHORT).show();
        }
    }
}
