package co.edu.unal.bookswapp;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener  {

    private FirebaseAuth auth;

    private final String TAG = RegisterActivity.class.getSimpleName();

    private EditText emailText, passwordText, confirmText;
    private Button registerButton;
    private TextView loginText;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        emailText = (EditText) findViewById(R.id.emailTextEdit);
        passwordText = (EditText) findViewById(R.id.passwordTextEdit);
        confirmText = (EditText) findViewById(R.id.confirmTextEdit);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginText = (TextView) findViewById(R.id.loginText);


        registerButton.setOnClickListener(this);
        loginText.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if(view == registerButton) {
            registerUser();
        }
        if(view == loginText) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    private void registerUser() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String confirm = confirmText.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "El correo no puede estar vacio", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "La contraseña no puede estar vacia", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!confirm.equals(password)) {
            Toast.makeText(this, "Las contraseñas no coinciden, rectifique e intente nuevamente", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Registrando usuario...");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            // TODO: start profile activity
                            Toast.makeText(RegisterActivity.this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "No se pudo registrar, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}
