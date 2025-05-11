# NoorCity – Application Desktop JavaFX pour la Gestion de l'Éclairage Urbain (Smart City)

![GitHub License](https://img.shields.io/github/license/KarimOuertatani/PIDEV_3A7_symfony)
![GitHub Repo stars](https://img.shields.io/github/stars/KarimOuertatani/PIDEV_3A7_symfony?style=social)

## 🧭 Overview

**NoorCity** is a desktop application developed in **Java** with **JavaFX**, as part of the **PIDEV** module at **Esprit School of Engineering**.  
The application is connected to a **MySQL** database, shared with the web version (Symfony).

It allows for smart management of public lighting through interactive modules, energy analysis, and advanced features such as graphical visualization and automated alerts.

## 🚀 Features

### 👤 User Management
- Classic login via JavaFX with Auth0 for Google user management.
- Password reset via SMS using the Twilio API.
- Create/edit user profiles (Citizen, Technician, Manager) through FXML forms.
- Sorting, searching, deleting, and managing user statistics.
- Export data in PDF and Excel format (Apache POI for Excel).
- Dynamic home pages adapted to each user role, with enhanced security via JavaFX.

### 🛣️ Infrastructure Management
- **Streetlights**: Generate and read QR codes, predict failures, suggest locations (Python script to be integrated into Java).
- **Zones**: Geocoding via the OpenCage API to display geographic zones on a JavaFX map.
- **Cameras**: Add and view cameras, real-time video feed, traffic analysis.
- **Machine Learning**: Integration of an AI model to detect vehicles from data collected by streetlights and cameras, using a Java library like Weka or Deeplearning4j.

### 🧾 Complaints & Interventions Management
- Add, sort, edit, and delete complaints with intervention management via FXML forms.
- Dynamic links between complaints and interventions, with status tracking and history.
- Data visualization on a map in JavaFX, generate QR codes for each complaint.
- Export PDF of processed complaints, filtering offensive language via a word filter service in Java.

### 📊 Data & Sensor Management
- Sensor types: temperature, light, consumption, and motion (PIR) for each streetlight.
- SMS alerts sent via Twilio if the temperature exceeds 50°C.
- Dynamic charts for visualizing sensor data using JavaFX Charts.
- Interactive dashboard for real-time data analysis.
- Conditional data export in Excel format with sorting and filters.
- Interactive chatbot using a JSON response service to interact with users.

### ⚡ Energy Management
- **Energy Sources**: Manage sources (solar, electricity, battery) with CRUD operations, display consumption charts using JavaFX Charts.
- Integration with the weather API (OpenWeatherMap) to adjust energy profiles based on weather conditions.
- Export data in CSV format for analysis, generate PDF reports (via Apache PDFBox or iText).
- Manage energy profiles: sorting, multi-criteria comparison, send email notifications (MailerInterface in Java), export PDF of energy reports.



## 🛠️ Technology Stack

- **Java (JDK 17+)**: Core programming language for the backend.
- **JavaFX with FXML (Scene Builder)**: Used for building the desktop user interface (UI).
- **Maven**: Dependency management tool for handling libraries and project setup.
- **MySQL (via JDBC)**: Database management system used for storing and retrieving data.
- **Twilio API (SMS)**: Service for sending SMS alerts (e.g., password reset, temperature alerts).
- **OpenWeatherMap API**: Weather data API to fetch and integrate weather conditions.
- **iText PDF**: Library for generating and manipulating PDF files.
- **JavaMail**: Used for sending emails (e.g., energy reports, notifications).


## 🔌 APIs Used

- **Twilio** → SMS alerts when a new energy source is added.
- **OpenWeatherMap** → Real-time weather data.
- **JavaMail** → Sending email notifications.
- **iText** → Automatic PDF export of reports.

### 🔧 Hardware Integration (IoT)
- **ESP32-WROVER board with camera**: used for real-time visual detection
- **Sensors**: temperature, motion (PIR), brightness (LDR)
- **Arduino Uno board with breadboard**: used for prototyping and initial data collection
- Sensor communication via ESP32 to the Symfony application (Wi-Fi MQTT or HTTP REST)



## 📁 Project Structure


```bash
PIDEV_3A7_Unity-Force-main/
├── src/
│   └── main/
│       ├── java/
│       │   └── tn/
│       │       └── esprit/
│       │           ├── controllers/
│       │           ├── Enumerations/
│       │           ├── interfaces/
│       │           ├── models/
│       │           ├── services/
│       │           ├── test/
│       │           └── utils/
│       └── resources/
├── .idea/
├── pom.xml
├── target/
└── .gitignore
```

## ⚙️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/KarimOuertatani/PIDEV_3A7_symfony
```

###  1.5 Start XAMPP

- Open XAMPP Control Panel
- **Start Apache and MySQL**
- Make sure the MySQL server is running on `localhost`

### 2. Import the project into IntelliJ IDEA

- Choose **"Open as Maven Project"**
- Wait for the dependencies to sync

### 3. Configure the database

- Open `MyDatabase.java`
- Modify the values: `url`, `username`, `password` according to your local configuration

### 4. Run the Application

- Execute the `MainApp.java` class
- The `.fxml` interfaces can be modified with **Scene Builder**

## 🏷️ Themes
`smart-city` `java` `javafx` `iot` `energy-management` `twilio` `openweathermap` `pdf-export` `scene-builder` `maven`

## 📽️ Demo
*(optional)* Add a YouTube link or a demo video here if available.

## 👨‍💻 Authors
- **Mohamed Youssef Mellouli**, **Mohamed Karim Ouertatani**, **Mohamed Amine Charrada**, **Mohamed Rayen Sansa**, **Aziz Ben Ammar** – Lead Developers  
- Project developed as part of **PIDEV** at **Esprit School of Engineering**

## 📦 Acknowledgements

This project was designed as part of the **PIDEV** module at **Esprit School of Engineering**.

We thank:

- Our teachers and mentors for their support:
  - **Yassine Dhaya** and **Mohamed Hosni** – Java/JavaFX Instructors  
  - The network instructor


- Les contributeurs du projet :
  - [**Mohamed Youssef Mellouli**](https://github.com/Youssef222003)  
  - [**Mohamed Karim Ouertatani**](https://github.com/KarimOuertatani)  
  - [**Mohamed Amine Charrada**](https://github.com/charrada-amine)  
  - [**Mohamed Rayen Sansa**](https://github.com/RayenSansa03)  
  - [**Aziz Ben Ammar**](https://github.com/azizbenammar7)

- Third-party services used: **Twilio**, **Auth0**, **OpenCage**, **OpenWeatherMap**, **MAILJET**

## 📝 License
This project is licensed under the MIT License – see the [LICENSE.md](./LICENSE.md) file for more information.