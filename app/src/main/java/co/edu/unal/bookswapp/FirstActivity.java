package co.edu.unal.bookswapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ivanc on 9/10/2017.
 */

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;

    private final String TAG = RegisterActivity.class.getSimpleName();

    private Button logOutButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        auth = FirebaseAuth.getInstance();

        logOutButton = (Button) findViewById(R.id.logOutButton);

        logOutButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if(view == logOutButton) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
