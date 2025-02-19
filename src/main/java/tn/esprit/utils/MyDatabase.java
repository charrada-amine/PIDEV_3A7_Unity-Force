package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static MyDatabase instance;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> origin/aminecharrada/GestionEnergie
    private Connection cnx;

    private final String URL = "jdbc:mysql://localhost:3306/projetpi_3a";
    private final String USER = "root";
    private final String PASSWORD = "";

    private MyDatabase() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion établie !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
<<<<<<< HEAD
=======
    private final String URL ="jdbc:mysql://127.0.0.1:3306/noorcity";
=======
    private final String URL ="jdbc:mysql://127.0.0.1:3307/pidev";
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
=======
    private final String URL ="jdbc:mysql://127.0.0.1:3306/esprit";
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
    private final String USERNAME ="root";
    private final String PASSWORD = "";
    private Connection  cnx ;

<<<<<<< HEAD
<<<<<<< HEAD
    public MyDatabase() {
=======
    private MyDatabase() {
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
=======
    private MyDatabase() {
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
        try {
            cnx = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("connected ...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static MyDatabase getInstance() {
        if (instance == null)
            instance = new MyDatabase();
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> a3c932b0e96e26782012e7d55e0b3688bdf4452d
=======
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
=======
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
=======
>>>>>>> origin/aminecharrada/GestionEnergie
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> origin/aminecharrada/GestionEnergie

    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("✅ Connexion fermée.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}

<<<<<<< HEAD
=======
}
>>>>>>> a3c932b0e96e26782012e7d55e0b3688bdf4452d
=======
}
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
=======
}
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
=======
>>>>>>> origin/aminecharrada/GestionEnergie
