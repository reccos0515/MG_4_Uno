package com.example.administrator.demo1.MultiplayerChat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.administrator.demo1.R;
import java.util.List;


public class MyChatItemRecyclerViewAdapter extends RecyclerView.Adapter<MyChatItemRecyclerViewAdapter.ViewHolder> {


    private final List<Message> mValues;

    public MyChatItemRecyclerViewAdapter(Context context,List<Message> message) {
        mValues = message;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.TYPE_MESSAGE:
                layout = R.layout.fragment_chatitem;
                break;
            case Message.TYPE_LOG:
                layout = R.layout.fragment_chatitem_list;
                break;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setUsername(mValues.get(position).getUsername());
        holder.setMessage(mValues.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View Objview) {
            super(Objview);
            mView = Objview;
            mIdView = (TextView) Objview.findViewById(R.id.id);
            mContentView = (TextView) Objview.findViewById(R.id.content);
        }
        public void setUsername(String username) {
            if (null == mIdView) return;
            mIdView.setText(username);
        }

        public void setMessage(String message) {
            if (null == mContentView) return;
            mContentView.setText(message);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
