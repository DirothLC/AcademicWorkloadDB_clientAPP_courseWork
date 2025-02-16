package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AuthorizationController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}