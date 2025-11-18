package kz.kstu.kutsinas.course_project.db.academic_workload.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kstu.kutsinas.course_project.db.academic_workload.dto.UserResponse;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.DatabaseConnection;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;


public class UserDAO {

    private static final String AUTH_URL = "http://localhost:8080/api/auth/login";

    public UserResponse authenticateUser(String login, String password) {
        try {
            URL url = new URL(AUTH_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            String jsonInput = String.format("{\"login\":\"%s\", \"password\":\"%s\"}", login, password);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = conn.getInputStream()) {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(is, UserResponse.class);
                }
            } else {
                System.out.println("Ошибка авторизации: " + responseCode);
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    br.lines().forEach(System.out::println);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}



//public class UserDAO {
//    public Connection authenticateUser(String login, String password) {
//        try {
//            return DatabaseConnection.getConnection(login, password);
//        } catch (SQLException e) {
//            return null;
//        }
//    }
//
//    public String getUserRole(Connection conn) {
//        String query = "SELECT CASE " +
//                "WHEN IS_MEMBER('TeacherRole') = 1 THEN 'teacher' " +
//                "WHEN IS_MEMBER('DeanRole') = 1 THEN 'dean' " +
//                "WHEN IS_MEMBER('db_admin_role') = 1 THEN 'administrator' " +
//                "WHEN IS_MEMBER('ResponsibleForWorkloadRole') = 1 THEN 'responsibleForWorkload' " +
//                "ELSE 'unknown' END AS UserRole";
//
//        try (PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//
//            if (rs.next()) {
//                return rs.getString("UserRole");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public int getUserId(Connection conn, String login) {
//        String query = "SELECT id FROM Users WHERE login = ?";
//
//        try (PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, login);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("id");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    public int getDepartmentId(Connection conn, String login) {
//        String query = "SELECT department_id FROM Users WHERE login = ?";
//
//        try (PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, login);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("department_id");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//}
