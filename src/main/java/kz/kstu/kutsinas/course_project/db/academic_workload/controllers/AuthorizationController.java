package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.AuthService;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.ViewLoader;
import org.controlsfx.control.textfield.CustomPasswordField;

public class AuthorizationController {
    @FXML
    private TextField loginField;
    @FXML
    private CustomPasswordField passwordField;
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

            String authStatus="Успешная авторизация!";
            String role = UserSession.getInstance().getRole();
            System.out.println(role);
            switch (role) {
                case "teacher":
                    ViewLoader.loadScene("teacher-view.fxml", loginButton);
                    Reporter.alertConfirmReporting(authStatus,"Вы вошли в систему как Учитель");
                    break;
                case "dean":
                    ViewLoader.loadScene("dean-view.fxml", loginButton);
                    Reporter.alertConfirmReporting(authStatus,"Вы вошли в систему как Декан");
                    break;
                case "administrator":
                    ViewLoader.loadScene("administrator-view.fxml", loginButton);
                    Reporter.alertConfirmReporting(authStatus,"Вы вошли в систему как Администратор");
                    break;
                case "responsibleForWorkload":
                    ViewLoader.loadScene("responsible_for_workload-view.fxml", loginButton);
                    Reporter.alertConfirmReporting(authStatus,"Вы вошли в систему как Ответственный по нагрузке");
                    break;
                default:
                    showAlert("Ошибка", "Неизвестная роль пользователя: " + role);
            }
        } else {
            showAlert("Ошибка входа", "Неверный логин или пароль!");
        }
    }

    private void showAlert(String title, String message) {
        Reporter.alertErrorReporting(title,message);
    }
}