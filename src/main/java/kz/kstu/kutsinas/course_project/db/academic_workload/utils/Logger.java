package kz.kstu.kutsinas.course_project.db.academic_workload.utils;

import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Logger {

    private static final String INSERT_LOG_SQL = "INSERT INTO log_console (message, log_level, user_login, source) VALUES (?, ?, ?, ?)";

    /**
     * Универсальный метод записи лога в БД.
     *
     * @param message   Сообщение для лога
     * @param level     Уровень логирования (INFO, WARN, ERROR и т.д.)
     * @param userLogin Логин пользователя
     * @param source    Источник вызова (например, имя класса или модуля)
     */
    public static void logAction(String message, String level, String userLogin, String source) {
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_LOG_SQL)) {

            stmt.setString(1, message);
            stmt.setString(2, level);
            stmt.setString(3, userLogin);
            stmt.setString(4, source);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка записи лога: " + e.getMessage());
        }
    }


    public static void info(String message, String userLogin, String source) {
        logAction(message, "INFO", userLogin, source);
    }

    public static void warn(String message, String userLogin, String source) {
        logAction(message, "WARN", userLogin, source);
    }

    public static void error(String message, String userLogin, String source) {
        logAction(message, "ERROR", userLogin, source);
    }

}
