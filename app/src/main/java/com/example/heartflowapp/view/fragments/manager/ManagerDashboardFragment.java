package com.example.heartflowapp.view.fragments.manager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.model.Site;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ManagerDashboardFragment extends Fragment {

    private static final String ARG_USER = "USER";

    private String user;

    public ManagerDashboardFragment() {
        // Required empty public constructor
    }

    public static ManagerDashboardFragment newInstance(String user) {
        ManagerDashboardFragment fragment = new ManagerDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site, container, false);
        getTotal((totalEvents, totalDonorsResult) -> {
            MaterialTextView totalDonors = view.findViewById(R.id.total_donors);
            MaterialTextView totalDrives = view.findViewById(R.id.total_events);
            totalDonors.setText(String.valueOf(totalDonorsResult));
            totalDrives.setText(String.valueOf(totalEvents));
        });

        setupLineChart(view);

        return view;
    }

    // Interface to handle async total fetching
    private interface TotalDataCallback {
        void onDataFetched(int totalEvents, int totalDonors);
    }


    private void getTotal(TotalDataCallback callback) {
        DatabaseManager db = new DatabaseManager();
        db.getRef("site")
                .whereEqualTo("createdBy", user)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalEvents = task.getResult().size();
                        int totalDonors = 0;
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                            List<?> donors = (List<?>) task.getResult().getDocuments().get(i).get("donors");
                            if (donors != null) {
                                totalDonors += donors.size();
                            }
                        }
                        // Pass the results to the callback
                        callback.onDataFetched(totalEvents, totalDonors);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void setupLineChart(View view) {
        LineChart lineChart = view.findViewById(R.id.blood_trend_chart);
        DatabaseManager db = new DatabaseManager();
        // Get site data sorted by date, only completed ones
        db.getRef("site")
                .whereEqualTo("createdBy", user)
                .whereEqualTo("status", "COMPLETE")
                .orderBy("date", Query.Direction.ASCENDING).
                get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Entry> lineEntries = new ArrayList<>();
                        List<String> labels = new ArrayList<>();
                        List<Site> sites = new ArrayList<>();
                        int index = 0;

                        for (DocumentSnapshot document : task.getResult()) {
                            String date = document.getString("date");
                            Site site = document.toObject(Site.class);
                            Map<String, Double> bloodTypes = (Map<String, Double>) document.get("requiredBloodTypes");
                            String siteId = document.getId();
                            if (bloodTypes != null) {
                                double totalBloodCollected = 0;
                                for (double value : bloodTypes.values()) {
                                    totalBloodCollected += value;
                                }
                                lineEntries.add(new Entry(index, (float) totalBloodCollected));
                                labels.add(date);
                                sites.add(site);
                                index++;
                            }
                        }

                        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Total Blood Collected");
                        lineDataSet.setColor(getResources().getColor(R.color.primary_red));
                        lineDataSet.setValueTextSize(12f);
                        lineDataSet.setLineWidth(2f);
                        lineDataSet.setCircleRadius(4f);
                        lineDataSet.setCircleColor(getResources().getColor(R.color.primary_red));

                        LineData lineData = new LineData(Collections.singletonList(lineDataSet));
                        lineChart.setData(lineData);

                        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                        lineChart.getXAxis().setLabelCount(labels.size());
                        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        lineChart.getXAxis().setDrawGridLines(false);
                        lineChart.getAxisRight().setEnabled(false);
                        lineChart.getDescription().setEnabled(false);
                        lineChart.setExtraBottomOffset(10f);
                        lineChart.invalidate();

                        // Handle click events on the chart
                        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry e, Highlight h) {
                                int dataIndex = (int) e.getX(); // Get the index of the selected entry
                                Site selectedSite = sites.get(dataIndex); // Get the corresponding site ID

                                // Open the SiteDetailsFragment with the selected site ID
                                SiteDetailsFragment fragment = SiteDetailsFragment.newInstance(selectedSite, user);
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.manager_container, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }

                            @Override
                            public void onNothingSelected() {
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch data" + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }


}