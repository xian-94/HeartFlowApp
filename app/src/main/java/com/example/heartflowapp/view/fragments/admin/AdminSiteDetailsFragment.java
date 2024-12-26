package com.example.heartflowapp.view.fragments.admin;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.adapters.SiteDetailsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminSiteDetailsFragment extends Fragment {

    private static final String ARG_SITE = "SITE";
    private static final String ARG_USER = "USER";
    private Site site;
    private String user;

    public static AdminSiteDetailsFragment newInstance(Site site, String user) {
        AdminSiteDetailsFragment fragment = new AdminSiteDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SITE, site);
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_site_details, container, false);

        TextView siteName = view.findViewById(R.id.site_name);
        TextView siteAddress = view.findViewById(R.id.site_address);
        TextView donationDate = view.findViewById(R.id.site_date);
        ImageButton closeBtn = view.findViewById(R.id.close_button);
        LinearLayout bloodTypesContainer = view.findViewById(R.id.blood_types_container);
        TextView siteManager = view.findViewById(R.id.site_manager);
        TabLayout tabLayout = view.findViewById(R.id.site_details_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.site_details_view_pager);
        TextView siteStatus = view.findViewById(R.id.site_status);
        Button download = view.findViewById(R.id.generate_reports);

        if (getArguments() != null) {
            site = (Site) getArguments().getSerializable(ARG_SITE);
            user = getArguments().getString(ARG_USER);
            siteName.setText(site.getName());
            siteAddress.setText(site.getAddress());
            donationDate.setText(site.getDate());
            siteStatus.setText(site.getStatus());
            getUser(siteManager);
            populateBloodTypes(bloodTypesContainer, site.getRequiredBloodTypes());

        }
        SiteDetailsPagerAdapter adapter = new SiteDetailsPagerAdapter(this, site);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Donors");
            else tab.setText("Volunteers");
        }).attach();


        closeBtn.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        download.setOnClickListener(v -> loadReportDetails());

        return view;
    }


    private void populateBloodTypes(LinearLayout container, Map<String, Double> requiredBloodTypes) {
        for (String bloodType : requiredBloodTypes.keySet()) {
            TextView bloodTypeBox = new TextView(getContext());

            // Set text and style for each blood type
            bloodTypeBox.setText(bloodType);
            bloodTypeBox.setPadding(16, 8, 16, 8);
            bloodTypeBox.setTextSize(14);
            bloodTypeBox.setTextColor(Color.parseColor("#FCAEAE"));
            bloodTypeBox.setGravity(Gravity.CENTER);

            // Set rounded background
            bloodTypeBox.setBackgroundResource(R.drawable.box_normal);

            // Add margin between boxes
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            bloodTypeBox.setLayoutParams(params);

            // Add blood type box to container
            container.addView(bloodTypeBox);
        }
    }

    private void getUser(TextView siteManager) {
        DatabaseManager db = new DatabaseManager();
        db.getRef("user").document(user).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        SiteManager fetchedUser = task.getResult().toObject(SiteManager.class);
                        if (fetchedUser != null) {
                            siteManager.setText(fetchedUser.getFullName());
                        }
                    }
                })
                .addOnFailureListener(e ->
                        siteManager.setText("Error loading manager"));
    }

    // Handle pre-processing for report writing
    private void loadReportDetails() {
        DatabaseManager db = new DatabaseManager();
        List<String> donorIds = site.getDonors();
        List<String> volunteerIds = site.getVolunteers();
        List<String> donorDetails = new ArrayList<>();
        List<String> volunteerDetails = new ArrayList<>();

        // Fetch donor details
        for (String donorId : donorIds) {
            db.getRef("donor").document(donorId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            String phone = documentSnapshot.getString("phone");
                            String blood = documentSnapshot.getString("type");
                            donorDetails.add(name + " (" + phone + ")" + " - Type: " + blood);
                        }

                        // Check if all donors and volunteers are fetched
                        if (donorDetails.size() == donorIds.size() && volunteerDetails.size() == volunteerIds.size()) {
                            createPDF(site, donorDetails, volunteerDetails);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to fetch donor details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        // Fetch volunteer details
        for (String volunteerId : volunteerIds) {
            db.getRef("user").document(volunteerId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            String phone = documentSnapshot.getString("phone");
                            volunteerDetails.add(name + " (" + phone + ")");
                            Toast.makeText(requireContext(), "Fetch volunteer", Toast.LENGTH_SHORT).show();
                        }

                        // Check if all donors and volunteers are fetched
                        if (donorDetails.size() == donorIds.size() && volunteerDetails.size() == volunteerIds.size()) {
                            createPDF(site, donorDetails, volunteerDetails);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to fetch volunteer details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Write data to file and create pdf
    private void createPDF(Site site, List<String> donorDetails, List<String> volunteerDetails) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Title
        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        canvas.drawText(site.getName(), 50, 50, paint);
        Toast.makeText(requireContext(), "title", Toast.LENGTH_SHORT).show();

        // Content
        paint.setTextSize(14);
        paint.setFakeBoldText(false);
        int y = 100;
        canvas.drawText("Address: " + site.getAddress(), 50, y, paint);
        y += 30;
        canvas.drawText("Date: " + site.getDate(), 50, y, paint);
        y += 30;
        canvas.drawText("Status: " + site.getStatus(), 50, y, paint);
        y += 30;

        canvas.drawText("Required Blood Types:", 50, y, paint);
        y += 30;
        for (Map.Entry<String, Double> entry : site.getRequiredBloodTypes().entrySet()) {
            canvas.drawText(" - " + entry.getKey() + ": " + entry.getValue() + " ml", 50, y, paint);
            y += 20;
        }

        // Donor List
        canvas.drawText("Total donors: " + donorDetails.size(), 50, y, paint);
        y += 30;
        canvas.drawText("Donors:", 50, y, paint);
        y += 20;
        for (String donor : donorDetails) {
            canvas.drawText(" - " + donor, 50, y, paint);
            y += 20;
        }

        // Volunteer List
        canvas.drawText("Total volunteers: " + volunteerDetails.size(), 50, y, paint);
        y += 30;
        canvas.drawText("Volunteers:", 50, y, paint);
        y += 20;
        for (String volunteer : volunteerDetails) {
            canvas.drawText(" - " + volunteer, 50, y, paint);
            y += 20;
        }

        // Finish page
        pdfDocument.finishPage(page);

        // Save the PDF file
        savePDF(pdfDocument, "Site_Report_" + site.getName() + ".pdf");
    }

    private void savePDF(PdfDocument pdfDocument, String fileName) {
        // Save to the public Documents directory
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            pdfDocument.writeTo(out);
            Toast.makeText(requireContext(), "PDF saved to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            // Notify the system about the new file
            MediaScannerConnection.scanFile(
                    requireContext(),
                    new String[]{file.getAbsolutePath()},
                    null,
                    (path, uri) -> Log.d("FileScan", "File scanned: " + path)
            );
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }



}
