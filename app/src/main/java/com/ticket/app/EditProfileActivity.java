package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private String editName,editSurname,editCell,editMail;
    private DatabaseReference mDatabase;
    private EditText name, cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDatabase= FirebaseDatabase.getInstance().getReference();

        final Intent intent = getIntent();
        editName = intent.getExtras().getString("Name");
        editSurname=  intent.getExtras().getString("Surname");
        editCell = intent.getExtras().getString("Cell");
        editMail = intent.getExtras().getString("Email");

        TextView mail = findViewById(R.id.mail);
        mail.setText(editMail);
        TextView tvname = findViewById(R.id.tv_name);
        tvname.setText(editName + " " + editSurname);
        name = findViewById(R.id.name);
        name.setText(editName + " " + editSurname);
        cell = findViewById(R.id.cell);
        cell.setText(editCell);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflauto = getMenuInflater();
        inflauto.inflate(R.menu.editprofile_right_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.done)
        {
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            //Toast.makeText(EditProfileActivity.this, "Cia", Toast.LENGTH_SHORT).show();
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).setValue(null);
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("email").setValue(editMail);
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("name").setValue(editName);
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("surname").setValue(editSurname);
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("cell").setValue(cell.getText().toString());
            Toast.makeText(EditProfileActivity.this, currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }






}
