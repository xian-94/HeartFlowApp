package com.example.heartflowapp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartflowapp.R;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.model.User;

import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder> {

    private final List<User> volunteerList;

    public VolunteerAdapter(List<User> volunteerList) {
        this.volunteerList = volunteerList;
    }

    @NonNull
    @Override
    public VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person, parent, false);
        return new VolunteerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerViewHolder holder, int position) {
        User volunteer = volunteerList.get(position);
        holder.name.setText(volunteer.getFullName());
        holder.phone.setText(volunteer.getPhone());
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    public static class VolunteerViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
        }
    }
}

