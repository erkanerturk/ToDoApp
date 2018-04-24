package com.erkanerturk.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private ProgressBar mProgressBar;
    private boolean isPassHidden = true;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        /*
          mEmailEditText = (EditText) findViewById(R.id.emailEditText);
          mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
          mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailText = mEmailEditText.getText().toString();
                if (emailText.contains(" ")) {
                    emailText = emailText.replace(" ", "");
                    mEmailEditText.setText(emailText);
                    mEmailEditText.setSelection(emailText.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        */
    }

    @Override
    public void onStart() {
        super.onStart();
        login();
    }

    public void login() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            List<AuthUI.IdpConfig> providers = new ArrayList<>();
            providers.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
            providers.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(providers).build(), RC_SIGN_IN
            );
        }
    }

    /*
    public void showOrHidePassword(View view) {
        if (!isPassHidden) {
            mPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
            mPasswordEditText.setSelection(mPasswordEditText.getText().toString().length());
            isPassHidden = true;

        } else {
            mPasswordEditText.setTransformationMethod(null);
            mPasswordEditText.setSelection(mPasswordEditText.getText().toString().length());
            isPassHidden = false;
        }
    }

    public void singIn(View view) {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            findViewById(R.id.singInButton).setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            login();
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                    findViewById(R.id.singInButton).setEnabled(true);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void singUp(View view) {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        if (!email.equals("") && !password.equals("")) {
            findViewById(R.id.singUpButton).setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "User Created",
                                    Toast.LENGTH_LONG).show();

                            login();
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                    findViewById(R.id.singUpButton).setEnabled(true);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
    */
}
