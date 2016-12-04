package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Gheorghe on 12/4/2016.
 */
public class DataBaseConnector {

    final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orcl1";

    // Database credentials
    final String USER = "c##gcr";
    final String PASS = "1234";

    private static DataBaseConnector instance = null;
    private DataBaseConnector() {
        initialize();
    }

    private Connection connection;

    private void initialize() {
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DataBaseConnector getInstance() {
        if (instance == null) {
            instance = new DataBaseConnector();
        }

        return instance;
    }
}
