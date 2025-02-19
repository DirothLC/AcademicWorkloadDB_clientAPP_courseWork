package kz.kstu.kutsinas.course_project.db.academic_workload.service;

import java.sql.Connection;
import java.sql.SQLException;

public class UserSession {
    private static UserSession instance;
    private Connection connection;
    private String username;
    private String role;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(Connection connection, String username, String role) {
        this.connection = connection;
        this.username = username;
        this.role = role;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void logout() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
            username = null;
            role = null;
            instance = null;
        }
    }
}
