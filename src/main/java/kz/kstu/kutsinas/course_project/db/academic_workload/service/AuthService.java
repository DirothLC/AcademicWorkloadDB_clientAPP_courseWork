package kz.kstu.kutsinas.course_project.db.academic_workload.service;

import kz.kstu.kutsinas.course_project.db.academic_workload.dao.UserDAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.dto.UserResponse;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;


public class AuthService {

    private final UserDAO userDao;

    public AuthService() {
        this.userDao = new UserDAO();
    }

    public boolean loginUser(String login, String password) {
        UserResponse userResponse = userDao.authenticateUser(login, password);

        if (userResponse == null) {
            System.out.println("Ошибка аутентификации через API");
            return false;
        }

        System.out.println("Авторизация успешна через API");
        System.out.println("Роль: " + userResponse.getRole());
        System.out.println("UserID: " + userResponse.getUserId() +
                ", DepartmentID: " + userResponse.getDepartmentId());


        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection(login, password);
            if (conn != null) {
                System.out.println("Подключение к внутренней БД успешно");
            } else {
                System.out.println("Не удалось подключиться к внутренней БД");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к внутренней БД:");
            e.printStackTrace();
        }


        if (conn != null) {
            if (userResponse.getDepartmentId() != null) {
                UserSession.getInstance().setUser(
                        conn,
                        userResponse.getLogin(),
                        userResponse.getRole(),
                        userResponse.getUserId(),
                        userResponse.getDepartmentId()
                );
            } else {
                UserSession.getInstance().setUser(
                        conn,
                        userResponse.getLogin(),
                        userResponse.getRole(),
                        userResponse.getUserId()
                );
            }
        }

        return true;
    }
}





//public class AuthService {
//    private final UserDAO userDao;
//
//    public AuthService() {
//        this.userDao = new UserDAO();
//    }
//
//    public boolean loginUser(String login, String password) {
//        Connection conn = userDao.authenticateUser(login, password);
//        if (conn != null) {
//            String role = userDao.getUserRole(conn);
//            int userId = userDao.getUserId(conn, login);
//            int departmentId;
//            if ("responsibleForWorkload".equals(role)) {
//                departmentId = userDao.getDepartmentId(conn, login);
//
//                System.out.println("Соединение с БД прошло успешно, ваша роль: " + role);
//                System.out.println(userId+", " + departmentId);
//                UserSession.getInstance().setUser(conn, login, role, userId, departmentId);
//                return true;
//
//            } else if (role != null) {
//                System.out.println("Соединение с БД прошло успешно, ваша роль: " + role);
//                System.out.println(userId);
//                UserSession.getInstance().setUser(conn, login, role, userId);
//                return true;
//            }
//        }
//        return false;
//    }
//}



