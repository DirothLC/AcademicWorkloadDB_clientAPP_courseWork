package kz.kstu.kutsinas.course_project.db.academic_workload.dao;

import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class DeanDAO {
    private Properties queries;
    private String currentTable = "";

    public DeanDAO() {
        queries = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("dean.queries.properties")) {
            if (input == null) {
                System.err.println("Не удалось найти файл свойств");
                return;
            }
            queries.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            System.out.println(queries);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки ресурса");
        }
    }


    public String querySelector(String selectedAction, int departmentId) {
        String query = queries.getProperty(selectedAction);
        System.out.println(selectedAction);
        if (query != null) {
            return query;
        } else {
            System.err.println("Запрос не найден");
            return null;
        }
    }

    public void extractTableNameFromKey(String key) {
        key = key.replaceAll("[UID]$", "");
        String tableName = queryToTableNameMap.get(key);
        if (tableName != null) {
            currentTable = tableName;
        } else {
            System.err.println("Не удалось определить имя таблицы для ключа: " + key);
        }
    }

    public String getCurrentTable() {
        return currentTable;
    }

    public String getPrimaryKey(String tableName) {
        switch (tableName.toLowerCase()) {
            case "teachers":
                return "id";
            case "academicworkload":
                return "teacherID"; // или workloadID
            case "discipline":
                return "ID";
            case "academicgroup":
                return "groupID";
            case "department":
                return "ID";
            case "jobtitle":
                return "ID";
            case "typesofacademicworkload":
                return "code";
            case "availabletypeofworkload":
                return "availableTypeID";
            case "normative":
                return "rate";
            default:
                return "id";
        }
    }

    private static final Map<String, String> queryToTableNameMap = new HashMap<>();

    static {
        queryToTableNameMap.put("Доступные виды нагрузки", "TypesOfAcademicWorkload");
        queryToTableNameMap.put("Список преподавателей", "Teachers");
        queryToTableNameMap.put("Нагрузка", "AcademicWorkload");
        queryToTableNameMap.put("Дисциплины", "Discipline");
        queryToTableNameMap.put("Группы", "AcademicGroup");
        queryToTableNameMap.put("Кафедры", "Department");
        queryToTableNameMap.put("Должности", "JobTitle");
        queryToTableNameMap.put("Допустимые виды нагрузки", "AvailableTypeOfWorkload");
        queryToTableNameMap.put("Норматив", "Normative");
    }

    public void updateRow(String tableName, Map<String, Object> row) throws SQLException {
        String primaryKey = getPrimaryKey(tableName);
        Object pkValue = row.get(primaryKey);

        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        List<Object> values = new ArrayList<>();

        for (String key : row.keySet()) {
            if (!key.equals(primaryKey)) {
                sql.append(key).append(" = ?, ");
                values.add(row.get(key));
            }
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE ").append(primaryKey).append(" = ?");
        values.add(pkValue);

        Connection conn = UserSession.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql.toString());

        for (int i = 0; i < values.size(); i++) {
            stmt.setObject(i + 1, values.get(i));
        }

        stmt.executeUpdate();
        Logger.info("Update row to " + tableName , UserSession.getInstance().getUsername(),"DeanDAO");
        stmt.close();
    }


    public void deleteRow(String tableName, Map<String, Object> row) throws SQLException {
        String primaryKey = getPrimaryKey(tableName);
        Object pkValue = row.get(primaryKey);

        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?";

        Connection conn = UserSession.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setObject(1, pkValue);

        stmt.executeUpdate();
        Logger.info("Deleted row from " + tableName , UserSession.getInstance().getUsername(),"DeanDAO");
        stmt.close();
    }
    public void insertRow(String tableName, Map<String, Object> row) throws SQLException {
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        List<Object> values = new ArrayList<>();

        for (String key : row.keySet()) {
            columns.add(key);
            placeholders.add("?");
            values.add(row.get(key));
        }

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        Connection conn = UserSession.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < values.size(); i++) {
            stmt.setObject(i + 1, values.get(i));
        }

        try {
            stmt.executeUpdate();
            System.out.println("Успешно вставлено: " + row);
            Logger.info("Inserted row into " + tableName, UserSession.getInstance().getUsername(),"DeanDAO");
        } catch (SQLException e) {
            System.err.println("Строка не вставленная, так как поле с таким же PK уже имеется" );
        }
        stmt.close();
    }

}
