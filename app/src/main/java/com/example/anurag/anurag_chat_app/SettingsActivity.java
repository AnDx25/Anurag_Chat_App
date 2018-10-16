package com.example.anurag.anurag_chat_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mdatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mDispllay_Image;
    private TextView mName;
    private TextView mStatus;
    private Button mChangeStatus;
    private Button mChangeImage;
    final static int PICK_IMAGE = 2;
    private StorageReference mImageSource;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDispllay_Image = (CircleImageView) findViewById(R.id.circleImageView);
        mImageSource= FirebaseStorage.getInstance().getReference();
        mName = (TextView) findViewById(R.id.settings_dispalyname);
        mStatus = (TextView) findViewById(R.id.settings_status);
        mChangeStatus = (Button) findViewById(R.id.settings_status_button);
        mChangeImage = (Button) findViewById(R.id.settings_image_button);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        //now from this line all the string based data will be loaded even if app restarted it will not take
        //time to reload this is firebase offline capability which get enabled
        //but even now image will take time to load since we are not retriving the image directly from firebase
        //but we are retriving the image url and through picasso we are retriving the image
        //now to load image faster then u need to include some line of code in LapitChat class reqalted to Picasso
        mdatabase.keepSynced(true);
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("default"))
                {
                   // Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.index).fit().centerInside().into(mDispllay_Image);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.index).fit().centerInside().into(mDispllay_Image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.index).fit().centerInside().into(mDispllay_Image);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, StatusActivity.class);
                String status = mStatus.getText().toString();
                i.putExtra("status", status);
                startActivity(i);
            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();


            //Picasso.with(ControlActivity.this).load(selectedImageUri).into(mSmallTarget);

//            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mFirstFilterPreviewImageView);
            //  Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mSecondFilterPreviewImageView);
            // Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mThirdFilterPreviewImageView);
            // Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mFourthFilterPreviewImageView);
            CropImage.activity(selectedImageUri).setAspectRatio(1,1).start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog=new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("UPLOADING IMAGE...");
                mProgressDialog.setMessage("please wait while we upload your image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();
                File thumb_filepath=new File(resultUri.getPath());
                String current_uid=mCurrentUser.getUid();

                //Bitmap thumb_bitmap=new Compressor(this).compressToBitmap(thumb_filepath)

                   Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                //String filename = System.currentTimeMillis()+"profile_image.jpg";
                final StorageReference filepath=mImageSource.child("profile_picture").child(current_uid+".jpg");
                final StorageReference thumb_path=mImageSource.child("profile_picture").child("thumb").child(current_uid+"jpg");
                //uploading main image
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                      if(task.isSuccessful()) {
                          //now we want to set the image name in the firebse database section also
                          //so to do that we have to set the url to image section of the database and then
                          //display that image into main screen
                          //this is download url
                          final String download_url = task.getResult().getDownloadUrl().toString();
                          //uploading the thumb image

                          UploadTask uploadTask = thumb_path.putBytes(thumb_byte);
                          uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                  String thumb_downloadurl=thumb_task.getResult().getDownloadUrl().toString();
                                  if (thumb_task.isSuccessful()) {//storing the data if upload is successful
                      //here we are using the MAP because we want to update only the image and thumb image
                      //but if we have used the HashMap then it will set these fields only in database and remove the user and status fields
                                      Map update_HashMap=new HashMap();
                                      update_HashMap.put("image",download_url);
                                      update_HashMap.put("thumb_image",thumb_downloadurl);
                                      mdatabase.updateChildren(update_HashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful()) {
                                                  mProgressDialog.dismiss();
                                                  Toast.makeText(SettingsActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                              }

                                          }
                                      });

                                  } else {
                                      Toast.makeText(SettingsActivity.this, "Error while uploading thumb nail", Toast.LENGTH_SHORT).show();
                                      mProgressDialog.dismiss();

                                  }

                              }
                          });

                      }else
                      {
                          Toast.makeText(SettingsActivity.this, "Error while uploading", Toast.LENGTH_SHORT).show();
                          mProgressDialog.dismiss();
                      }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}



