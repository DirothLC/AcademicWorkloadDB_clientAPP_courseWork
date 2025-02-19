package kz.kstu.kutsinas.course_project.db.academic_workload.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=Academic_Workload;encrypt=true;trustServerCertificate=true";

    public static Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(URL, username, password);
    }
}
