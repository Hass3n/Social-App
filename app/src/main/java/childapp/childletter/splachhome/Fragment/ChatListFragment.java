package childapp.childletter.splachhome.Fragment;


import android.content.Intent;
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

import childapp.childletter.splachhome.Adapter.AdapterChatlist;
import childapp.childletter.splachhome.Adapter.AdapterUser;
import childapp.childletter.splachhome.Models.ModelChat;
import childapp.childletter.splachhome.Models.ModelChatlist;
import childapp.childletter.splachhome.Models.Modeluser;
import childapp.childletter.splachhome.R;
import childapp.childletter.splachhome.SplachActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;
    View view;
    RecyclerView recycleView;
    List<ModelChatlist>chatlistslist;
    List<Modeluser>userList;
    DatabaseReference reference;
    AdapterChatlist adapterChatlist;
    LinearLayoutManager linearLayoutManager;
    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_chat_list, container, false);
        recycleView=view.findViewById(R.id.recycleView);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        CurrentUser= mAuth.getCurrentUser();
         chatlistslist=new ArrayList<>();

         reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(CurrentUser.getUid());
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 chatlistslist.clear();
                 for (DataSnapshot ds: dataSnapshot.getChildren())
                 {
                     ModelChatlist chatlist=ds.getValue(ModelChatlist.class);
                     chatlistslist.add(chatlist);

                 }

                 LoadChat();



             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        return view;
    }

    private void LoadChat() {

        userList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {

                    Modeluser user=ds.getValue(Modeluser.class);
                    for(ModelChatlist chatlist:chatlistslist)
                    {

                        if (chatlist.getId()!=null&& user.getUid().equals(chatlist.getId()))
                        {

                            userList.add(user);
                            Log.e("online",userList.get(0).getOnlineStatus()+"");
                            break;


                        }

                    }

                    adapterChatlist=new AdapterChatlist(getContext(),userList);

                    linearLayoutManager=new LinearLayoutManager(getContext());
                    recycleView.setLayoutManager(linearLayoutManager);

                    recycleView.setAdapter(adapterChatlist);

                    //set last message
                    for(int i=0;i<userList.size();i++)
                    {

                       lastMessage(userList.get(i).getUid());


                    }






                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void lastMessage(final String useruid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String theLastMessage="default";
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {

                    ModelChat chat=ds.getValue(ModelChat.class);
                    if (chat==null)
                    {
                        continue;

                    }
                    String sender=chat.getSender();
                    String reciever=chat.getReceiver();
                    if (sender==null||reciever==null)
                    {
                        continue;
                    }

                    if (chat.getReceiver().equals(CurrentUser.getUid())&&chat.getSender().equals(useruid)||
                            chat.getReceiver().equals(useruid)&&chat.getSender().equals(CurrentUser.getUid()))
                    {
                        if (chat.getType().equals("image"))
                        {

                            theLastMessage ="send photo ............";

                        }
                        else
                        {

                          theLastMessage=chat.getMessage();


                        }


                    }


                }

                adapterChatlist.setLastmessageMap(useruid,theLastMessage);
                adapterChatlist.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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
            startActivity(new Intent(getActivity(), HomeFragment.class));

        }


    }


}
