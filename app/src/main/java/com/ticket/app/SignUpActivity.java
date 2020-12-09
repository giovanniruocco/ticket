package com.ticket.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {
    Button btnSignup;
    TextView btnLogin;
    EditText input_email,input_pass,name,surname, cell, confirm_pass;
    RelativeLayout activity_sign_up;
    private Vibrator myVib;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign up");
        mDatabase=FirebaseDatabase.getInstance().getReference();
        btnSignup = (Button)findViewById(R.id.signup_btn_register);
        btnLogin = (TextView)findViewById(R.id.signup_btn_login);

        input_email = (EditText)findViewById(R.id.signup_email);
        input_pass = (EditText)findViewById(R.id.signup_password);
        confirm_pass = (EditText)findViewById(R.id.confirm_password);
        name = (EditText)findViewById(R.id.signup_username);
        surname = (EditText)findViewById(R.id.signup_surname);
        cell = (EditText)findViewById(R.id.signup_cell);
        activity_sign_up = (RelativeLayout)findViewById(R.id.activity_sign_up);
        myVib=(Vibrator)this.getSystemService(VIBRATOR_SERVICE);


        btnSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                if (input_email.getText().toString().isEmpty()||!input_email.getText().toString().contains("@")||input_pass.getText().toString().isEmpty()||input_pass.getText().toString().length()<6 || !input_pass.getText().toString().equals(confirm_pass.getText().toString()) )
                {
                    Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
                else {
                    signUpUser(input_email.getText().toString(), input_pass.getText().toString());
                }
            }


        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this, "Ammor", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }


        });
        auth = FirebaseAuth.getInstance();

    }

    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();

                        }
                        else{
                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("email").setValue(input_email.getText().toString());
                            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("name").setValue(name.getText().toString());
                            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("surname").setValue(surname.getText().toString());
                            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("cell").setValue(cell .getText().toString());
                            startActivity(new Intent(SignUpActivity.this,ProfileActivity.class));
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
    }

}

