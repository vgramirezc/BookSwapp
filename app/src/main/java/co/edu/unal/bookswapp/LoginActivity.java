package co.edu.unal.bookswapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {

    private FirebaseAuth auth;

    private final String TAG = RegisterActivity.class.getSimpleName();

    private EditText emailText, passwordText;
    private Button loginButton;
    private TextView registerText;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailText = (EditText) findViewById(R.id.emailTextEdit);
        passwordText = (EditText) findViewById(R.id.passwordTextEdit);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerText = (TextView) findViewById(R.id.registerText);

        loginButton.setOnClickListener(this);
        registerText.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if(view == loginButton) {
            loginUser();
        }
        if(view == registerText) {
            finish();
            //startActivity(new Intent(this, FirstActivity.class));
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void loginUser() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "El correo no puede estar vacio", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "La contraseña no puede estar vacia", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Ingresando...");
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                            // TODO: start profile activity
                            finish();
                            startActivity(new Intent(LoginActivity.this, FirstActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Correo o contraseña incorrecto, intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}