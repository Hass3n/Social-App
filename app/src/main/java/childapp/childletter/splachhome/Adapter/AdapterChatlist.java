package childapp.childletter.splachhome.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import childapp.childletter.splachhome.ChatActivity;
import childapp.childletter.splachhome.Models.Modeluser;
import childapp.childletter.splachhome.R;

public class AdapterChatlist  extends RecyclerView.Adapter<AdapterChatlist.MyHolder>{

    Context context;
    List<Modeluser>userList;  //get user info
    private HashMap<String,String>lastmessageMap;

    public AdapterChatlist(Context context, List<Modeluser> userList) {
        this.context = context;
        this.userList = userList;
        lastmessageMap = new HashMap<>();

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.rowchatlist,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String hisUid=userList.get(position).getUid();
        String userImage=userList.get(position).getImage();
        String userName=userList.get(position).getName();
        String lastMessage=lastmessageMap.get(hisUid);

        holder.nametv.setText(userName);

        String online=userList.get(position).getTypingTo().toString();
        Log.e("on",online+"");

        if(lastMessage==null||lastMessage.equals("default"))
        {

            holder.LastMessagetv.setVisibility(View.GONE);

        }


        else {

            holder.LastMessagetv.setVisibility(View.VISIBLE);
            holder.LastMessagetv.setText(lastMessage);


        }

        try {
             Picasso.get().load(userImage).placeholder(R.drawable.use).into(holder.profileIv);



        }
        catch (Exception e)
        {
            Picasso.get().load(R.drawable.use).into(holder.profileIv);

        }

        //  get online state for user in chatlist


     /*  if(userList.get(position).getOnlineStatus().equals("online"))
        {
            // online
            holder.onlinestateIv.setImageResource(R.drawable.circle_online);



        }
        else{
            //offline
            holder.onlinestateIv.setImageResource(R.drawable.circle_offline);

        }*/

        // handle click of user chatlist

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 // start chat Activity for user

                 Intent intent=new Intent(context, ChatActivity.class);
                 intent.putExtra("hasUid",hisUid);
                 context.startActivity(intent);



             }
         });






    }


    public void  setLastmessageMap(String userId,String lastMessage)
    {

     lastmessageMap.put(userId,lastMessage);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    class MyHolder  extends RecyclerView.ViewHolder {

        //view of row chatlist
        ImageView profileIv,onlinestateIv;
        TextView nametv,LastMessagetv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv=itemView.findViewById(R.id.profileIv);
            onlinestateIv=itemView.findViewById(R.id.onlinestateIv);
            nametv=itemView.findViewById(R.id.nametv);
            LastMessagetv=itemView.findViewById(R.id.LastMessagetv);


        }
    }
}
