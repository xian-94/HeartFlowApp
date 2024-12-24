package com.example.heartflowapp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartflowapp.R;
import com.example.heartflowapp.model.Donor;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder> {

    private final List<Donor> donorList;

    public DonorAdapter(List<Donor> donorList) {
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person, parent, false);
        return new DonorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        Donor donor = donorList.get(position);
        holder.name.setText(donor.getFullName());
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;

        public DonorViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }
}
