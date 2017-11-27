package co.edu.unal.bookswapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by ivanc on 9/10/2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProfileDatabaseReference;

    private final String TAG = RegisterActivity.class.getSimpleName();

    private Button editButton;
    private TextView user;
    private TextView name;
    private TextView interests;
    private TextView score;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        editButton = (Button) findViewById(R.id.editButton);
        user = (TextView) findViewById(R.id.user);
        name = (TextView) findViewById(R.id.name);
        interests = (TextView) findViewById(R.id.interests);
        score = (TextView) findViewById(R.id.score);

        mProfileDatabaseReference = mFirebaseDatabase.getReference().child("users").child(auth.getCurrentUser().getUid());
        mProfileDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile perfil = dataSnapshot.getValue(Profile.class);
                user.setText(perfil.getEmail());
                name.setText(perfil.getName());
                interests.setText(perfil.getInterests());
                if(perfil.getNumberscores()>0){
                    double puntaje = perfil.getScore()/perfil.getNumberscores();
                    score.setText(Double.toString(puntaje));
                }else{
                    score.setText("No tiene calificación todavía");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        editButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        /*if(view == editButton) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }*/
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
                //this.startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.quit:
                //finish();
                this.startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return false;
    }
}
