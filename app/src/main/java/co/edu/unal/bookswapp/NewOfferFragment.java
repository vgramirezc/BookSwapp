package co.edu.unal.bookswapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewOfferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewOfferFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    //constants
    private static final int PICK_IMAGE_ID = 1;

    //UI variables
    private ImageButton mImageButton;
    private EditText mTitleEditText, mAuthorEditText, mDescriptionEditText;
    private ImageView mImageView;
    private Button mCreateButton;
    private ProgressBar mProgressBar;

    //Firebase variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;
    private DatabaseReference mUserPhotosDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUserPhotosStorageReference;

    //other variables
    private Uri mPhotoUri;
    private Uri mSelectedImageUri;
    private Toast mToast;


    public NewOfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewOfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewOfferFragment newInstance(String param1, String param2) {
        NewOfferFragment fragment = new NewOfferFragment();
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
        mSearchMenuItem.setVisible( false );
    }

    @Override
    public void onResume() {
        super.onResume();
        ( (DrawerActivity) getActivity() ).mToolbar.setTitle( R.string.new_offer );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageButton = (ImageButton) view.findViewById( R.id.ib_new_offer );
        mTitleEditText = (EditText) view.findViewById( R.id.et_new_offer_title );
        mAuthorEditText = (EditText) view.findViewById( R.id.et_new_offer_author );
        mDescriptionEditText = (EditText) view.findViewById( R.id.et_new_offer_description );
        mImageView = (ImageView) view.findViewById( R.id.iv_new_offer );
        mCreateButton = (Button) view.findViewById( R.id.bt_new_offer_register );
        mProgressBar = (ProgressBar) view.findViewById( R.id.pb_new_offer );

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOffersDatabaseReference = mFirebaseDatabase.getReference().child( "offers" );
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mUserPhotosStorageReference = mFirebaseStorage.getReference().child("photos").child( mFirebaseAuth.getCurrentUser().getUid() );
        mUserPhotosDatabaseReference = mFirebaseDatabase.getReference().child( "photos" ).child( mFirebaseAuth.getCurrentUser().getUid() );

        mPhotoUri = null;
        mImageView.setVisibility( View.GONE );
        mProgressBar.setVisibility( View.GONE );
        mProgressBar.getIndeterminateDrawable().setColorFilter( ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark), PorterDuff.Mode.MULTIPLY);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mTitleEditText.getText().toString().trim().length() == 0 ){
                    showToast( "El título no puede estar vacío" );
                }
                else if( mAuthorEditText.getText().toString().trim().length() == 0 ){
                    showToast( "El autor no puede estar vacío" );
                }
                else if( mDescriptionEditText.getText().toString().trim().length() == 0 ){
                    showToast( "La descripción no puede estar vacía" );
                }
                else if( mPhotoUri == null ){
                    showToast( "Debe subir una imagen del libro" );
                }
                else{
                    Offer newOffer = new Offer(mFirebaseAuth.getCurrentUser().getDisplayName(),
                            mFirebaseAuth.getCurrentUser().getUid(),
                            mTitleEditText.getText().toString().trim(),
                            mAuthorEditText.getText().toString().trim(),
                            mDescriptionEditText.getText().toString().trim(),
                            mPhotoUri.toString(), 0, null);
                    mOffersDatabaseReference.push().setValue(newOffer);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_offer, container, false);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK ){

            File imageFile = ImagePicker.getTempFile(getActivity());
            boolean isCamera = (data == null || data.getData() == null  || data.getData().toString().contains(imageFile.toString()));
            if (isCamera) mSelectedImageUri = Uri.fromFile(imageFile);
            else mSelectedImageUri = data.getData();

            String photoKey = mUserPhotosDatabaseReference.push().getKey();
            StorageReference photoRef = mUserPhotosStorageReference.child( photoKey );
            photoRef.putFile( mSelectedImageUri )
                    .addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressBar.setVisibility( View.VISIBLE );
                        }
                    })
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mPhotoUri = taskSnapshot.getDownloadUrl();
                            mImageButton.setVisibility( View.GONE );
                            mImageView.setVisibility( View.VISIBLE );
                            Glide.with(mImageView.getContext())
                                    .load(mPhotoUri)
                                    .into(mImageView);
                            mProgressBar.setVisibility( View.GONE );
                        }
                    });
        }
    }

    //////////////////////////////    CLASS METHODS    /////////////////////////////////
    private void selectImage(){
        Intent chooseImageIntent = ImagePicker.getPickImageIntent( getActivity() );
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void showToast( String message ){
        if( mToast != null ) mToast.cancel();
        mToast.makeText( getActivity(), message, Toast.LENGTH_SHORT ).show();
    }
}
