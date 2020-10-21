package childapp.childletter.splachhome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.TextView;

import childapp.childletter.splachhome.Fragment.ChatListFragment;
import childapp.childletter.splachhome.Fragment.HomeFragment;
import childapp.childletter.splachhome.Fragment.ProfileFragment;
import childapp.childletter.splachhome.Fragment.UserFragment;
import childapp.childletter.splachhome.notofications.Token;

public class DashboradActivity extends AppCompatActivity {
    private TextView mTextMessage;
    ActionBar actionBar;
    String mUID;


    private FirebaseAuth mAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.setTitle("Home");
                    HomeFragment homeFragment=new HomeFragment();
                    FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content,homeFragment,"").commit();
                    return true;
                case R.id.navigation_dashboard:
                    actionBar.setTitle("Profile");
                    ProfileFragment profileFragment=new ProfileFragment();
                    FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,profileFragment,"").commit();

                    return true;
                case R.id.navigation_notifications:
                    actionBar.setTitle("User");
                    UserFragment userFragment=new UserFragment();
                    FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content,userFragment,"").commit();
                    return true;
                case R.id.navigation_chat:
                    actionBar.setTitle("Chat");
               ChatListFragment chatFragment=new ChatListFragment();
                    FragmentTransaction ft4=getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.content,chatFragment,"").commit();






            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashborad);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        actionBar=getSupportActionBar();

        actionBar.setTitle("Home");
        HomeFragment homeFragment=new HomeFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content,homeFragment,"").commit();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        // update Token
        CheckcurrentUser();

        UpdateToken(FirebaseInstanceId.getInstance().getToken());




    }

    @Override
    protected void onResume() {
        CheckcurrentUser();
        super.onResume();
    }

    public void UpdateToken(String token)
    {

            if(mUID!=null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
                Token mtoken = new Token(token);
                ref.child(mUID).setValue(mtoken);
            }



    }


    //CheckcurrentUser state
    public void CheckcurrentUser()
    {

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {

            // stay here
            mUID=currentUser.getUid();
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",mUID);

            editor.apply();


        }
        else
        {
            // go to main act
            startActivity(new Intent(DashboradActivity.this, SplachActivity.class));

        }


    }

    @Override
    public void onStart() {
        super.onStart();
        CheckcurrentUser();

    }



}
