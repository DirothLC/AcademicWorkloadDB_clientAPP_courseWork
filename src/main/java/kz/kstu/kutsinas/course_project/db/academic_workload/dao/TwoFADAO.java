package kz.kstu.kutsinas.course_project.db.academic_workload.dao;

import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TwoFADAO {
    public String getSecretForUser(int userId) {
        String sql = "SELECT secret_key FROM user_2fa WHERE user_id = ?";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("secret_key");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEnabled(int userId) {
        String sql = "SELECT is_enabled FROM user_2fa WHERE user_id = ?";
        Connection conn = UserSession.getInstance().getConnection();
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("is_enabled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Если записи нет — создаёт с новым secret и is_enabled = 0.
     * Если есть — возвращает текущий secret.
     * Возвращает секрет (Base32).
     */
    public String createOrGetSecret(int userId, String secretBase32) {
        String existing = getSecretForUser(userId);
        if (existing != null) return existing;

        String insert = "INSERT INTO user_2fa (user_id, secret_key, is_enabled) VALUES (?, ?, 0)";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, userId);
            ps.setString(2, secretBase32);
            ps.executeUpdate();
            return secretBase32;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean enable2FA(int userId) {
        String update = "UPDATE user_2fa SET is_enabled = 1 WHERE user_id = ?";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disable2FA(int userId) {
        String update = "UPDATE user_2fa SET is_enabled = 0 WHERE user_id = ?";
        Connection conn = UserSession.getInstance().getConnection();
        try ( PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete2FA(int userId) {
        String del = "DELETE FROM user_2fa WHERE user_id = ?";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(del)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isTwoFAEnabled(int userId) {
        String sql = "SELECT is_enabled FROM user_2fa WHERE user_id = ?";
        Connection conn = UserSession.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("is_enabled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getSecret(int userId) {
        String sql = "SELECT secret_key FROM user_2fa WHERE user_id = ?";
        Connection conn = UserSession.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("secret_key");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
