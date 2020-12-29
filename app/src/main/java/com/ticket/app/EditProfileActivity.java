package com.ticket.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private String editName,editSurname,editCell,editMail, editImage;
    private DatabaseReference mDatabase;
    private EditText name, surname, cell;
    private Vibrator myVib;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    static final int REQUEST_CAMERA = 1;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    private Integer SELECT_FILE=0;
    private String urlimage, mCurrentPhotoPath;
    private Bitmap bmp, bitmap;
    private ImageView profileimage;
    File photoFile = null;
    private static final String IMAGE_DIRECTORY_NAME = "TICKET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mDatabase= FirebaseDatabase.getInstance().getReference();
        myVib=(Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        final Intent intent = getIntent();
        editName = intent.getExtras().getString("Name");
        editSurname=  intent.getExtras().getString("Surname");
        editCell = intent.getExtras().getString("Cell");
        editMail = intent.getExtras().getString("Email");
        editImage = intent.getExtras().getString("Image");

        TextView mail = findViewById(R.id.mail);
        mail.setText(editMail);
        TextView tvname = findViewById(R.id.tv_name);
        tvname.setText(editName + " " + editSurname);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        name.setText(editName);
        surname.setText(editSurname);
        cell = findViewById(R.id.cell);
        cell.setText(editCell);
        profileimage = (ImageView) findViewById(R.id.profile_img);
        profileimage.setImageResource(R.drawable.ic_profile);

        if (editImage != null){
            Picasso.get()
                    .load(editImage)
                    .placeholder(R.drawable.roundloading)
                    .fit()
                    .centerCrop()
                    .into(profileimage);
        }


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
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).setValue(null);
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("email").setValue(editMail);
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("name").setValue(name.getText().toString());
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("surname").setValue(surname.getText().toString());
            mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("cell").setValue(cell.getText().toString());
            getUrlimage();
            if (urlimage != null)
            {
                mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("image").setValue(urlimage);
            } else {
                mDatabase.child("Users").child(currentFirebaseUser.getUid()).child("image").setValue(editImage);
            }
            Toast.makeText(EditProfileActivity.this, getUrlimage(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void Click(View v){
        myVib.vibrate(25);
        SelectImage();
    }
    public void ClickEdit(View v){
        myVib.vibrate(25);
        SelectImage();
    }

    private void SelectImage(){
        final CharSequence[] items={"Camera","Album", "Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    myVib.vibrate(25);
                    captureimage();
                } else if (items[i].equals("Album")) {
                    myVib.vibrate(25);
                    if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                        selectimage();
                    else
                        requeststorage();
                } else if (items[i].equals("Back")) {
                    myVib.vibrate(25);
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectimage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }
    private void requeststorage(){
        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }
    private void captureimage()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Crea il file dove dovrebbe andare l'immagine
                try {

                    photoFile = createImageFile();
                    // Continua solamente se il file è stato creato con successo
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.ticket.app.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                }
                catch (Exception ex) {
                   }
            }
            else
            {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_CAMERA){
                bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                profileimage.setImageBitmap(bmp);
                uploadCamera();

            }else if(requestCode==SELECT_FILE){
                selectedImageUri = data.getData();

                //CONVERT URI INTO BITMAP
                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
                ((Cursor) cursor).moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                bmp = BitmapFactory.decodeFile(imagePath, options);
                cursor.close(); // At the end remember to close the cursor or you will end with the RuntimeException!
                profileimage.setImageBitmap(bmp);
                uploadCamera();

            }
        }
    }
    private File createImageFile() throws IOException {
        // Creazione di un file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefisso*/
                ".jpg",         /* suffisso */
                storageDir      /* cartella */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void uploadCamera(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StorageReference ref = storageReference.child("Foto/" + UUID.randomUUID().toString());
        if (bmp!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] data = baos.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Snackbar.make(v, "Upload completed", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            setUrlimage(downloadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Snackbar.make(v, "Upload failed, retry.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Loading: "+(int)progress+"%");
                        }
                    });
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureimage();
            }
        }
        if (requestCode==1)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                selectimage();
        }
    }
    public void setUrlimage(Uri prova)
    {
        urlimage =new String(prova.toString());
    }
    public String getUrlimage()
    {
        return urlimage;
    }







}
