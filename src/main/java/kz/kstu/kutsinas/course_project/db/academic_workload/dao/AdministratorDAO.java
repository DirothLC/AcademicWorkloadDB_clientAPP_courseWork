package kz.kstu.kutsinas.course_project.db.academic_workload.dao;

import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdministratorDAO {
    public List<Map<String, Object>> getAuditLogs() {
        List<Map<String, Object>> auditLogs = new ArrayList<>();
        String query = "SELECT event_time, action_id, succeeded, server_principal_name, database_name, statement " +
                "FROM sys.fn_get_audit_file('C:\\AuditLogs\\*.sqlaudit', default, default)";

        try {
            Connection conn = UserSession.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                auditLogs.add(row);
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
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



}
