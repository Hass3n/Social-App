package childapp.childletter.splachhome.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
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

import childapp.childletter.splachhome.SplachActivity;
import childapp.childletter.splachhome.R;


public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private  FirebaseUser User;
   private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    //ArrayList<ModelPost>model;

    View view;
    public HomeFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycleView);

      //  model=new ArrayList<>();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        User= mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        linearLayoutManager=new LinearLayoutManager(getActivity());
        databaseReference= firebaseDatabase.getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                model.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren())
//                {
//                 ModelPost modelpost=ds.getValue(ModelPost.class);
//                  model.add(modelpost);
//
//                }

//                // add data to    postAdapter
//
//                postAdapter=new PostAdapter(model,getActivity());
//                recyclerView.setAdapter(postAdapter);
//                recyclerView.setLayoutManager(linearLayoutManager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(),databaseError.getMessage()+"",Toast.LENGTH_LONG).show();

            }
        });










        return view;
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
