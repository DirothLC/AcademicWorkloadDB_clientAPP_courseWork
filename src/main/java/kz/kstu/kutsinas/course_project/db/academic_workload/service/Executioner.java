package kz.kstu.kutsinas.course_project.db.academic_workload.service;

import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executioner {
    public static List<Map<String, Object>> executeQuery(String query) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        Connection conn = UserSession.getInstance().getConnection();
        if (conn == null) {
            System.out.println("Ошибка: соединение с БД не установлено!");
            return resultList;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            Reporter.alertErrorReporting("Ошибка выполнения запроса: " , e.getMessage());
        }
        return resultList;
    }
}
