# 🏗️ QTrace - Government Project Transparency Mobile Application

A comprehensive mobile platform empowering citizens to track Quezon City government projects in real time, inspect project details, and submit community reports directly from their mobile devices.

---

## 📋 About QTrace

QTrace bridges the gap between government agencies and citizens via an accessible mobile experience.

* 👥 **Public Mobile Experience:** Citizens can discover projects, inspect progress, track contractor details, and report site issues directly from their phones.
* 🔐 **Secure Management & Moderation:** Administrators manage project lifecycles, moderate citizen feedback, publish announcements, and review audit logs.
* 📊 **In-App Analytics:** Visual progress bars, metrics, and interactive charts right within the application interface.
* 🗺️ **Interactive Native Maps:** View nearby government projects using device location and interactive project pins.

---

## 🚀 How It Works

### 1️⃣ Administrative Control
Admins authenticate → Manage projects & contractors → Review citizen reports → Monitor full audit trails.

* Secure login with QC ID and credentials.
* Full lifecycle management for projects, news updates, and contractor profiles.
* Review, respond to, and resolve user-submitted site reports.
* Actionable system audit logs for absolute accountability.

### 2️⃣ Citizen Participation
Citizens open app → Explore project map → Track real-time status → Submit site feedback.

* Search and filter nearby or city-wide municipal projects.
* Access uploaded documents, image galleries, and progress timeline updates.
* Pinpoint project sites on native interactive maps.
* Report concerns or delays directly with photo attachments and geolocation.

### 3️⃣ Data Architecture
`MySQL Database ↔ API Controllers (Backend) ↔ Mobile App Screens (UI)`

* **Database:** Centralized MySQL database storing project data, audit logs, media metadata, and user reports.
* **API / Controller Layer:** Backend logic (`/database/controllers/`) serving JSON endpoints for application requests.
* **Mobile Views:** Dynamic application screens rendering structured project lists, maps, user accounts, and input forms.

---

## ✨ Key Features

### 🎯 Admin & Moderation Features

| Feature | Description |
| :--- | :--- |
| **Dashboard** | App metrics, project statuses, pending reports, and active counts at a glance. |
| **Project Management** | Create and edit project records, budgets, timelines, and geo-coordinates. |
| **Report Moderation** | Inspect incoming citizen complaints, attach status updates, and notify users. |
| **Contractor Directory** | Register certified contractors, assign projects, and update status credentials. |
| **News & Articles** | Draft and publish project announcements and public news feeds. |
| **Audit Trails** | Track data modifications, historical diff views, and user actions. |

### 🌍 Citizen Features

| Feature | Description |
| :--- | :--- |
| **Project Directory** | Search, filter, and paginate through active and completed city projects. |
| **Interactive Map** | View project sites anchored to map coordinates with status-based markers. |
| **Detailed Records** | Access image galleries, project documents, budgets, and contractor links. |
| **Submit Reports** | Log real-time site issues complete with image uploads and description logs. |
| **Contractor Profile** | View approved contractors, ratings, and active municipal contracts. |
| **Transparency Log** | Publicly accessible change audit trail tracking overall project updates. |

---

## 🗺️ App Navigation Architecture
