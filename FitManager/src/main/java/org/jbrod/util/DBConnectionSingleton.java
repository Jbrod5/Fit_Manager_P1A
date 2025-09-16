package org.jbrod.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionSingleton {

    // Configuración de la BD
    private static final String HOST = "localhost";
    private static final int PUERTO = 5432; // puerto por defecto de PostgreSQL
    private static final String DB = "fitmanager";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "";

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + DB;

    // Instancia única del singleton
    private static DBConnectionSingleton instance;

    private Connection connection;

    // Constructor privado para que no se pueda instanciar desde afuera
    private DBConnectionSingleton() {
        try {
            connection = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión a PostgreSQL exitosa");
        } catch (SQLException e) {
            System.out.println("Error al conectarse a PostgreSQL");
            e.printStackTrace();
        }
    }

    // Metodo para obtener la instancia única
    public static DBConnectionSingleton getInstance() {
        if (instance == null) {
            instance = new DBConnectionSingleton();
        }
        return instance;
    }

    // Metodo para obtener la conexión
    public Connection getConnection() {
        return connection;
    }


}
