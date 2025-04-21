package kz.kstu.kutsinas.course_project.db.academic_workload.controllers.admin_functions_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kz.kstu.kutsinas.course_project.db.academic_workload.controllers.AdministratorController;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.Executioner;

import java.util.List;
import java.util.Map;

public class UserControlController extends AdministratorController {
    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private ComboBox roleComboBox;

    @FXML
    private TableView usersTable;

    @Override
    public void initialize(){
        List<Map<String, Object>> usersData = Executioner.executeQuery("SELECT * FROM Users");
        loadDataToTableView(usersTable, usersData);
    }

    @FXML
    void onCreateUserButtonClick() {

    }

    @FXML
    void onDisableUserButtonClick() {

    }



}
