package co.edu.unal.bookswapp;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReservationsNotClosedTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReservationsNotClosedTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOfferClosedTab extends Fragment implements RecyclerViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mRecyclerView;

    //Firebase variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mOffersDatabaseReference;

    //Other variables
    private OfferAdapter mOfferAdapter;
    private ArrayList<Offer> mOffers;
    private String mCurUserId;
    private String mCurQuery;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyOfferClosedTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservationsNotClosedTab.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservationsNotClosedTab newInstance(String param1, String param2) {
        ReservationsNotClosedTab fragment = new ReservationsNotClosedTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu( true );
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        changeSearchViewTextColor(searchView);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        mCurQuery = query.toLowerCase();
                        changeResultOffers( mCurQuery );
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if( newText.length() == 0 ){
                            changeResultOffers( "" );
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById( R.id.rv_search );

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mOffersDatabaseReference = mFireBaseDatabase.getReference().child( "offers" );

        mCurUserId = mFirebaseAuth.getCurrentUser().getUid();
        mCurQuery = "";
        mOffers = new ArrayList<Offer>();
        initRecyclerView();
    }

    @Override
    public void onItemClick(View v, int position) {
        //Toast.makeText( getActivity(), "Redireccionar a offer de " + mOffers.get( position ).getTitle(), Toast.LENGTH_SHORT ).show();

        String id = mOffers.get(position).getId();
        Log.i("idOffer", mOffers.get(position).toString());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString("offer_id", id);
        Fragment f = new OfferViewFragment();
        f.setArguments(args);
        fragmentTransaction.replace(R.id.main_content, f).addToBackStack(null).commit();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        layoutManager.setReverseLayout( true );
        layoutManager.setStackFromEnd( true );
        mRecyclerView.setLayoutManager( layoutManager );
        mOfferAdapter = new OfferAdapter( mOffers, this );
        mRecyclerView.setAdapter( mOfferAdapter );
        mRecyclerView.addItemDecoration( new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        changeResultOffers( "" );
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
        Query offersQuery = mOffersDatabaseReference.orderByChild( "timestamp" );
        offersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Offer resultOffer = dataSnapshot.getValue( Offer.class );
                if( mCurUserId.equals( resultOffer.getOwnerId() ) && resultOffer.getState() == 2 ) {
                    if (resultOffer.getTitle().toLowerCase().contains(query) || resultOffer.getAuthor().toLowerCase().contains(query)) {
                        mOffers.add(resultOffer);
                        mOfferAdapter.notifyDataSetChanged();
                    }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}