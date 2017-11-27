package co.edu.unal.bookswapp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by ivanc on 9/10/2017.
 */

public class SearchActivity extends AppCompatActivity implements RecyclerViewListener {

    //constants
    private final static int MAX_CNT_RESULTS = 100;

    //UI variables
    private RecyclerView mRecyclerView;
    private OfferAdapter mOfferAdapter;

    //Firebase variables
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mOffersDatabaseReference;

    //Other variables
    private ArrayList<Offer> mOffers;

    //////////////////////////////////// OVERRIDDEN METHODS ////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = (RecyclerView) findViewById( R.id.rv_search );

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mOffersDatabaseReference = mFireBaseDatabase.getReference().child( "offers" );

        mOffers = new ArrayList<Offer>();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        changeSearchViewTextColor(searchView);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        changeResultOffers( query.toLowerCase() );
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                //finish();
                //startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.new_offer:
                //finish();
                this.startActivity(new Intent(this, NewOfferActivity.class));
                return true;
            case R.id.my_offer:
                //finish();
                this.startActivity(new Intent(this, MyOfferActivity.class));
                return true;
            case R.id.messages:
                //finish();
                this.startActivity(new Intent(this, MessagesActivity.class));
                return true;
            case R.id.profile:
                //finish();
                this.startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.quit:
                //finish();
                this.startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return false;
    }

    @Override
    public void onItemClick(View v, int position) {
        Toast.makeText( this, "Redireccionar a offer de " + mOffers.get( position ).getTitle(), Toast.LENGTH_SHORT ).show();
    }

    ///////////////////////////// CLASS METHODS /////////////////////////////////////////////////

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager( this );
        mRecyclerView.setLayoutManager( layoutManager );
        mOfferAdapter = new OfferAdapter( mOffers, this );
        mRecyclerView.setAdapter( mOfferAdapter );
        mRecyclerView.addItemDecoration( new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        Query offersQuery = mOffersDatabaseReference.orderByChild( "state" ).equalTo( 0 ).limitToFirst( MAX_CNT_RESULTS );
        offersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mOffers.add( dataSnapshot.getValue( Offer.class ) );
                mOfferAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.BLACK);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private void changeResultOffers( final String query ){
        mOffers.clear();
        mOfferAdapter.notifyDataSetChanged();
        Query offersQuery = mOffersDatabaseReference.orderByChild( "state" ).equalTo( 0 ).limitToFirst( MAX_CNT_RESULTS );
        offersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Offer resultOffer = dataSnapshot.getValue( Offer.class );
                if( resultOffer.getTitle().toLowerCase().contains( query ) || resultOffer.getAuthor().toLowerCase().contains( query ) ) {
                    mOffers.add(resultOffer);
                    mOfferAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}