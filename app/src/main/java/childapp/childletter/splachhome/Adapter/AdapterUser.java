package childapp.childletter.splachhome.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import childapp.childletter.splachhome.ChatActivity;
import childapp.childletter.splachhome.R;
import childapp.childletter.splachhome.Models.Modeluser;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {
        List<Modeluser> data_items;
        Context context;
        OnItemclick onItemclick;

        public void setOnItemclick(OnItemclick onItemclick) {
            this.onItemclick = onItemclick;
        }

        public AdapterUser(List<Modeluser> data_items, Context context) {
            this.data_items= data_items;

            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rowuser,viewGroup,false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

            final Modeluser item= data_items.get(i);
            viewHolder.txt1.setText(item.getName());
            viewHolder.txt2.setText(item.getEmail());
            final String id=item.getUid().toString();
             String image=item.getImage().toString();
            try {

                Picasso.get().load(image).into(viewHolder.imageView);

            }
            catch ( Exception e)
            {
                Picasso.get().load(R.drawable.add_a_photo).into(viewHolder.imageView);


            }

            viewHolder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(context, ChatActivity.class);
                    intent.putExtra("hasUid",id);
                    context.startActivity(intent);



                }
            });


        }

        @Override
        public int getItemCount() {
            return data_items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt1,txt2;
            ImageView imageView;
            ImageView imag;
            View parent;

            ViewHolder(View view){
                super(view);
                txt1= view.findViewById(R.id.nametv);
                txt2= view.findViewById(R.id.emailtv);
                imageView=view.findViewById(R.id.imagetv);
                parent=view;

            }




        }


        public  interface  OnItemclick
        {
            public void onitemclick(Modeluser datacrafstman,int postion);



        }

    }






