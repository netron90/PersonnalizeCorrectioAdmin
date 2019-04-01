package netron90.personnalize.personnalize_co_admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import netron90.personnalize.personnalize_co_admin.Database.UserMessageDb;

/**
 * Created by CHRISTIAN on 30/03/2019.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    public static List<UserMessageDb> listUserMessage;
    private Context context;
    private String userName = "";

    public ChatMessageAdapter(List<UserMessageDb> listUserMessage) {
        this.listUserMessage = listUserMessage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_model, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(listUserMessage.get(position).getUserId().equals(ChatActivity.teamId))
        {
            //holder.params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //holder.paramsTime.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //holder.paramsContent.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.paramsCard.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.chatLayoutContent.setBackgroundColor(ContextCompat.getColor(context, R.color.chatBackgroundColor));
            holder.chatUserName.setText("Vous");
            holder.chatMessageContent.setText(listUserMessage.get(position).getUserTextMessage());
            holder.chatMessageTime.setText(listUserMessage.get(position).getMessageTime());
        }
        else
        {
            //holder.params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            //holder.paramsTime.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            //holder.paramsContent.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.paramsCard.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.chatLayoutContent.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLigthWhite));
            holder.chatUserName.setText(DocumentAvailableAdapter.userName);
            holder.chatMessageContent.setText(listUserMessage.get(position).getUserTextMessage());
            holder.chatMessageTime.setText(listUserMessage.get(position).getMessageTime());
        }

    }

    @Override
    public int getItemCount() {
        return listUserMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView chatUserName, chatMessageContent, chatMessageTime;
        private RelativeLayout.LayoutParams params, paramsTime, paramsContent, paramsCard;
        private CardView chatLayoutContent;

        public ViewHolder(View itemView) {
            super(itemView);

            chatUserName = (TextView) itemView.findViewById(R.id.chat_user_name);
            chatMessageContent = (TextView) itemView.findViewById(R.id.chat_content);
            chatMessageTime = (TextView) itemView.findViewById(R.id.chat_time);
            chatLayoutContent = (CardView) itemView.findViewById(R.id.cardView);
            params = (RelativeLayout.LayoutParams) chatUserName.getLayoutParams();
            paramsTime = (RelativeLayout.LayoutParams) chatMessageTime.getLayoutParams();
            paramsContent = (RelativeLayout.LayoutParams) chatMessageContent.getLayoutParams();
            paramsCard = (RelativeLayout.LayoutParams) chatLayoutContent.getLayoutParams();
        }
    }
}
