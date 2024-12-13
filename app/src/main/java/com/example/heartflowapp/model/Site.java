package com.example.heartflowapp.model;

import java.util.Date;
import java.util.List;

public class Site {
    private String siteId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String createdBy;
    private List<String> managerIds;
    private int totalDonors;
    private Date createdAt;
}
