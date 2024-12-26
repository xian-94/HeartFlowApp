Student ID: s3957034
Student Name: Truong Hong Van

App Functionalities:
-------------------
The application supports three user roles and general features as follows:

1. Blood Donation Site Manager
   - Set up new donation sites.
   - Input data after donation drives, including total blood collected.
   - Register as a volunteer for donation sites.
   - View the list of donors and volunteers for the site. 
   - View statistics donation sites. 

2. Donors:
   - View active and registered donation sites on a map.
   - Click on sites to view detailed information such as address, donation hours, and required blood types.
   - Register themselves to donate at a site. 
   - Volunteer for a site. 

3. Super User:
   - Generate reports on donation drives (number of donors, blood volume, blood types collected) as PDF files.
   - View all donation sites across the application, including site details. 
   - View statistics of all users in the app. 

4. General Functionalities:
   - Map View:
     - Display nearby donation sites with customized markers.
     - Show detailed information (address, donation hours, required blood types) when clicking on a site.
   - Filter/Search:
     -  Filter donation sites based on specific criteria such as blood type and date of the event.
     -  Search donation site address. 
   - Authentication:
     - User signup/login/

Technology Used:
----------------
1. Google Maps API:
   - Used to implement the map functionality, including displaying donation sites and providing location-based interaction.

2. Firebase:
   - Firebase Authentication: Ensures secure signup and login for users.
   - Firebase Firestore: A NoSQL database used to store and manage site details, user information, and event data in real-time.

3. PdfDocument API:
   - Generates downloadable PDF reports with details of completed donation drives, including blood type statistics and participant lists.

4. Material Design Components:
   - Used to create a modern and intuitive user interface, including buttons, cards, and navigation bars.

5. RecyclerView:
   - Used for displaying lists of donors and volunteers efficiently, ensuring smooth scrolling and dynamic updates.

Drawbacks:
----------
1. Unfinished Features:
   - Managers cannot yet download a list of registered donors for their sites.
   - Users cannot find routes to donation sites from their current location.
   - Notifications for updates to donation sites are not yet implemented.
   
2. Performance:
   - Loading a large number of sites on the map or in lists may affect performance on low-end devices.

3. Offline Functionality:
   - The app heavily relies on Firebase for real-time data, making it less effective in offline mode.



