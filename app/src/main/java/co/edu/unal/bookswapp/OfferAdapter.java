package co.edu.unal.bookswapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by vr on 11/26/17.
 */

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder>{

    private ArrayList<Offer> mOffers;
    private RecyclerViewListener itemListener;

    public OfferAdapter( ArrayList<Offer> offers, RecyclerViewListener listener ) {
        mOffers = offers;
        itemListener = listener;
    }

    @Override
    public OfferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OfferHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_item, parent, false ) );
    }

    @Override
    public void onBindViewHolder(OfferHolder holder, int position) {
        holder.textViewTitle.setText( mOffers.get( position ).getTitle() );
        holder.textViewAuthor.setText( mOffers.get( position ).getAuthor() );
        Glide.with(holder.imageView.getContext())
                .load( mOffers.get( position ).getPhotoUrl() )
                .into( holder.imageView );
    }

    @Override
    public int getItemCount() {
        return (mOffers == null) ? 0 : mOffers.size();
    }

    public class OfferHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textViewTitle;
        public TextView textViewAuthor;

        public OfferHolder( View itemView ) {
            super( itemView );
            imageView = (ImageView) itemView.findViewById( R.id.iv_list_item );
            textViewTitle = (TextView) itemView.findViewById( R.id.tv_title_list_item );
            textViewAuthor = (TextView) itemView.findViewById( R.id.tv_author_list_item );
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View view) {
            itemListener.onItemClick( view, this.getAdapterPosition() );
        }
    }

}
