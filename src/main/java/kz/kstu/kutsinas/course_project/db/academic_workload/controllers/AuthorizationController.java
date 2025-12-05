package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kz.kstu.kutsinas.course_project.db.academic_workload.dao.TwoFADAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.AuthService;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Logger;
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

        if (!authService.loginUser(login, password)) {
            showAlert("Ошибка входа", "Неверный логин или пароль!");
            return;
        }

        int userId = UserSession.getInstance().getUserId();

        TwoFADAO twoFADAO = new TwoFADAO();

        if (twoFADAO.isTwoFAEnabled(userId)) {
            openOTPStage();
            Logger.info("OTP Authorization",UserSession.getInstance().getUsername(), "AuthorizationController");
            return;
        }

        openUserScene();
        Logger.info("User authorization",UserSession.getInstance().getUsername(),"AuthorizationController");
    }
    private void openOTPStage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kz/kstu/kutsinas/course_project/db/academic_workload/views/otp-view.fxml"));
            Parent root = loader.load();

            OTPController controller = loader.getController();
            controller.setCallback(this::openUserScene);

            Stage stage = new Stage();
            stage.setTitle("Введите OTP код");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(loginButton.getScene().getWindow());

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void openUserScene() {
        String role = UserSession.getInstance().getRole();
        String authStatus = "Успешная авторизация!";

        switch (role) {
            case "teacher":
                ViewLoader.loadScene("teacher-view.fxml", loginButton);
                Reporter.alertConfirmReporting(authStatus, "Вы вошли как Учитель");
                break;
            case "dean":
                ViewLoader.loadScene("dean-view.fxml", loginButton);
                Reporter.alertConfirmReporting(authStatus, "Вы вошли как Декан");
                break;
            case "administrator":
                ViewLoader.loadScene("administrator-view.fxml", loginButton);
                Reporter.alertConfirmReporting(authStatus, "Вы вошли как Администратор");
                break;
            case "responsibleForWorkload":
                ViewLoader.loadScene("responsible_for_workload-view.fxml", loginButton);
                Reporter.alertConfirmReporting(authStatus, "Вы вошли как Ответственный по нагрузке");
                break;
            default:
                showAlert("Ошибка", "Неизвестная роль пользователя: " + role);
                break;
        }
    }


    private void showAlert(String title, String message) {
        Reporter.alertErrorReporting(title,message);
    }
}