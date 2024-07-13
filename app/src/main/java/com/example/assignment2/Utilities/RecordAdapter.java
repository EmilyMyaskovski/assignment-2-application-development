package com.example.assignment2.Utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment2.R;
import com.example.assignment2.Interfeces.Callback_ScoresItemClicked;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<ScoreData> records;
    private Callback_ScoresItemClicked callback;
    private Context context;

    public RecordAdapter(List<ScoreData> records, Callback_ScoresItemClicked callback, Context context) {
        this.records = records != null ? records : new ArrayList<>();
        this.callback = callback;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        ScoreData record = records.get(position);
        holder.nameTextView.setText(record.getName());
        holder.scoreTextView.setText(String.valueOf(record.getScore()));
        holder.locationTextView.setText(getAddressFromLatLng(record.getLatitude(), record.getLongitude()));

        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.scoresItemClicked(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressString = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressString.append(address.getAddressLine(i)).append("\n");
                }

                return addressString.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown location";
    }

    public void addRecord(ScoreData newRecord) {
        records.add(newRecord);
        notifyItemInserted(records.size() - 1);
    }

    public void updateRecords(List<ScoreData> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView scoreTextView;
        TextView locationTextView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            scoreTextView = itemView.findViewById(R.id.text_score);
            locationTextView = itemView.findViewById(R.id.text_location);
        }
    }
}
