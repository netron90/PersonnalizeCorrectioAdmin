package netron90.personnalize.personnalize_co_admin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import netron90.personnalize.personnalize_co_admin.Database.DocumentAvailable;

/**
 * Created by CHRISTIAN on 25/03/2019.
 */

public class DocumentAvailableAdapter extends RecyclerView.Adapter<DocumentAvailableAdapter.ViewHolder> {

    private List<DocumentAvailable> docAvailable;

    public DocumentAvailableAdapter(List<DocumentAvailable> docAvailable) {
        this.docAvailable = docAvailable;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.document_model_activity, null, false);

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

        }
    }
}
