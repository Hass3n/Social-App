package childapp.childletter.splachhome.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.view.CropImageView;

import java.util.HashMap;

import childapp.childletter.splachhome.SplachActivity;
import childapp.childletter.splachhome.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private  FirebaseUser User;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    FloatingActionButton fab;
    CropImageView cropImageView;

    ImageView image_t,coverIv;
     TextView name_t,email_t,phone_t;
    View view;


    String ProfileCoverImage;


    ProgressDialog pd;
    // permission constant
    private static final  int   CAMERA_REQUEST_CODE=100;
    private static final  int   STORAGE_REQUEST_CODE=200;
    private static final  int   IMAGE_PICK_GALLERY_CODE=300;
    private static final  int   IMAGE_PICK_CAMERA_CODE=400;
    Intent CamIntent, GalIntent, CropIntent ;
    // array of permssion to be request
    String cameraPermissions[];
    String storagePermissions[];

    // Uri Image
    Uri Image_uri=null;

    //storage
    private StorageReference mStorageRef;

    //path where image_cover are store
    String storage_path="User_profile_cover_Image/";


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //init permission
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions =new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pd=new ProgressDialog(getActivity());
        User= mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=   firebaseDatabase.getReference("User");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // init array of  permssion
        // init View
        image_t=view.findViewById(R.id.avataiv);
        coverIv=view.findViewById(R.id.coverIv);
        name_t=view.findViewById(R.id.name_u);

        email_t=view.findViewById(R.id.mail_u);
        phone_t=view.findViewById(R.id.phone_u);
        fab=view.findViewById(R.id.fab);
        // make query to make current user profile
        Query query=databaseReference.orderByChild("email").equalTo(User.getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {

                    // read data
                    String name=""+ds.child("name").getValue();
                    String mail=""+ds.child("email").getValue();
                    String phone=""+ds.child("phone").getValue();
                    String image=""+ds.child("image").getValue();
                    String co_image=""+ds.child("cover").getValue();

                    // put data in view
                     name_t.setText(name);
                    email_t.setText(mail);
                    phone_t.setText(phone);

                    try {

                        Picasso.get().load(image).into(image_t);

                    }
                    catch ( Exception e)
                    {
                        Picasso.get().load(R.drawable.add_a_photo).into(image_t);


                    }

                    try {

                        Picasso.get().load(co_image).into(coverIv);

                    }
                    catch ( Exception e)
                    {
                        Picasso.get().load(R.drawable.add_a_photo).into(coverIv);


                    }




                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // handle fabactionbutton

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View C) {

                ShowEditprofileDialog();

            }
        });


        return view;
    }

    // check storage permission
    private boolean CheckStoragePermission()
    {
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);

        return result;


    }

    private void RequeststoragePermission()
    {
      //request runtime permssion
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);

    }

    private boolean CheckCamaraPermission()
    {


        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);


        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);

        return result && result1;


    }

    private void RequestcameraPermission()
    {
        //request runtime permssion
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);

    }


    private void ShowEditprofileDialog() {

        // show dialog to choose edit  profile
        String option[]={"Edit profile picture","Edit coVer photo","Edit Name","Edit phone"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0)
                {

                   pd.setMessage("Updating profile picture");
                    ProfileCoverImage="image";
                   ShowImagePickDialog();
                }

                else if (which==1)
                {
                    pd.setMessage("Updating  coVer photo");
                    ProfileCoverImage="cover";
                    ShowImagePickDialog();


                }

                else if (which==2)
                {
                    pd.setMessage("Updating Name ");
                    ShowNamePhoneUpdatDialog("name");

                }

                else if (which==3)
                {

                    pd.setMessage("Updating  phone");
                    ShowNamePhoneUpdatDialog("phone");
                }


            }
        });


           builder.create().show();

    }

    private void ShowNamePhoneUpdatDialog(final String key) {


        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("update"+key);

        //set layout Manger

        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText editText=new EditText(getActivity());
        editText.setHint("enter"+key);

        editText.setMinEms(10);
        linearLayout.setPadding(10,10,10,10);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

             String value=editText.getText().toString().trim();
             if (!TextUtils.isEmpty(value))
             {
                pd.show();
                HashMap<String,Object> result=new HashMap<>();
                result.put(key,value);
                databaseReference.child(User.getUid()).updateChildren(result)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                              pd.dismiss();
                              Toast.makeText(getActivity(),"updated"+key,Toast.LENGTH_LONG).show();


                            }

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                      pd.dismiss();
                        Toast.makeText(getActivity(),e.getMessage()+"",Toast.LENGTH_LONG).show();


                    }
                });

             }
             else {

                 Toast.makeText(getActivity(),"please enter your"+key,Toast.LENGTH_LONG).show();
             }


            }
        });

        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                pd.dismiss();

            }
        });
        // create and show dialog

        builder.create().show();




    }

    private void ShowImagePickDialog() {

        // show dialog to choose edit  profile
        String option[]={"Camera","Gallary"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image from");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0)
                {
                    // Camera clicked
                    if (!CheckCamaraPermission())
                    {
                        RequestcameraPermission();
                    }
                    else
                    {
                        PickFromCamera();
                    }

                }

                else if (which==1)
                {
                   //Gallary clicked

                    if (!CheckStoragePermission())
                    {
                        RequeststoragePermission();
                    }
                    else{

                        PickFromGallary();
                    }

                }



            }
        });
        builder.create().show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // this method call  when user press allow or Deny && handle

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {

                // picking from camera check camera and storage permssion allow

                if (grantResults.length > 0) {

                    boolean CameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (CameraAccept && StorageAccept) {
                        //PERMISSION ENABLE

                         PickFromCamera();

                    } else {


                        Toast.makeText(getActivity(), "please enable Cemara and storage permssion ", Toast.LENGTH_LONG).show();
                    }


                }
            }
            break;

            case  STORAGE_REQUEST_CODE:
            {


                if (grantResults.length > 0) {


                    boolean StorageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (StorageAccept) {
                        //PERMISSION ENABLE

                         PickFromGallary();

                    } else {


                        Toast.makeText(getActivity(), "storage permssion ", Toast.LENGTH_LONG).show();
                    }


                }

            }

            break;



        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         if (resultCode==RESULT_OK ) {

             if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                 // image is pick from gallery,get uri image
                 Image_uri = data.getData();

                 UploadProfileCoverPhoto(Image_uri);

             }
             if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                 // image is pick from CAMERA,get uri image

                 UploadProfileCoverPhoto(Image_uri);

             }


         }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadProfileCoverPhoto(Uri image_uri) {

        pd.show();
        Log.e("u",image_uri+"");
        String filepathName=storage_path+""+ProfileCoverImage+"_"+User.getUid();
        StorageReference storageReference=mStorageRef.child(filepathName);
        storageReference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      // image upload to storage now get uri to store in database
                        Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                             while (!uriTask.isSuccessful());
                            Uri uridowanload = uriTask.getResult();
                            Log.e("uridowanload ",uridowanload+"");

                            // check image is upload or not and uri is recieve
                            if(uriTask.isSuccessful())
                            {
                                //image upload
                                //add/update uri in database user
                                HashMap<String,Object>result=new HashMap<>();
                                result.put(ProfileCoverImage,uridowanload.toString());
                                databaseReference.child(User.getUid()).updateChildren(result)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                pd.dismiss();
                                                Toast.makeText(getActivity(),"Image update",Toast.LENGTH_LONG).show();


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity()," error updating image ",Toast.LENGTH_LONG).show();

                                    }
                                });


                            }
                             else {

                                 pd.dismiss();
                                 Toast.makeText(getActivity(),"some error",Toast.LENGTH_LONG).show();
;                            }





                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.dismiss();
                Toast.makeText(getActivity(),"some error"+e.getMessage()+"h",Toast.LENGTH_LONG).show();

            }
        });


    }

    private void PickFromGallary() {

     Intent gallaryIntent=new Intent(Intent.ACTION_PICK);
     gallaryIntent.setType("image/*");
     startActivityForResult(gallaryIntent,IMAGE_PICK_GALLERY_CODE);



    }

    private void PickFromCamera() {

        //Intent of picking image from device cemare
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        // put image uri
        Image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);


        // Intent to start cemare
        Intent cemareIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cemareIntent.putExtra(MediaStore.EXTRA_OUTPUT,Image_uri);
        startActivityForResult(cemareIntent,IMAGE_PICK_CAMERA_CODE);

    }






    //CheckcurrentUser state
    public void CheckcurrentUser()
    {

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {

            // stay here
        }
        else
        {
            // go to main act
            startActivity(new Intent(getActivity(), SplachActivity.class));

        }


    }


}
