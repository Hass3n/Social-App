package childapp.childletter.splachhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
// progress bar
    ProgressDialog progressDialog;
    EditText email,password;
    Button btn_login;
    TextView txt_regiser,forget_passw;
    private FirebaseAuth mAuth;
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 100;

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Login");

        // enable Back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
       // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // show  progressDialog while user register
        progressDialog=new ProgressDialog(this);
       // progressDialog.setTitle("Login");
      //  progressDialog.setMessage("Login in.......");

        //init
        email=findViewById(R.id.sign_email);
        password=findViewById(R.id.sign_password);
        btn_login=findViewById(R.id.login);
        txt_regiser=findViewById(R.id.go_register);
        forget_passw=findViewById(R.id.forget_passw);
        signInButton=findViewById(R.id.sing_google);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // handle click login

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //take input user
                String emial=email.getText().toString().trim();
                String passw=password.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(emial).matches())
                {
                    //set Error
                    email.setError("invalid email");
                    email.setFocusable(true);

                }

                else if(password.length()<6)
                {

                    //set Error
                    password.setError("password must be atleast 6 character");
                    password.setFocusable(true);


                }

                else {
                    LoginUser(emial, passw);

                }




            }
        });

        // handle

        txt_regiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RgisterActivity.class));
            }
        });

        // handle forget_passw

        forget_passw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecoverPassword();

            }
        });

        // handle click sign with googe

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

    }

    // recover password

    public void RecoverPassword()
    {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("RecoverPassword");

        //set layout Manger

        LinearLayout linearLayout=new LinearLayout(this);

        final EditText Emailhint=new EditText(this);
        Emailhint.setHint("Email");
        Emailhint.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        // mak min width
        Emailhint.setMinEms(10);
        linearLayout.setPadding(10,10,10,10);
        linearLayout.addView(Emailhint);
        builder.setView(linearLayout);
       builder.setPositiveButton("Recove", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {


               String email=Emailhint.getText().toString().trim();
               BeginRecover(email);


           }
       });

        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               dialog.dismiss();


            }
        });

             builder.create().show();

    }

    private void BeginRecover(String email) {
        progressDialog.setMessage("sending email......");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Emailsent",Toast.LENGTH_LONG).show();

                }
                else {

                    Toast.makeText(LoginActivity.this,"failed",Toast.LENGTH_LONG).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,"failed",Toast.LENGTH_LONG).show();

            }
        });
    }


    private void LoginUser(String emial, String passw) {
        progressDialog.setMessage("Login in.......");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(emial, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                              SharedPreferences sh=  getSharedPreferences("my_log",MODE_PRIVATE);
                              SharedPreferences.Editor editor=sh.edit();
                              editor.putString("id",user.getUid().toString());
                              editor.apply();
                              editor.commit();


                            startActivity(new Intent(LoginActivity.this, DashboradActivity.class));
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,e.getLocalizedMessage()+"",Toast.LENGTH_LONG).show();
            }
        });



    }

    // sing goolge
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this,"Google sign in failed"+e+"",Toast.LENGTH_LONG).show();
                //Log.w(TAG, "Google sign in failed", e);

            }
        }
    }

//firebaseAuthWithGoogle
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this,"signInWithCredential:success",Toast.LENGTH_LONG).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            //if user sign in firsttime and get info user
                            if (task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                String email=user.getEmail().toString();
                                String uid=user.getUid().toString();
                                HashMap<Object,String> hashMap=new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name","");
                                hashMap.put("OnlineState","online");
                                hashMap.put("typingTo","noOne");
                                hashMap.put("phone","");
                                hashMap.put("image","");
                                hashMap.put("cover","");
                                // Write a message to the database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("User");
                                myRef.child(uid).setValue(hashMap);




                            }


                            startActivity(new Intent(LoginActivity.this,DashboradActivity.class));
                            finish();



                        } else {

                            Toast.makeText(LoginActivity.this,"signInWithCredential:failed",Toast.LENGTH_LONG).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(LoginActivity.this,"signInWithCredential:failed"+e,Toast.LENGTH_LONG).show();


            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();  // go preious actie
        return super.onSupportNavigateUp();
    }


}
