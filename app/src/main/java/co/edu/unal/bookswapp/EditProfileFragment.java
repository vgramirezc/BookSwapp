package co.edu.unal.bookswapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final int PICK_IMAGE_ID = 1;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProfileDatabaseReference;
    private DatabaseReference mUserPhotosDatabaseReference;
    private StorageReference mUserPhotosStorageReference;

    private final String TAG = RegisterActivity.class.getSimpleName();

    private EditText nameEditText;
    private ImageView profileImageView;
    private TextView ratingTextView;
    private TextView offersTextView;
    private TextView interchangeTextView;
    private TextView emailTextView;
    private EditText phoneEditText;
    private EditText cityEditText;
    private Button saveButton;
    private Profile perfil;
    private Uri mSelectedImageUri;
    private Uri mPhotoUri;

    private ProgressDialog progressDialog;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        ( (DrawerActivity) getActivity() ).mToolbar.setTitle( R.string.profile );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserPhotosStorageReference = FirebaseStorage.getInstance().getReference().child("photos").child( mAuth.getCurrentUser().getUid() );
        mUserPhotosDatabaseReference = mFirebaseDatabase.getReference().child( "photos" ).child( mAuth.getCurrentUser().getUid() );

        profileImageView = (ImageView) view.findViewById(R.id.profile_image);
        nameEditText = (EditText) view.findViewById(R.id.edit_name);
        offersTextView = (TextView) view.findViewById(R.id.offers_count);
        interchangeTextView = (TextView) view.findViewById(R.id.interchange_count);
        ratingTextView = (TextView) view.findViewById(R.id.rating_count);
        emailTextView = (TextView) view.findViewById(R.id.email_profile);
        phoneEditText = (EditText) view.findViewById(R.id.edit_phone_profile);
        cityEditText = (EditText) view.findViewById(R.id.edit_city_profile);

        saveButton = (Button) view.findViewById(R.id.button_save);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mProfileDatabaseReference = mFirebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                perfil = dataSnapshot.getValue(Profile.class);
                nameEditText.setText(perfil.getName());
                offersTextView.setText(""+perfil.getOffersCounter());
                interchangeTextView.setText(""+perfil.getInterchangesCounter());
                if(perfil.getScoresCounter() > 0)
                    ratingTextView.setText(""+ (int)(perfil.getScore() / perfil.getScoresCounter()) );
                else
                    ratingTextView.setText("--");
                emailTextView.setText(perfil.getEmail());
                phoneEditText.setText("+"+perfil.getPhone());
                cityEditText.setText(perfil.getCity());

                if(!perfil.getUrlImage().equals("")) {
                    Glide.with(profileImageView.getContext())
                            .load(perfil.getUrlImage()).
                            asBitmap().centerCrop().into(new BitmapImageViewTarget(profileImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(profileImageView.getContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            profileImageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                if(name.length()>0) perfil.setName(name);
                else perfil.setName("Nombre no disponible");
                String phone = phoneEditText.getText().toString();
                if(phone.length()>0) perfil.setPhone(phone);
                else perfil.setPhone("Telefono no disponible");
                String ciudad = cityEditText.getText().toString();
                if(ciudad.length()>0) perfil.setCity(ciudad);
                else perfil.setCity("Ciudad no disponible");
                mProfileDatabaseReference.setValue(perfil);
                if(mPhotoUri != null && !mPhotoUri.equals(""))
                    mProfileDatabaseReference.child("urlImage").setValue( mPhotoUri.toString() );
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("profile_id", mAuth.getCurrentUser().getUid());
                Fragment f = new ProfileFragment();
                f.setArguments(args);
                fragmentTransaction.replace(R.id.main_content, f).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mPhotoUri = taskSnapshot.getDownloadUrl();
                            Glide.with(profileImageView.getContext())
                                    .load(mPhotoUri)
                                    .into(profileImageView);
                        }
                    });
        }
    }

    private void selectImage(){
        Intent chooseImageIntent = ImagePicker.getPickImageIntent( getActivity() );
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
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
