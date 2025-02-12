package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    final String USERNAME ="root";
    final String PASSWORD ="";
    final String URL ="jdbc:mysql://127.0.0.1:3306/projetpi_3a";

    private Connection cnx ;

    public MyDatabase(){
        try {
            cnx = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getCnx() {
        return cnx;
    }
}
