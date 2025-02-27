package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase2 {
    private static MyDatabase2 instance;
    private final String URL ="jdbc:mysql://127.0.0.1:3306/noorcity";
    private final String USERNAME ="root";
    private final String PASSWORD = "";
    private Connection  cnx ;

    public MyDatabase2() {
        try {
            cnx = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("connected ...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static MyDatabase2 getInstance() {
        if (instance == null)
            instance = new MyDatabase2();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }


}
