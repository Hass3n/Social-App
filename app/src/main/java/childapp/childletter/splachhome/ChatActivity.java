package childapp.childletter.splachhome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
;import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import childapp.childletter.splachhome.Adapter.AdapterChat;
import childapp.childletter.splachhome.Models.ModelChat;
import childapp.childletter.splachhome.Models.Modeluser;
import childapp.childletter.splachhome.notofications.ApiService;
import childapp.childletter.splachhome.notofications.Client;
import childapp.childletter.splachhome.notofications.Data;
import childapp.childletter.splachhome.notofications.Response;
import childapp.childletter.splachhome.notofications.Sender;
import childapp.childletter.splachhome.notofications.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView  nametv,userstatetv;
    ImageView profiletv;
    EditText messagEt;
    ImageButton sendBtn,attchBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    String hasUid="";
    String myid="";
    String hisimage="";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userdbReference;
    //for checking if user has seen message or not

    ValueEventListener seenListener;
    DatabaseReference userrferenceforseen;
    List<ModelChat>chatList;
    AdapterChat adapterChat;

    // notofication
    ApiService apiService;
    boolean notify=false;
    // permission constant
    private static final  int   CAMERA_REQUEST_CODE=100;
    private static final  int   STORAGE_REQUEST_CODE=200;
    private static final  int   IMAGE_PICK_GALLERY_CODE=300;
    private static final  int   IMAGE_PICK_CAMERA_CODE=400;

    // array of permssion to be request
    String cameraPermissions[];
    String storagePermissions[];

    // Uri Image
    Uri Image_uri=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //init view
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.chat_recycle);
        profiletv=findViewById(R.id.profileIv);
        nametv=findViewById(R.id.nametv);
        userstatetv=findViewById(R.id.user_state);
        messagEt=findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);
        attchBtn=findViewById(R.id.attachBtn);

        //init permission array
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions =new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //Layout for Recycle

       LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
       linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // create api service
        apiService= Client.getRetrofit("https://fcm.googleapis.com").create(ApiService.class);



      // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        User= mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        userdbReference=firebaseDatabase.getReference("User");
        // if click any item in Adpter user can get id

        Intent intent=getIntent();
        hasUid=intent.getStringExtra("hasUid");


        // search query to get info user

        Query userQuery=userdbReference.orderByChild("uid").equalTo(hasUid);
        userQuery.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot ds:dataSnapshot.getChildren()) {

                                                    String name = "" + ds.child("name").getValue();
                                                    hisimage = "" + ds.child("image").getValue();
                                                    // get value of online state
                                                    String onlineState=""+ds.child("OnlineState").getValue();
                                                    String typingStatus=""+ds.child("typingTo").getValue();

                                                    nametv.setText(name);


                                                    if(typingStatus.equals(myid)) {


                                                        userstatetv.setText("typing......");
                                                    }
                                                    else {
                                                        if(onlineState.equals("online")) {
                                                            userstatetv.setText(onlineState);
                                                        }

                                                        else {

                                                            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                                                            cal.setTimeInMillis(Long.parseLong(onlineState));
                                                            String datatime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

                                                            userstatetv.setText("Last seen :     "+datatime);

                                                        }



                                                    }





                                                    try {

                                                        Picasso.get().load(hisimage).into(profiletv);

                                                    }
                                                    catch (Exception e)
                                                    {

                                                        Picasso.get().load(R.drawable.add_a_photo).into(profiletv);
                                                    }

                                                }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });




    // handl e click send Button

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;

                String message=messagEt.getText().toString();

                if(TextUtils.isEmpty(message))
                {
                    Toast.makeText(ChatActivity.this,"message empty",Toast.LENGTH_LONG).show();



                }
                else
                {
                    SendMessage(message);

                }

                // clear edit text
                messagEt.setText("");

            }
        });


        // click attach_btn

        attchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowImagePickDialog();

            }
        });


        
        // read message
        
        readmessage();

        //seen message

        SeenMessae();

        // check edit text chang
        messagEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0)
                {

                    CheckTypeState("noOne");

                }
                else{

                    CheckTypeState(hasUid);

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });








    }


    private void readmessage() {
        chatList=new ArrayList<>();

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Chats");

         dbRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             chatList.clear();
             for (DataSnapshot ds :dataSnapshot.getChildren())
             {
                 ModelChat chat=ds.getValue(ModelChat.class);
                 if(chat.getReceiver().equals(myid)&& chat.getSender().equals(hasUid)||
                         chat.getReceiver().equals(hasUid)&& chat.getSender().equals(myid))
                 {

                     chatList.add(chat);
                 }



             }

             adapterChat=new AdapterChat(ChatActivity.this,chatList,hisimage);
             adapterChat.notifyDataSetChanged();
             recyclerView.setAdapter(adapterChat);




             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


    }

    private void SeenMessae() {

        userrferenceforseen=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=userrferenceforseen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myid)&& chat.getSender().equals(hasUid))
                    {

                        HashMap<String,Object>hashMapseen=new HashMap<>();
                        hashMapseen.put("isSeen",true);
                        ds.getRef().updateChildren(hashMapseen);



                    }


                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void SendMessage(final String message) {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        String timestamp=String.valueOf(System.currentTimeMillis());

        hashMap.put("sender",myid);
        hashMap.put("receiver",hasUid);
        hashMap.put("message",message);
        hashMap.put("timestemp",timestamp);
        hashMap.put("isSeen",false);
        hashMap.put("type","text");

        databaseReference.child("Chats").push().setValue(hashMap);




        // notification work

        String msg=message;
      DatabaseReference database=FirebaseDatabase.getInstance().getReference("User").child(myid);

      database.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              Modeluser user=dataSnapshot.getValue(Modeluser.class);
              if(notify)
              {

                  sendNotification(hasUid,user.getName(),message);
              }
              notify=false;

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });


      // Create chatlist noe/child in firebase

        final DatabaseReference chatref1=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myid)
                .child(hasUid);

         chatref1.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     if(!dataSnapshot.exists())
                     {


                         chatref1.child("id").setValue(hasUid);


                     }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        final DatabaseReference chatref2=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hasUid)
                .child(myid);

        chatref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {


                    chatref2.child("id").setValue(myid);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    // upload image  firebase
    private void SendImageMessage(Uri image_uri) throws IOException {

        notify=true;

        //progress dialog
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Sending Image.........");
        progressDialog.show();


        final String timestemp=""+System.currentTimeMillis();
        String fileNameAndPath="ChatImages/"+"post_"+timestemp;

        // get bitmap from imageuri

        Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[]data=baos.toByteArray();// convert image to byte
        StorageReference ref= FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        progressDialog.dismiss();
                        Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String dowanloaduri=uriTask.getResult().toString();
                        if(uriTask.isSuccessful())
                        {
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                            HashMap<String,Object>hashMap=new HashMap<>();

                            hashMap.put("sender",myid);
                            hashMap.put("receiver",hasUid);
                            hashMap.put("message",dowanloaduri);
                            hashMap.put("timestemp",timestemp);
                            hashMap.put("type","image");
                            hashMap.put("isSeen",false);
                            databaseReference.child("Chats").push().setValue(hashMap);

                            // send Notification
                            DatabaseReference database=FirebaseDatabase.getInstance().getReference("User").child(myid);
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Modeluser user=dataSnapshot.getValue(Modeluser.class);

                                    if(notify)
                                    {
                                        sendNotification(hasUid,user.getName(),"send photo");

                                    }
                                    notify=false;



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            // Create chatlist noe/child in firebase

                            final DatabaseReference chatref1=FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(myid)
                                    .child(hasUid);

                            chatref1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(!dataSnapshot.exists())
                                    {


                                        chatref1.child("id").setValue(hasUid);


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            final DatabaseReference chatref2=FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(hasUid)
                                    .child(myid);

                            chatref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(!dataSnapshot.exists())
                                    {


                                        chatref2.child("id").setValue(myid);


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });














                        }




                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();

            }
        });




    }

    private void sendNotification(final String Uid, final String name, final String message) {

        String t="chKOA2SW9_Y:APA91bFEZUqgUzEdFd-21rj7onXGuM0qKnLU9MixE4YM3CrO_UMj_hEjivFq5fYPttX3xBBbGmThmbjmdAlb2cSANxxzDptp-XALKa1BoC1PqaQ1k5INk62N_hqLaViVRrvHFcxp3BJU";


        DatabaseReference alltokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = alltokens.orderByKey().equalTo(Uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data=new Data(myid,name+":"+message,"New message",Uid,R.drawable.use);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.SendNotofication(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                                    Log.e("response",response.body().hashCode()+"");

                                    Toast.makeText(ChatActivity.this,response.message()+"sucess",Toast.LENGTH_LONG).show();


                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });






                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





      /*  DatabaseReference alltokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=alltokens.orderByKey().equalTo(hasUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               for (DataSnapshot ds:dataSnapshot.getChildren())
               {
                   Token token=ds.getValue(Token.class);

                   Data data=new Data(myid,name+":"+message,"New message",hasUid,R.drawable.use);
                   Sender sender=new Sender(data,token.getToken());
                   apiService.SendNotofication(sender)
                           .enqueue(new Callback<Response>() {
                               @Override
                               public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                                   Log.e("response",response.body().hashCode()+"");

                                   Toast.makeText(ChatActivity.this,response.message()+"sucess",Toast.LENGTH_LONG).show();


                               }

                               @Override
                               public void onFailure(Call<Response> call, Throwable t) {

                               }
                           });


               }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/



    }





    // checkonline state

    private void CheckOnlineState(String status)
    {

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("User").child(myid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("OnlineState",status);
        // update value of online state of current user
        dbRef.updateChildren(hashMap);



    }

    // check typing



    private void CheckTypeState(String Typing)
    {

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("User").child(myid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("typingTo",Typing);
        //update value of online state of current user
        dbRef.updateChildren(hashMap);



    }




    //CheckcurrentUser state
    public void CheckcurrentUser() {

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            myid=currentUser.getUid(); // get current id
            // stay here
        }
        else {
            // go to main act
            startActivity(new Intent(ChatActivity.this, SplachActivity.class));

        }





    }



    // check storage permission
    private boolean CheckStoragePermission()
    {
        boolean result= ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);

        return result;


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void RequeststoragePermission()
    {
        //request runtime permssion
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);

    }

    private boolean CheckCamaraPermission()
    {


        boolean result= ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);


        boolean result1= ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);

        return result && result1;


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void RequestcameraPermission()
    {
        //request runtime permssion
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);

    }

    private void ShowImagePickDialog() {

        // show dialog to choose edit  profile
        String option[]={"Camera","Gallary"};
        AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Pick Image from");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
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
        Image_uri=ChatActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);


        // Intent to start cemare
        Intent cemareIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cemareIntent.putExtra(MediaStore.EXTRA_OUTPUT,Image_uri);
        startActivityForResult(cemareIntent,IMAGE_PICK_CAMERA_CODE);

    }

    // handle Permissions
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


                        Toast.makeText(ChatActivity.this, "please enable Cemara and storage permssion ", Toast.LENGTH_LONG).show();
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


                        Toast.makeText(ChatActivity.this, "storage permssion ", Toast.LENGTH_LONG).show();
                    }


                }

            }

            break;



        }


        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK )
        {

            if (requestCode==IMAGE_PICK_GALLERY_CODE)
            {
                // image is pick from gallery,get uri image
                Image_uri=data.getData();

                // take uri image and uplaod to firebase
                try {

                    SendImageMessage(Image_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE)
            {
                // image is pick from CAMERA,get uri image

                // take uri image and uplaod to firebase
                try {

                    SendImageMessage(Image_uri);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onPause() {

        super.onPause();
        // get timestemp

        String timetemp=String.valueOf(System.currentTimeMillis());
        // set  offline and lastseen
        CheckOnlineState(timetemp);

        CheckTypeState("noOne");

        //remove message seen
        userrferenceforseen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        CheckOnlineState("online");
        super.onResume();
    }

    @Override
    protected void onStart() {
        CheckcurrentUser();
        // set online state
        CheckOnlineState("online");
        super.onStart();
    }
}
