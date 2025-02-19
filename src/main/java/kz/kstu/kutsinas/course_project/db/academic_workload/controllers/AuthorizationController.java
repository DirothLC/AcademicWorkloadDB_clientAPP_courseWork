package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kz.kstu.kutsinas.course_project.db.academic_workload.dao.UserDAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.AuthService;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.SceneLoader;

import java.sql.Connection;

public class AuthorizationController {
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;

    private final AuthService authService = new AuthService();

    @FXML
    protected void onSignInButtonClick() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Логин и пароль не могут быть пустыми!");
            return;
        }


        if (authService.loginUser(login, password)) {

            String role = UserSession.getInstance().getRole();
            System.out.println(role);
            switch (role) {
                case "teacher":
                    SceneLoader.loadScene("teacher-view.fxml", loginButton);
                    break;
                case "dean":
                    SceneLoader.loadScene("dean-view.fxml", loginButton);
                    break;
                case "administrator":
                    SceneLoader.loadScene("admin-view.fxml", loginButton);
                    break;
                case "responsibleForWorkload":
                    SceneLoader.loadScene("responsible_for_workload-view.fxml", loginButton);
                    break;
                default:
                    showAlert("Ошибка", "Неизвестная роль пользователя: " + role);
            }
        } else {
            showAlert("Ошибка входа", "Неверный логин или пароль!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}