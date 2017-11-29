package co.edu.unal.bookswapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    private String offerId;

    private Offer currentOffer;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView ownerTextView;
    private TextView authorTextView;
    private TextView statusTextView;
    private ImageView bookImageView;
    private Button reserveButton;
    private Button cancelReserveButton;
    private Button startChatButton;
    private Button finishButton;

    public OfferViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfferViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferViewFragment newInstance(String param1, String param2) {
        OfferViewFragment fragment = new OfferViewFragment();
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
            offerId = getArguments().getString("offer_id");
        }
    }

    public void onResume() {
        super.onResume();
        ( (DrawerActivity) getActivity() ).mToolbar.setTitle( "" );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOffersDatabaseReference = mFirebaseDatabase.getReference().child( "offers" );
        mOffersDatabaseReference.child(offerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("data", dataSnapshot.toString());
                currentOffer = dataSnapshot.getValue(Offer.class);
                Log.i("data", currentOffer.toString());
                titleTextView.setText(currentOffer.getTitle());
                authorTextView.setText("Autor: " + currentOffer.getAuthor());
                ownerTextView.setText("Dueño: " +currentOffer.getOwnerName());
                statusTextView.setText(getRealStatus( currentOffer.getState() ) );
                descriptionTextView.setText(currentOffer.getDescription());
                Glide.with(bookImageView.getContext())
                        .load(currentOffer.getPhotoUrl())
                        .centerCrop()
                        .into(bookImageView);
                showButtons();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        descriptionTextView = (TextView) view.findViewById(R.id.description);
        titleTextView = (TextView) view.findViewById(R.id.title);
        authorTextView = (TextView) view.findViewById(R.id.author);
        ownerTextView = (TextView) view.findViewById(R.id.owner);
        statusTextView = (TextView) view.findViewById(R.id.status);
        bookImageView = (ImageView) view.findViewById(R.id.image_book);

        reserveButton = (Button) view.findViewById(R.id.reserve);
        cancelReserveButton = (Button) view.findViewById(R.id.cancel_reserve);
        startChatButton = (Button) view.findViewById(R.id.start_chat);
        finishButton = (Button) view.findViewById(R.id.finish);

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText txtUrl = new EditText(getActivity());
                txtUrl.setHint("example@gmail.com");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Hacer Reserva")
                        .setMessage("Escribe el correo del usuario con el que quieres hacer el intercambio")
                        .setView(txtUrl)
                        .setPositiveButton("Reservar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String email = txtUrl.getText().toString();
                                mOffersDatabaseReference.child(offerId).child("state").setValue(1);
                                mOffersDatabaseReference.child(offerId).child("userReservedId").setValue(email);
                                //TODO instead of email, get the id querying the bd
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

        cancelReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOffersDatabaseReference.child(offerId).child("state").setValue(0);
                mOffersDatabaseReference.child(offerId).child("userReservedId").setValue(null);
            }
        });

        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO here you go Ivan :p
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOffersDatabaseReference.child(offerId).child("state").setValue(2);
            }
        });
    }

    private void showButtons() {
        reserveButton.setVisibility(View.GONE);
        cancelReserveButton.setVisibility(View.GONE);
        startChatButton.setVisibility(View.GONE);
        reserveButton.setVisibility(View.GONE);
        finishButton.setVisibility(View.GONE);

        boolean owner = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentOffer.getOwnerId());
        if(owner) {
            if(getRealStatus( currentOffer.getState() ).equals(getString(R.string.reservation_open))) {
                reserveButton.setVisibility(View.VISIBLE);
            } else if( getRealStatus( currentOffer.getState() ).equals(getString(R.string.reservation_reserved)) ) {
                cancelReserveButton.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.VISIBLE);
            }
        } else {
            if(currentOffer.getOwnerId() != null && FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentOffer.getOwnerId())) {
                cancelReserveButton.setVisibility(View.VISIBLE);
                startChatButton.setVisibility(View.VISIBLE);
            } else {
                startChatButton.setVisibility(View.VISIBLE);
            }
        }

        /// cambiar color segun state
        if(getRealStatus( currentOffer.getState() ).equals(getString(R.string.reservation_open))) {
            statusTextView.setTextColor(Color.rgb(0, 153, 0));
        }
        if(getRealStatus( currentOffer.getState() ).equals(getString(R.string.reservation_reserved))) {
            statusTextView.setTextColor(Color.rgb(153, 76, 0));
        }
        if(getRealStatus( currentOffer.getState() ).equals(getString(R.string.reservation_close))) {
            statusTextView.setTextColor(Color.rgb(255, 0, 0));
        }

    }

    String getRealStatus(int state) {
        if(state == 0) return getString(R.string.reservation_open);
        if(state == 1) return getString(R.string.reservation_reserved);
        return getString(R.string.reservation_close);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer_view, container, false);
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
