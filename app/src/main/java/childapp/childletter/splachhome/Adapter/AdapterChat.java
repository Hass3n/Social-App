package childapp.childletter.splachhome.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;

import childapp.childletter.splachhome.Models.ModelChat;
import childapp.childletter.splachhome.R;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat>chatList;
    String image_url;
    FirebaseUser fbuser;

    public AdapterChat(Context context, List<ModelChat> chatList, String image_url) {
        this.context = context;
        this.chatList = chatList;
        this.image_url = image_url;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate layout left or right

        if (viewType==MSG_TYPE_RIGHT)
        {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_right,parent,false);
            MyHolder viewHolder = new MyHolder(v);
            return viewHolder;


        }

      else
        {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_left,parent,false);
            MyHolder viewHolder = new MyHolder(v);
            return viewHolder;


        }





    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        //get data
        String message=chatList.get(position).getMessage();
         String timetemp=chatList.get(position).getTimestemp();
        String type=chatList.get(position).getType();

         // convert time temp to dd/mm/yyyy hh:mm am/pm

        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timetemp));
        String datatime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

        if (type.equals("text"))
        {
            holder.messageIv.setVisibility(View.VISIBLE);
            holder.imageIv.setVisibility(View.GONE);
            holder.messageIv.setText(message);

        }
        else {

            holder.messageIv.setVisibility(View.GONE);
            holder.imageIv.setVisibility(View.VISIBLE);
            Picasso.get().load(message).placeholder(R.drawable.ic_image).into(holder.imageIv);


        }


        //set data

        holder.timeIv.setText(datatime);


        // set image sender or reciever
        try {

            Picasso.get().load(image_url).into(holder.profileIv);

        }
        catch (Exception e)
        {

            Picasso.get().load(R.drawable.use).into(holder.profileIv);

        }

        //set seen or deliver message state

        if (position==chatList.size()-1)
        {
            if (chatList.get(position).isSeen())
            {
                holder.isSeenIv.setText("seen");

            }
            else {

                holder.isSeenIv.setText("Delivered");
            }

        }
        else {

            holder.isSeenIv.setVisibility(View.GONE);
        }



        // handle click layout to delete message

        holder.messagelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("are you want to delete message");

                //set layout Manger

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                   MessageDelete(position);




                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();


                    }
                });

                builder.create().show();





            }
        });











    }

    private void MessageDelete(int position) {
        //get current sign in user
        fbuser= FirebaseAuth.getInstance().getCurrentUser();

        String msgTimestemp=chatList.get(position).getTimestemp();
        final DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("Chats");
        final Query query=dbRef.orderByChild("timestemp").equalTo(msgTimestemp);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds :dataSnapshot.getChildren())
                {
                    // check sender to delete message only send

                    if (ds.child("sender").getValue().equals(fbuser.getUid())) {
                        // delete
                       // ds.getRef().removeValue();

                        //or update
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "message was delete ...");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "message deleted", Toast.LENGTH_LONG).show();
                    }

                    else {

                        Toast.makeText(context, "you can delete only message ", Toast.LENGTH_LONG).show();
                    }


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        //get current sign in user
        fbuser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fbuser.getUid()))
        {
            return MSG_TYPE_RIGHT;

        }
        else {
            return MSG_TYPE_LEFT;
        }

    }

    class MyHolder extends RecyclerView.ViewHolder
    {
         ImageView profileIv,imageIv;
         TextView messageIv,timeIv,isSeenIv;
         LinearLayout messagelayout;

        public MyHolder(View itemView)
        {
            super(itemView);
            profileIv=itemView.findViewById(R.id.profileIv);
            messageIv=itemView.findViewById(R.id.messageIv);
            imageIv=itemView.findViewById(R.id.messagev);
            timeIv=itemView.findViewById(R.id.timeIv);
            isSeenIv=itemView.findViewById(R.id.IsSeenTv);
            messagelayout=itemView.findViewById(R.id.messagelayout);


        }



    }
}
