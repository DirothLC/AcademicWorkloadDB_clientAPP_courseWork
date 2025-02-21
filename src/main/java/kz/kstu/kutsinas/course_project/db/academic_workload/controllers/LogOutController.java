package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Manager;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.ViewLoader;

public class LogOutController {
    @FXML
    private Button confirmButton;
    @FXML
    private Button rejectButton;

    @FXML
    protected void onConfirmButtonClick(){
        UserSession.getInstance().logout();
        Manager.closeAllWindows();
        ViewLoader.loadStage("authorization-view.fxml","AcademicWorkload");
        Reporter.alertConfirmReporting("Выход из аккаунта","Вы вышли из учетной записи, соединение с БД разорвано.\n " +
                "Необходима повторная авторизация");
    }

    @FXML
    protected void onRejectButtonClick(){
       Stage stage=(Stage) rejectButton.getScene().getWindow();
       stage.close();
    }
}
