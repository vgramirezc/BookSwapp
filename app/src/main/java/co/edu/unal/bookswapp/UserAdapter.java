package co.edu.unal.bookswapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by vr on 11/29/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{

    private ArrayList<ChatInfo> mUsers;
    private RecyclerViewListener itemListener;

    public UserAdapter ( ArrayList<ChatInfo> users, RecyclerViewListener listener ) {
        mUsers = users;
        itemListener = listener;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_user_chat, parent, false ) );
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.textViewName.setText( mUsers.get( position ).getUser() );
        holder.textViewLastMessage.setText( mUsers.get( position ).getLastMessage() );
        Glide.with(holder.imageView.getContext())
                .load( mUsers.get( position ).getUserImageUri() )
                .into( holder.imageView );
    }

    @Override
    public int getItemCount() {
        return (mUsers == null) ? 0 : mUsers.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textViewName;
        public TextView textViewLastMessage;

        public UserHolder( View itemView ) {
            super( itemView );
            imageView = (ImageView) itemView.findViewById( R.id.iv_chat );
            textViewName = (TextView) itemView.findViewById( R.id.tv_chat_name );
            textViewLastMessage = (TextView) itemView.findViewById( R.id.tv_chat_last_message );
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View view) {
            itemListener.onItemClick( view, this.getAdapterPosition() );
        }
    }

}
