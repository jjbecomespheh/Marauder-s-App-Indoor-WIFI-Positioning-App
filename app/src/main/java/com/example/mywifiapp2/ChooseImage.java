package com.example.mywifiapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.mywifiapp2.Point;
import com.example.mywifiapp2.ChooseFromStorage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class ChooseImage extends AppCompatActivity {
    private Uri mImageUri;
    private String URLlink;

    EditText URLEntry;
    SubsamplingScaleImageView PreviewImage;
    Button DeviceUpload, UrlUpload, ConfirmURL,ConfirmImage,ChangeImage, FirebaseUpload;
    FirebaseUser user;
    DatabaseReference database;
    StorageReference storage;

    static String IMAGE_URL = "IMAGE_URL";
    static String IMAGE_DEVICE = "IMAGE_DEVICE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        storage = FirebaseStorage.getInstance().getReference(user.getUid()).child("Upload");


        DeviceUpload = findViewById(R.id.DeviceUpload);

        FirebaseUpload = findViewById(R.id.FirebaseUpload);
        PreviewImage = (SubsamplingScaleImageView)findViewById(R.id.PreviewImage);

        ConfirmImage = findViewById(R.id.button_confirm);
        ChangeImage = findViewById(R.id.button_changeImage);


        ConfirmImage.setVisibility(View.GONE);
        ChangeImage.setVisibility(View.GONE);

        /*PreviewImage.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    float x = e.getX();
                    float y = e.getY();
                    // Send the data to the database
                    database.child("Scan").child("Scan Location").child("X").setValue(x);
                    database.child("Scan").child("Scan Location").child("Y").setValue(y);
                    Log.i("MAPPOSITION",PreviewImage.viewToSourceCoord(x,y).toString());
                    super.onLongPress(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });*/

       /*
        Either Upload a Map or choose a Map from the exisiting firebase storage
         */
        DeviceUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
                DeviceUpload.setVisibility(View.GONE);

                FirebaseUpload.setVisibility(View.GONE);
                ConfirmImage.setVisibility(View.VISIBLE);
                ChangeImage.setVisibility(View.VISIBLE);
            }
        });



        FirebaseUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseImage.this, ChooseFromStorage.class);
                startActivity(intent);
            }


        });





        ChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUpload.setVisibility(View.VISIBLE);

                FirebaseUpload.setVisibility(View.VISIBLE);

                ConfirmImage.setVisibility(View.GONE);
                ChangeImage.setVisibility(View.GONE);
            }
        });

        ConfirmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                storage = FirebaseStorage.getInstance().getReference(user.getUid()).child("Upload");
                if(mImageUri!= null){storage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "The Map has been uploaded into firebase", Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"The Map has failed to be uploaded into firebase", Toast.LENGTH_SHORT).show();
                    }
                });}

                Intent intent = new Intent(ChooseImage.this,MappingActivity.class);

                PreviewImage.buildDrawingCache();
                Bitmap bitmap_device = PreviewImage.getDrawingCache();
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap_device.compress(Bitmap.CompressFormat.PNG,50,bs);
                intent.putExtra(IMAGE_DEVICE,bs.toByteArray());
                startActivity(intent);

            }
        });
    }

    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        SubsamplingScaleImageView imageView;
        URL url;
        public LoadImage(SubsamplingScaleImageView PreviewImage){
            this.imageView = PreviewImage;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String URLlink = strings[0];
            Bitmap bitmap = null;
            try {
                if(!URLlink.isEmpty()){
                    url = new URL(URLlink);
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                //InputStream inputStream = new java.net.URL(URLlink).openStream();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //PreviewImage.setImageBitmap(bitmap);
            PreviewImage.setImage(ImageSource.bitmap(bitmap));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            PreviewImage.setImage(ImageSource.uri(mImageUri));
            //Picasso.with(this).load(mImageUri).into(PreviewImage);


        }
    }

}
