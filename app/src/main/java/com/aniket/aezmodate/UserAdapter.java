package com.aniket.aezmodate;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.aniket.aezmodate.model.ConversationBean;
import com.squareup.picasso.Picasso;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private List<ConversationBean> listdata;
    Context mContext;
    public ImageView imageView;
    public TextView textView;
    public RelativeLayout relativeLayout;
    public OnCardInfoListener onCardInfoListener;
    // RecyclerView recyclerView;
    public UserAdapter(List<ConversationBean> listdata, Context context) {
        this.listdata = listdata;
        this.mContext = context;

        try{
            this.onCardInfoListener = ((OnCardInfoListener)context);
        }
        catch (ClassCastException e){
            throw new ClassCastException(e.getMessage());
        }
    }

    public interface OnViewHolderClickedListener {
        void onViewHolderClicked();
    }

    private static OnViewHolderClickedListener mViewHolderClickListener;

    public static void setOnViewHolderClickedListener(OnViewHolderClickedListener l) {
        mViewHolderClickListener = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ConversationBean myListData = listdata.get(position);
        holder.textView.setText(myListData.getProfileName());
        Picasso.get().load(myListData.getProfileImage()).into(holder.imageView);
        holder.profileMessage.setText(myListData.getLastMessage());
        holder.lastMessageTime.setText(myListData.getLastMessageTime());

        if (myListData.isLastMessageSeen()) {
            //myListData.getProfileMessage().setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.messageSeenColor));
            holder.unseenCount.setVisibility(View.INVISIBLE);
            holder.unseenCount.setText(myListData.getUnseenMessageCount());
        } else {
            //message.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.messageUnSeenColor));
            holder.unseenCount.setText(myListData.getUnseenMessageCount());
            holder.unseenCount.setVisibility(View.VISIBLE);
        }
        //holder.imageView.setImageResource(listdata.get(position).getProfileImage());
        holder.LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("targetname", myListData.getProfileName());
                onCardInfoListener.OnCardInfoListener(intent);
            }
        });
    }

    public interface OnCardInfoListener{
        public void OnCardInfoListener(Intent intent);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView profileMessage, lastMessageTime, unseenCount;
        public ImageView imageView;
        public View LinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            this.profileMessage = (TextView) itemView.findViewById(R.id.profileMessage);
            this.lastMessageTime = itemView.findViewById(R.id.profileLastMessageTime);
            this.unseenCount = itemView.findViewById(R.id.profileUnseenMessageCount);
            LinearLayout = (LinearLayout)itemView.findViewById(R.id.rootLayout);
        }
    }
}
