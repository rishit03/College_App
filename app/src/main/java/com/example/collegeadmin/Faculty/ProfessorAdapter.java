package com.example.collegeadmin.Faculty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ProfessorViewAdapter> {
    private List<ProfessorData> list;
    private Context context;

    public ProfessorAdapter(List<ProfessorData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfessorViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.faculty_item_layout, parent, false);
        return new ProfessorViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorViewAdapter holder, int position) {
        ProfessorData item = list.get(position);
        holder.name.setText(item.getName());
        holder.post.setText(item.getPost());
        holder.email.setText(item.getEmail());
        try {
            Picasso.get().load(item.getImage()).into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Update Professor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ProfessorViewAdapter extends RecyclerView.ViewHolder {

        private TextView name, email, post;
        private Button update;
        private ImageView imageView;

        public ProfessorViewAdapter(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.professorName);
            post = itemView.findViewById(R.id.professorPost);
            email = itemView.findViewById(R.id.professorEmail);
            update = itemView.findViewById(R.id.professorUpdate);
            imageView = itemView.findViewById(R.id.professorImage);
        }
    }

    ;
}
