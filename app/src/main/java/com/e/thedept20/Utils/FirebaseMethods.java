package com.e.thedept20.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e.thedept20.Adapters.Photo;
import com.e.thedept20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by User on 6/26/2017.
 */

public class FirebaseMethods
{

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context)
        {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if (mAuth.getCurrentUser() != null)
            {
            userID = mAuth.getCurrentUser().getUid();
            }
        }

    private void deleteOutputFile(@Nullable String uri)
        {
        if (uri != null)
        //noinspection ResultOfMethodCallIgnored
            new File(Uri.parse(uri).getPath()).delete();
        }

    public byte[] readBytes(FileInputStream inputStream) throws IOException
        {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1)
            {
            byteBuffer.write(buffer, 0, len);
            }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
        }

    private boolean isMediaVideo(String uri)
        {
        return uri.contains(".mp4") || uri.contains(".wmv") || uri.contains(".flv") || uri.contains(".avi");
        }

    private void setProfilePhoto(String url)
        {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
        }

    private String getTimestamp()
        {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
        }

    private void addPhotoToDatabase(String caption, String url)
        {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

        }

    public int getImageCount(DataSnapshot dataSnapshot)
        {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren())
            {
            count++;
            }
        return count;
        }

    /**
     * Update 'user_account_settings' node for the current user
     *
     * @param displayName
     * @param website
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber)
        {

        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if (displayName != null)
            {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
            }


        if (website != null)
            {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
            }

        if (description != null)
            {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
            }

        if (phoneNumber != 0)
            {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
            }
        }

    /**
     * update username in the 'users' node and 'user_account_settings' node
     *
     * @param username
     */
    public void updateUsername(String username)
        {
        Log.d(TAG, "updateUsername: upadting username to: " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
        }

    /**
     * update the email in the 'user's' node
     *
     * @param email
     */
    public void updateEmail(String email)
        {
        Log.d(TAG, "updateEmail: upadting email to: " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

        }

    /**
     * Register a new email and password to Firebase Authentication
     *
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username)
        {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                        {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful())
                            {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                            } else if (task.isSuccessful())
                            {
                            //send verificaton email
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                            }

                        }
                });
        }

//    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot){
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
//
//        User user = new User();
//
//        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//    }

    public void sendVerificationEmail()
        {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
            {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                            {
                            if (task.isSuccessful())
                                {

                                } else
                                {
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            }
                    });
            }
        }

    private class BackgroundGetBytesFromBitmap extends AsyncTask<String, Integer, byte[]>
    {

        @Override
        protected byte[] doInBackground(String... params)
            {
            byte[] bytes = null;

//            Bitmap bm = ImageManager.getBitmap(Uri.parse(params[0]).getPath());
            Bitmap bm = null;
            try
                {
                RotateBitmap rotateBitmap = new RotateBitmap();
                bm = rotateBitmap.HandleSamplingAndRotationBitmap(mContext, Uri.parse("file://" + params[0]));
                } catch (IOException e)
                {
                Log.e(TAG, "BackgroundGetBytesFromBitmap: IOException: " + e.getMessage());
                }

            bytes = ImageManager.getBytesFromBitmap(bm, ImageManager.IMAGE_SAVE_QUALITY);
            return bytes;
            }
    }

    /**
     * Add information to the users nodes
     * Add information to the user_account_settings node
     * @param email
     * @param username
     * @param description
     * @param website
     * @param profile_photo
     */


    /**
     * Retrieves the account settings for teh user currently logged in
     * Database: user_acount_Settings node
     * @param dataSnapshot
     * @return
     */


}








































