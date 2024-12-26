package com.example.heartflowapp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartflowapp.R;
import com.example.heartflowapp.model.Site;

import java.util.List;

public class DonorSiteAdapter extends RecyclerView.Adapter<DonorSiteAdapter.DonorSiteViewHolder> {

    private final List<Site> drives;

    public DonorSiteAdapter(List<Site> drives) {
        this.drives = drives;
    }

    @NonNull
    @Override
    public DonorSiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new DonorSiteViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DonorSiteViewHolder holder, int position) {
        Site drive = drives.get(position);
        holder.eventName.setText(drive.getName());
        holder.eventDate.setText(drive.getDate());
        holder.eventStatus.setText(drive.getStatus());
    }

    @Override
    public int getItemCount() {
        return drives.size();
    }

    static class DonorSiteViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventStatus;

        public DonorSiteViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventStatus = itemView.findViewById(R.id.event_status);
        }
    }
}

