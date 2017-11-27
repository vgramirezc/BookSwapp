package co.edu.unal.bookswapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.EventLogTags;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static co.edu.unal.bookswapp.ImagePicker.getTempFile;

/**
 * Created by ivanc on 9/10/2017.
 */

public class NewOfferActivity extends AppCompatActivity{

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

    ///////////////////////       OVERRIDDEN METHODS      ///////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offer);
        mImageButton = (ImageButton) findViewById( R.id.ib_new_offer );
        mTitleEditText = (EditText) findViewById( R.id.et_new_offer_title );
        mAuthorEditText = (EditText) findViewById( R.id.et_new_offer_author );
        mDescriptionEditText = (EditText) findViewById( R.id.et_new_offer_description );
        mImageView = (ImageView) findViewById( R.id.iv_new_offer );
        mCreateButton = (Button) findViewById( R.id.bt_new_offer_register );
        mProgressBar = (ProgressBar) findViewById( R.id.pb_new_offer );

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOffersDatabaseReference = mFirebaseDatabase.getReference().child( "offers" );
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mUserPhotosStorageReference = mFirebaseStorage.getReference().child("photos").child( mFirebaseAuth.getCurrentUser().getUid() );
        mUserPhotosDatabaseReference = mFirebaseDatabase.getReference().child( "photos" ).child( mFirebaseAuth.getCurrentUser().getUid() );

        mPhotoUri = null;
        mImageView.setVisibility( View.GONE );
        mProgressBar.setVisibility( View.GONE );
        mProgressBar.getIndeterminateDrawable().setColorFilter( ContextCompat.getColor(this, android.R.color.holo_blue_dark), PorterDuff.Mode.MULTIPLY);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                //finish();
                this.startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.new_offer:
                //finish();
                //startActivity(new Intent(this, NewOfferActivity.class));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK ){

            File imageFile = ImagePicker.getTempFile(NewOfferActivity.this);
            boolean isCamera = (data == null || data.getData() == null  || data.getData().toString().contains(imageFile.toString()));
            if (isCamera) mSelectedImageUri = Uri.fromFile(imageFile);
            else mSelectedImageUri = data.getData();

            String photoKey = mUserPhotosDatabaseReference.push().getKey();
            StorageReference photoRef = mUserPhotosStorageReference.child( photoKey );
            photoRef.putFile( mSelectedImageUri )
                .addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgressBar.setVisibility( View.VISIBLE );
                    }
                })
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        Intent chooseImageIntent = ImagePicker.getPickImageIntent( NewOfferActivity.this );
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private void showToast( String message ){
        if( mToast != null ) mToast.cancel();
        mToast.makeText( NewOfferActivity.this, message, Toast.LENGTH_SHORT ).show();
    }
}