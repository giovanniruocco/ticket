package com.ticket.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    SignInButton btnGoogleLogin;
    EditText input_email,input_password;
    TextView btnSignup,btnForgotPass;
    RelativeLayout activity_main;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase= FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_login);
        btnGoogleLogin = findViewById(R.id.googlelogin_btn);

        TextView textGOOGLE = (TextView) btnGoogleLogin.getChildAt(0);
        textGOOGLE.setText("LOGIN WITH GOOGLE          ");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignup = (TextView)findViewById(R.id.login_btn_signup);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = (Button)findViewById(R.id.login_btn_login);

        input_email = (EditText)findViewById(R.id.login_email);
        input_password = (EditText)findViewById(R.id.login_password);
        btnSignup = (TextView)findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView)findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout)findViewById(R.id.activity_main);

        auth = FirebaseAuth.getInstance();


        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //Intent intento = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
                //startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            }


        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }


        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_email.getText().toString().isEmpty() || !input_email.getText().toString().contains("@") || input_password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Something goes wrong", Toast.LENGTH_LONG).show();
                } else {
                    loginUser(input_email.getText().toString(), input_password.getText().toString());
                    }
            }

        });
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }


        });





    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.

            firebaseAuthWithGoogle(account.getIdToken());
            //FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            //auth = FirebaseAuth.getInstance();
            //mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue("blo");
            //mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue("bla");
            //mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("surname").setValue("bla");

                    startActivity(new Intent(LoginActivity.this, MyTicketsActivity.class));

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });
    }




    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            startActivity(new Intent(LoginActivity.this, MyTicketsActivity.class));
        }
        super.onStart();
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Email or password are wrong", Toast.LENGTH_LONG).show();
                        }
                        else{
                            startActivity(new Intent(LoginActivity.this,MyTicketsActivity.class));
                        }
                    }
                });
    }
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    }



}