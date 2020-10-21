package childapp.childletter.splachhome.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import childapp.childletter.splachhome.Adapter.AdapterUser;
import childapp.childletter.splachhome.SplachActivity;
import childapp.childletter.splachhome.R;
import childapp.childletter.splachhome.Models.Modeluser;

import static android.content.Context.MODE_PRIVATE;


public class UserFragment extends Fragment {
    RecyclerView recyclerView;
    List<Modeluser>userlist;
    View view;

    private FirebaseAuth mAuth;
    private  FirebaseUser User;
    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView=view.findViewById(R.id.user_recycle);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        User= mAuth.getCurrentUser();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userlist=new ArrayList<>();

       getAlluser();


        return view;
    }




    public void getAlluser()
    {
        SharedPreferences sh=  getContext().getSharedPreferences("my_log",MODE_PRIVATE);
        final String id=sh.getString("id",null);

        final FirebaseUser Fuser= FirebaseAuth.getInstance().getCurrentUser();

          DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 userlist.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Modeluser modeluser=ds.getValue(Modeluser.class);

                    if(!(modeluser.getUid().equals(id))) {


                        userlist.add(modeluser);


                    }



                }

               // recyclerView
                AdapterUser adapterUser=new AdapterUser(userlist,getContext());
                recyclerView.setAdapter(adapterUser);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage()+"",Toast.LENGTH_LONG).show();

            }
        });



    }
    //show menu

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);  //to show menu option
        super.onCreate(savedInstanceState);
    }


    //Inflate layout menu

      @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate layout menu
        inflater.inflate(R.menu.menu_item,menu);
        //search view
         MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView)MenuItemCompat.getActionView(item);

        //search lisner
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if(!(TextUtils.isEmpty(query.trim())))
                {
                    searchUser(query);

                }
                else {

                    getAlluser();

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!(TextUtils.isEmpty(newText.trim())))
                {
                    searchUser(newText);

                }
                else {

                    getAlluser();

                }




                return false;
            }
        });



        super.onCreateOptionsMenu(menu,inflater);
    }



    //searchUser

    private void searchUser(final String s) {
        SharedPreferences sh=  getContext().getSharedPreferences("my_log",MODE_PRIVATE);
        final String id=sh.getString("id",null);


        final FirebaseUser Fuser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {

                    Modeluser modeluser=ds.getValue(Modeluser.class);
                    //get user ecept current user
                    // not do

                       if(!(modeluser.getUid().equals(id))) {
                           if (modeluser.getName().toLowerCase().contains(s.toLowerCase())) ;
                           {

                               userlist.add(modeluser);


                           }


                       }

                }

                // recyclerView
                AdapterUser adapterUser=new AdapterUser(userlist,getContext());

                //refresh   Adapter
                adapterUser.notifyDataSetChanged();

                recyclerView.setAdapter(adapterUser);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),databaseError.getMessage()+"",Toast.LENGTH_LONG).show();

            }
        });





    }

    // handle click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.log_out)
        {
            mAuth.signOut();
            CheckcurrentUser();

        }
        return super.onOptionsItemSelected(item);
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
