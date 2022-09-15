package com.example.intouch.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.intouch.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfilePicture {

    public interface PictureListener {
        void onProfilePictureUpdated();
    }

    Uri filePath;
    FirebaseUser mUser;
    Uri userPhotoURL;
    PictureListener pictureListener;
    Context context;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    StorageReference storageReference;

    ProfilePicture(FirebaseUser mUser, Context context){
        this.mUser = mUser;
        this.context = context;
        this.userPhotoURL = null;
        this.filePath = null;
        this.pictureListener = null;
    }

    ProfilePicture(Uri filePath, Uri userPhotoURL, FirebaseUser mUser, PictureListener pictureListener, Context context){
        this.filePath = filePath;
        this.userPhotoURL = userPhotoURL;
        this.mUser = mUser;
        this.pictureListener = pictureListener;
        this.context = context;

        sharedpreferences = context.getSharedPreferences(MainActivity.MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void uploadImage(OnCompleteListener<Void> onCompleteListener){
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            String uid = mUser.getUid();
            StorageReference ref = storageReference.child("users/" + uid + "/profile_image");

            if (ref != null) {
                Toast.makeText(context, "" + ref, Toast.LENGTH_SHORT).show();
            }

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Reference to default image file in Cloud Storage
                            // refactor this
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateUserProfilePicture(uri, onCompleteListener);
                                    progressDialog.dismiss();
                                }
                            });
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

        }

    }


    public void updateUserProfilePicture(final Uri uri, OnCompleteListener<Void> onCompleteListener) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        mUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(onCompleteListener);
    }
}
