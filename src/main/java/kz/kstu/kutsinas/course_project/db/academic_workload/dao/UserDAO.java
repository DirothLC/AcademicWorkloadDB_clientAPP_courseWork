package kz.kstu.kutsinas.course_project.db.academic_workload.dao;

import kz.kstu.kutsinas.course_project.db.academic_workload.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public Connection authenticateUser(String login, String password) {
        try {
            return DatabaseConnection.getConnection(login, password);
        } catch (SQLException e) {
            return null;
        }
    }


    public String getUserRole(Connection conn) {
        String query = "SELECT CASE " +
                "WHEN IS_MEMBER('TeacherRole') = 1 THEN 'teacher' " +
                "WHEN IS_MEMBER('DeanRole') = 1 THEN 'dean' " +
                "WHEN IS_MEMBER('AdministratorRole') = 1 THEN 'administrator' " +
                "WHEN IS_MEMBER('ResponsibleForWorkloadRole') = 1 THEN 'responsibleForWorkload' " +
                "ELSE 'unknown' END AS UserRole";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("UserRole");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
