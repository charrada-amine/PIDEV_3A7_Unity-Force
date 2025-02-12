package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static MyDatabase instance;
    private Connection cnx;

    private final String URL = "jdbc:mysql://localhost:3306/pi3a7";
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
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }

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

