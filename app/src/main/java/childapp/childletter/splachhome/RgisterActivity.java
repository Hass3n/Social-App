package childapp.childletter.splachhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RgisterActivity extends AppCompatActivity {
    EditText email,password;
    Button btn_rgister;
    TextView log_txt;
    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    // progress bar
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgister);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Action Bar and title

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Ceate Account");

        // enable Back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // init
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        btn_rgister=findViewById(R.id.register);
        log_txt=findViewById(R.id.go_login);

        // show  progressDialog while user register
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("rgister...user");

        // handle click
        btn_rgister.setOnClickListener(new View.OnClickListener() {
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
                    RegisterUser(emial, passw);
                    
                }

            }
        });


        // handle click go log

        log_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RgisterActivity.this,LoginActivity.class));
            }
        });

    }

    private void RegisterUser(String emial, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emial, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                             progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email=user.getEmail().toString();
                            String uid=user.getUid().toString();
                            HashMap<Object,String>hashMap=new HashMap<>();
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



                            startActivity(new Intent(RgisterActivity.this, DashboradActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                             progressDialog.dismiss();
                            Toast.makeText(RgisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();  // go preious actie
        return super.onSupportNavigateUp();
    }
}
