package kz.kstu.kutsinas.course_project.db.academic_workload.dao;

import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Logger;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdministratorDAO {

    public List<Map<String, Object>> getAuditLogs() {
        List<Map<String, Object>> auditLogs = new ArrayList<>();
        String query = "SELECT id, message, log_level, user_login, source, created_at FROM log_console ORDER BY created_at DESC";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                auditLogs.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении логов: " + e.getMessage());
            e.printStackTrace();
        }

        return auditLogs;
    }


    public void createDatabaseBackup(String folderPath, String fileName) {
        String backupFilePath = folderPath + File.separator + fileName + ".bak";
        String query = "BACKUP DATABASE Academic_Workload TO DISK = ? WITH INIT";

        try  {
            Connection conn = UserSession.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, backupFilePath);
            stmt.executeUpdate();
            Reporter.alertConfirmReporting("Успех", "Резервная копия базы данных успешно создана: " + backupFilePath);
           stmt.close();

        } catch (SQLException e) {
            Reporter.alertErrorReporting("Ошибка", "Не удалось создать резервную копию базы данных: " + e.getMessage());

        }
    }
    public void restoreDatabaseFromBackup(File backupFile) {
        String databaseName = "Academic_Workload";
        String backupFilePath = backupFile.getAbsolutePath();

        String setSingleUser = "ALTER DATABASE " + databaseName + " SET SINGLE_USER WITH ROLLBACK IMMEDIATE";
        String restoreQuery = "RESTORE DATABASE " + databaseName + " FROM DISK = ? WITH REPLACE";
        String setMultiUser = "ALTER DATABASE " + databaseName + " SET MULTI_USER";

        try  {
            Connection conn = UserSession.getInstance().getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(setSingleUser);
            }

            try (PreparedStatement restoreStmt = conn.prepareStatement(restoreQuery)) {
                restoreStmt.setString(1, backupFilePath);
                restoreStmt.executeUpdate();
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(setMultiUser);
            }

            Reporter.alertConfirmReporting("Успех", "База данных успешно восстановлена из резервной копии: " + backupFilePath);


        } catch (SQLException e) {
            Reporter.alertErrorReporting("Ошибка", "Не удалось восстановить базу данных из резервной копии: " + e.getMessage());
        }
    }
    public void createDatabaseUser(String login, String password, String role, int linkedId) {
        String safePassword = password.replace("'", "''");

        String createLoginSQL = "CREATE LOGIN [" + login + "] WITH PASSWORD = '" + safePassword + "'";
        String createUserSQL = "CREATE USER [" + login + "] FOR LOGIN [" + login + "]";
        String addToRoleSQL = "ALTER ROLE [" + role + "] ADD MEMBER [" + login + "]";

        try  {
            Connection connection = UserSession.getInstance().getConnection();
            connection.setAutoCommit(false);
            try (Statement stmt1 = connection.createStatement()) {
                stmt1.executeUpdate(createLoginSQL);
            }
            try (Statement stmt2 = connection.createStatement()) {
                stmt2.executeUpdate(createUserSQL);

            }
            try (Statement stmt3 = connection.createStatement()) {
                stmt3.executeUpdate(addToRoleSQL);

            }
            String insertLocalUser = "INSERT INTO users (login, role, id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt4 = connection.prepareStatement(insertLocalUser)) {
                stmt4.setString(1, login);
                stmt4.setString(2, role);
                stmt4.setInt(3, linkedId);
                stmt4.executeUpdate();
                Logger.info("Administrator Created User Login: " + login, UserSession.getInstance().getUsername(), "AdministratorDAO");

            }

            connection.commit();

            Reporter.alertConfirmReporting("Успех", "Пользователь успешно создан: " + login);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            Reporter.alertErrorReporting("Ошибка", "Не удалось создать пользователя: " + e.getMessage());
        }

    }

    public void createDatabaseUser(String login, String password, String role, int linkedId, int departmentId) {
        String safePassword = password.replace("'", "''");

        String createLoginSQL = "CREATE LOGIN [" + login + "] WITH PASSWORD = '" + safePassword + "'";
        String createUserSQL = "CREATE USER [" + login + "] FOR LOGIN [" + login + "]";
        String addToRoleSQL = "ALTER ROLE [" + role + "] ADD MEMBER [" + login + "]";

        try  {
            Connection connection = UserSession.getInstance().getConnection();
            connection.setAutoCommit(false);

            try (Statement stmt1 = connection.createStatement()) {
                stmt1.executeUpdate(createLoginSQL);
            }

            try (Statement stmt2 = connection.createStatement()) {
                stmt2.executeUpdate(createUserSQL);
            }

            try (Statement stmt3 = connection.createStatement()) {
                stmt3.executeUpdate(addToRoleSQL);
            }

            String insertLocalUser = "INSERT INTO users (login, role, id, department_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt4 = connection.prepareStatement(insertLocalUser)) {
                stmt4.setString(1, login);
                stmt4.setString(2, role);
                stmt4.setInt(3, linkedId);
                stmt4.setInt(4, departmentId);
                stmt4.executeUpdate();
                Logger.info("Administrator Created User Login: " + login, UserSession.getInstance().getUsername(), "AdministratorDAO");

            }

            connection.commit();
            Reporter.alertConfirmReporting("Успех", "Пользователь успешно создан: " + login);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            Reporter.alertErrorReporting("Ошибка", "Не удалось создать пользователя: " + e.getMessage());

        }
    }

    public void deleteDatabaseUserById(int userId) {
        String selectLoginSQL = "SELECT login FROM users WHERE id = ?";
        String deleteUserSQLTemplate = "DROP USER [%s]";
        String deleteLoginSQLTemplate = "DROP LOGIN [%s]";
        String deleteLocalRecordSQL = "DELETE FROM users WHERE id = ?";

        try  {
            Connection connection = UserSession.getInstance().getConnection();
            connection.setAutoCommit(false);

            String login = null;

            try (PreparedStatement stmt1 = connection.prepareStatement(selectLoginSQL)) {
                stmt1.setInt(1, userId);
                try (ResultSet rs = stmt1.executeQuery()) {
                    if (rs.next()) {
                        login = rs.getString("login");
                    } else {
                        Reporter.alertErrorReporting("Ошибка", "Пользователь с таким ID не найден.");
                        return;
                    }
                }
            }

            try (Statement stmt2 = connection.createStatement()) {
                stmt2.executeUpdate(String.format(deleteUserSQLTemplate, login));
            }

            try (Statement stmt3 = connection.createStatement()) {
                stmt3.executeUpdate(String.format(deleteLoginSQLTemplate, login));
            }

            try (PreparedStatement stmt4 = connection.prepareStatement(deleteLocalRecordSQL)) {
                stmt4.setInt(1, userId);
                stmt4.executeUpdate();
            }

            connection.commit();
            Reporter.alertConfirmReporting("Успех", "Пользователь успешно удалён: " + login);
            Logger.info("Administrator Deleted User Login: " + login, UserSession.getInstance().getUsername(), "AdministratorDAO");


        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            Reporter.alertErrorReporting("Ошибка", "Не удалось удалить пользователя: " + e.getMessage());
        }
    }

    public boolean sendUserToServer(int id, String login, String password, String role, Integer departmentId) {
        try {
            URL url = new URL("http://localhost:8080/api/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody;
            if (departmentId != null) {
                jsonBody = String.format(
                        "{\"id\":%d,\"login\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"departmentId\":%d}",
                        id, login, password, role, departmentId
                );
            } else {
                jsonBody = String.format(
                        "{\"id\":%d,\"login\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                        id, login, password, role
                );
            }

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                System.err.println("Ошибка при синхронизации пользователя с сервером: " + responseCode);
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    br.lines().forEach(System.err::println);
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при отправке данных на сервер:");
            e.printStackTrace();
        }

        return false;
    }

    public List<Map<String, Object>> searchLogsWithDate(
            String parameter, String searchValue, LocalDate startDate, LocalDate endDate) {

        List<Map<String, Object>> logs = new ArrayList<>();

        String query =
                "SELECT id, message, log_level, user_login, source, created_at " +
                        "FROM log_console " +
                        "WHERE " + parameter + " LIKE ? " +
                        "AND created_at BETWEEN ? AND ? " +
                        "ORDER BY created_at DESC";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchValue + "%");
            stmt.setTimestamp(2, Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(3, Timestamp.valueOf(endDate.atTime(23, 59, 59)));

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columns; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                logs.add(row);
            }

        } catch (SQLException e) {
            Reporter.alertErrorReporting("Ошибка","Ошибка поиска логов с датой ");
            System.err.println("Ошибка поиска логов с датой: " + e.getMessage());
        }

        return logs;
    }


    public List<Map<String, Object>> searchLogs(
            String parameter, String searchValue) {

        List<Map<String, Object>> logs = new ArrayList<>();

        String query =
                "SELECT id, message, log_level, user_login, source, created_at " +
                        "FROM log_console " +
                        "WHERE " + parameter + " LIKE ? " +
                        "ORDER BY created_at DESC";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchValue + "%");

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columns; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                logs.add(row);
            }

        } catch (SQLException e) {
            Reporter.alertErrorReporting("Ошибка","Ошибка поиска логов ");

            System.err.println("Ошибка поиска логов: " + e.getMessage());
        }

        return logs;
    }
}
