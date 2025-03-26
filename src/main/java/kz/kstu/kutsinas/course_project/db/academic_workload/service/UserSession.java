package kz.kstu.kutsinas.course_project.db.academic_workload.service;

import java.sql.Connection;
import java.sql.SQLException;

public class UserSession {
    private static UserSession instance;
    private Connection connection;
    private String username;
    private String role;
    private int userId;
    private int departmentId;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }


    public void setUser(Connection connection, String username, String role, int userId, int departmentId) {
        this.connection = connection;
        this.username = username;
        this.role = role;
        this.userId = userId;
        this.departmentId = departmentId;
    }
    public void setUser(Connection connection, String username, String role, int userId) {
        this.connection = connection;
        this.username = username;
        this.role = role;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void logout() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Соединение с БД разорвано");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
            username = null;
            role = null;
            userId = 0;
            departmentId = 0;
            instance = null;
        }
    }
}
