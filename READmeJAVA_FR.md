# NoorCity – Application Desktop JavaFX pour la Gestion de l'Éclairage Urbain (Smart City)

![GitHub License](https://img.shields.io/github/license/KarimOuertatani/PIDEV_3A7_symfony)
![GitHub Repo stars](https://img.shields.io/github/stars/KarimOuertatani/PIDEV_3A7_symfony?style=social)

## 🧭 Vue d’ensemble

**NoorCity** est une application desktop développée en **Java** avec **JavaFX**, dans le cadre du module **PIDEV** à **Esprit School of Engineering**.  
L’application est connectée à une base de données **MySQL**, partagée avec la version web (Symfony).

Elle permet de gérer intelligemment l’éclairage public à travers des modules interactifs, des analyses énergétiques et des fonctionnalités avancées comme la visualisation graphique et les alertes automatiques.
## 🚀 Fonctionnalités

### 👤 Gestion des Utilisateurs
- Connexion classique via JavaFX avec Auth0 pour la gestion des utilisateurs Google.
- Réinitialisation du mot de passe par SMS via l'API Twilio.
- Création/modification de profils utilisateur (Citoyen, Technicien, Responsable) via formulaire FXML.
- Tri, recherche, suppression et gestion des statistiques des utilisateurs.
- Export des données au format PDF et Excel (Apache POI pour Excel).
- Pages d’accueil dynamiques adaptées à chaque rôle d'utilisateur, avec sécurité renforcée via JavaFX.

### 🛣️ Gestion des Infrastructures
- **Lampadaires** : Génération et lecture de QR codes, prédiction de pannes, suggestion de localisation (script Python à intégrer dans Java).
- **Zones** : Géocodage via l'API OpenCage pour afficher des zones géographiques sur une carte JavaFX.
- **Caméras** : Ajout et visualisation des caméras, flux vidéo en temps réel, analyse de trafic.
- **Machine Learning** : Intégration d'un modèle d'intelligence artificielle (IA) pour la détection de véhicules à partir des données collectées par les lampadaires et les caméras, en utilisant une bibliothèque Java comme Weka ou Deeplearning4j.

### 🧾 Gestion des Réclamations & Interventions
- Ajout, tri, modification et suppression des réclamations avec gestion des interventions via formulaire FXML.
- Liens dynamiques entre réclamations et interventions, avec suivi de statut et historique.
- Visualisation des données sur une carte en JavaFX, génération de QR codes pour chaque réclamation.
- Export PDF des réclamations traitées, filtrage des propos via un service de filtrage de mots offensants en Java.

### 📊 Gestion des Données & Capteurs
- Types de capteurs : température, luminosité, consommation et mouvement (PIR) pour chaque lampadaire.
- Alertes SMS envoyées via Twilio si la température dépasse 50°C.
- Graphiques dynamiques pour la visualisation des données des capteurs avec JavaFX Charts.
- Tableau de bord interactif pour analyser les données en temps réel.
- Export conditionnel des données au format Excel, avec tri et filtres.
- Chatbot interactif utilisant un service de réponse JSON pour interagir avec les utilisateurs.
  
### ⚡ Gestion de l’Énergie
- **Sources énergétiques** : Gestion des sources (solaire, électricité, batterie) avec opérations CRUD, affichage de graphiques sur la consommation d’énergie via JavaFX Charts.
- Intégration avec l'API météo (OpenWeatherMap) pour ajuster les profils énergétiques en fonction des conditions climatiques.
- Export des données sous format CSV pour analyse, génération de rapports PDF (via Apache PDFBox ou iText).
- Gestion des profils énergétiques : tri, comparaison multi-critères, envoi de notifications par email (MailerInterface en Java), export PDF des rapports énergétiques.


## 🛠️ Pile technologique

- Java (JDK 17+)
- JavaFX avec FXML (Scene Builder)
- Maven (gestion de dépendances)
- MySQL (via JDBC)
- Twilio API (SMS)
- OpenWeatherMap API (météo)
- iText PDF (génération de fichiers PDF)
- JavaMail (envoi d’e-mails)

## 🔌 APIs utilisées

- Twilio → Alerte SMS à l’ajout d’une source
- OpenWeatherMap → Données météo temps réel
- JavaMail → Envoi de notifications mail
- iText → Export PDF automatique des rapports


### 🔧 Intégration Matérielle (IoT)
- **Carte ESP32-WROVER avec caméra** : utilisée pour la détection visuelle en temps réel
- **Capteurs** : température, mouvement (PIR), luminosité (LDR)
- **Carte Arduino Uno avec plaque d’essai blanche** : utilisée pour le prototypage et la collecte initiale de données
- Communication des capteurs via ESP32 vers l’application Symfony (Wi-Fi MQTT ou HTTP REST)

## 📁 Structure du Projet

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

## ⚙️ Démarrage rapide

### 1. Cloner le dépôt

```bash
git clone https://github.com/KarimOuertatani/PIDEV_3A7_symfony
```

### 1.5. Démarrer XAMPP

- Ouvrir XAMPP Control Panel
- **Démarrer Apache et MySQL**
- S'assurer que le serveur MySQL est bien en ligne sur `localhost`

### 2. Importer le projet dans IntelliJ IDEA

- Choisir **"Open as Maven Project"**
- Attendre la synchronisation des dépendances

### 3. Configurer la base de données

- Ouvrir `MyDatabase.java`
- Modifier les valeurs : `url`, `username`, `password` selon votre configuration locale

### 4. Lancer l’application

- Exécuter la classe `MainApp.java`
- Les interfaces `.fxml` peuvent être modifiées avec **Scene Builder**

## 🏷️ Thèmes
`smart-city` `java` `javafx` `iot` `energy-management` `twilio` `openweathermap` `pdf-export` `scene-builder` `maven`

## 📽️ Demo
*(optionnel)* Ajouter un lien YouTube ou une vidéo de démonstration ici si disponible.

## 👨‍💻 Auteurs
- **Mohamed Youssef Mellouli**, **Mohamed Karim Ouertatani**, **Mohamed Amine Charrada**, **Mohamed Rayen Sansa**, **Aziz Ben Ammar** – Développeurs principaux  
- Projet réalisé dans le cadre de **PIDEV** à **Esprit School of Engineering**

## 📦 Remerciements

Ce projet a été conçu dans le cadre du module **PIDEV** à **Esprit School of Engineering**.

Nous remercions :

- Nos enseignants et encadrants pour leur accompagnement :
  - **Yassine Dhaya** et **Mohamed Hosni** – Enseignants Java/JavaFX  
  - L’enseignant de réseaux

- Les contributeurs du projet :
  - [**Mohamed Youssef Mellouli**](https://github.com/Youssef222003)  
  - [**Mohamed Karim Ouertatani**](https://github.com/KarimOuertatani)  
  - [**Mohamed Amine Charrada**](https://github.com/charrada-amine)  
  - [**Mohamed Rayen Sansa**](https://github.com/RayenSansa03)  
  - [**Aziz Ben Ammar**](https://github.com/azizbenammar7)

- Les services tiers utilisés : **Twilio**, **OpenWeatherMap**, **JavaMail**, **iText**


## 📝 Licence
Ce projet est sous licence MIT – voir le fichier [LICENSE.md](./LICENSE.md) pour plus d'informations.
