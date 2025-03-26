package kz.kstu.kutsinas.course_project.db.academic_workload.service;

import kz.kstu.kutsinas.course_project.db.academic_workload.dao.UserDAO;

import java.sql.Connection;


public class AuthService {
    private final UserDAO userDao;

    public AuthService() {
        this.userDao = new UserDAO();
    }

    public boolean loginUser(String login, String password) {
        Connection conn = userDao.authenticateUser(login, password);
        if (conn != null) {
            String role = userDao.getUserRole(conn);
            int userId = userDao.getUserId(conn);
            int departmentId;
            if ("responsibleForWorkload".equals(role)) {
                departmentId = userDao.getDepartmentId(conn);

                System.out.println("Соединение с БД прошло успешно, ваша роль: " + role);

                UserSession.getInstance().setUser(conn, login, role, userId, departmentId);
                return true;

            } else if (role != null) {
                System.out.println("Соединение с БД прошло успешно, ваша роль: " + role);

                UserSession.getInstance().setUser(conn, login, role, userId);
                return true;
            }
        }
        return false;
    }
    }



