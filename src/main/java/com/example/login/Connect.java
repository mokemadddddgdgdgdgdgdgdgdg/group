package com.example.login;

import java.sql.Connection;
import java.sql.DriverManager;

import static java.lang.Class.forName;

public class Connect {

    public static Connection connectDb() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3307/limkokwing", "root", "khopolo");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

}