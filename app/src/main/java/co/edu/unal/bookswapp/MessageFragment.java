package co.edu.unal.bookswapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment implements RecyclerViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //UI variables
    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;

    //Firebase variables
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mChatOfDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    //Other variables
    private ArrayList<ChatInfo> mUsers;
    private String mCurUserId;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
                        changeResultUsers( query.toLowerCase() );
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if( newText.length() == 0 ){
                            changeResultUsers( "" );
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        ( (DrawerActivity) getActivity() ).mToolbar.setTitle( R.string.messages );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById( R.id.rv_search );

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurUserId = mFirebaseAuth.getCurrentUser().getUid();
        mChatOfDatabaseReference = mFireBaseDatabase.getReference().child( "chats_of" );
        mUsersDatabaseReference = mFireBaseDatabase.getReference().child( "users" );

        mUsers = new ArrayList<ChatInfo>();
        initRecyclerView();
    }

    @Override
    public void onItemClick(View v, int position) {
        //Toast.makeText( getActivity(), "Chat con usuario " + mUsers.get( position ).getUser(), Toast.LENGTH_SHORT ).show();
        Log.i("Perro", mFirebaseAuth.getCurrentUser().getUid());
        Log.i("Perro2", mUsers.get( position ).getUserId());
        mChatOfDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child( mUsers.get( position ).getUserId() )

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.getValue(String.class);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        Bundle args = new Bundle();
                        Log.i("chat_chat_id", dataSnapshot.toString());
                        args.putString("chat_id", id);
                        Fragment f = new ChatFragment();
                        f.setArguments(args);
                        fragmentTransaction.replace(R.id.main_content, f).addToBackStack(null).commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    ///////////////////////////// CLASS METHODS /////////////////////////////////////////////////

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        //layoutManager.setReverseLayout( true );
        //layoutManager.setStackFromEnd( true );
        mRecyclerView.setLayoutManager( layoutManager );
        mUserAdapter = new UserAdapter( mUsers, this );
        mRecyclerView.setAdapter( mUserAdapter );
        mRecyclerView.addItemDecoration( new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        changeResultUsers( "" );
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

    private void changeResultUsers( final String query ){
        mUsers.clear();
        mUserAdapter.notifyDataSetChanged();
        mChatOfDatabaseReference.child( mCurUserId ).addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String uid = dataSnapshot.getKey();
                        //mUsersDatabaseReference.child( uid );
                        mUsersDatabaseReference.orderByChild("id").equalTo( uid ).addChildEventListener(
                                new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                        if( !map.get( "name" ).toString().contains( query ) && !map.get( "email" ).toString().contains( query ) ) return;
                                        ChatInfo chatInfo = new ChatInfo( map.get("id").toString(),  map.get( "name" ).toString(), map.get( "email" ).toString(), map.get("urlImage").toString() );
                                        mUsers.add( chatInfo );
                                        mUserAdapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    }
                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    }
                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                }
                        );
                        //Log.w( "name", user.child("name").toString() );
                        //Log.w( "url", user.child("urlImage").toString() );
                        //ChatInfo chatInfo = new ChatInfo( user.child("name").toString(), "hola", user.child("urlImage").toString() );
                        //mUsers.add( chatInfo );
                        //mUserAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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
