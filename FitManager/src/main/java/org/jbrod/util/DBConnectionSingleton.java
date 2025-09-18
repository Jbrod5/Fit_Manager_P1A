package org.jbrod.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionSingleton {

    private static final String HOST = "localhost";
    private static final int PUERTO = 5432;
    private static final String DB = "fitmanager";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "";

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + DB;

    private static DBConnectionSingleton instance;
    private Connection connection;

    private DBConnectionSingleton() {
        abrirConexion();
    }

    private void abrirConexion() {
        try {
            connection = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión a PostgreSQL exitosa");
        } catch (SQLException e) {
            System.out.println("Error al conectarse a PostgreSQL");
            e.printStackTrace();
        }
    }

    public static DBConnectionSingleton getInstance() {
        if (instance == null) {
            instance = new DBConnectionSingleton();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("Reconectado a PostgreSQL");
            }
        } catch (SQLException e) {
            System.out.println("Error al reconectarse a PostgreSQL");
            e.printStackTrace();
        }
        return connection;
    }

}
