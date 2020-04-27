package com.example.nischay.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class WriteBlogActivity extends AppCompatActivity {

    EditText m_user_input_story,m_user_input_heading;
    Button m_add_btn;
    private DatabaseReference mDatabase;
    Blog entry;
    String uploaded_image = "";
    String image_path_database = "";

    // views for button
    private Button btnSelect, btnUpload;
    // view for image view
    private ImageView imageView;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_blog);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Up Button Enabling
        ActionBar supportActionBar = this.getSupportActionBar();
        if(supportActionBar!=null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        else
            Log.e("Write Vlog Activity","Action Bar Not Supported");

        entry = new Blog();

        m_add_btn = (Button) findViewById(R.id.add_btn);
        m_user_input_story = (EditText) findViewById(R.id.user_input_story);
        m_user_input_heading = (EditText) findViewById(R.id.user_input_heading);

//        Log.e("Write blog Activity","GOOOOoooooooooooooooooooooooD");
        m_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download_image_url();
                Log.e("On success","Adding data");
                if(m_user_input_heading.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Please give a header",Toast.LENGTH_SHORT).show();
                else if(m_user_input_story.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Please write something",Toast.LENGTH_SHORT).show();
                else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                        entry.setHeading(m_user_input_heading.getText().toString());
                        entry.setStory(m_user_input_story.getText().toString());
                        entry.setUser_id(user.getUid().toString());

                        download_image_url();
                        if(uploaded_image.isEmpty()){
                            Toast.makeText(getApplicationContext(),"Please press the add button again.",Toast.LENGTH_SHORT).show();
                        }else{
                            upload_data(entry);
                            finish();
                            return;
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Please sign in again",Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("Write blog act","return from function");
            }
        });



        // initialise views
        btnSelect = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.user_input_image);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SelectImage();
            }
        });

        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                uploadImage();
            }
        });

//        for(int i=1;i<=100;i++){
//            Blog b = new Blog();
//            b.setHeading("Heading Number ::= "+i);
//            b.setStory("Story Number ::= "+i);
//            upload_data(b);
//        }
    }

    private void download_image_url() {
        Log.e("Write Blog Activity",image_path_database+"  <-- Image");
        final StorageReference ref = storageReference.child("images/"+image_path_database);
        Log.e("WriteBlogActivity",image_path_database);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                uploaded_image = uri.toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast
                        .makeText(getApplicationContext(),
                                "Image Corupted.",
                                Toast.LENGTH_SHORT)
                        .show();
                Log.e("WriteBlogActivity",exception.getMessage());
            }
        });
    }


    // Select Image method
    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String image_path_tentative = UUID.randomUUID().toString();
            image_path_database = image_path_tentative;
            // Defining the child of storageReference
            final StorageReference ref
                    = storageReference
                    .child("images/"+image_path_tentative);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();

                                    Toast
                                            .makeText(getApplicationContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }


    private void upload_data(Blog BLOG) {
        if(BLOG==null)
            return;
        BLOG.setImage_url(uploaded_image);
        Log.e("Image path",uploaded_image);
        String key = mDatabase.child("users").push().getKey();
        mDatabase.child("users").child(key).setValue(BLOG);
//        mDatabase.child("users").child(key).child("image_url").setValue("https://firebasestorage.googleapis.com/v0/b/whatsapp-6572f.appspot.com/o/images%2F85f2163f-e9f5-4b10-9df9-96bb447913e2?alt=media&token=2c09a4de-8642-4aab-925a-8de45cde401b");
        Toast.makeText(getApplicationContext(),"Blog posted successfully",Toast.LENGTH_SHORT).show();
    }

//    Tool bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
