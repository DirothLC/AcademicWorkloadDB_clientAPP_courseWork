package kz.kstu.kutsinas.course_project.db.academic_workload.controllers.admin_functions_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kz.kstu.kutsinas.course_project.db.academic_workload.controllers.AdministratorController;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.Executioner;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Logger;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;

import java.util.List;
import java.util.Map;

public class UserControlController extends AdministratorController {
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField idField;
    @FXML
    private TextField departmentIdField;

    @FXML
    private ComboBox roleComboBox;

    @FXML
    private TableView usersTable;

    @Override
    public void initialize(){
       updateUsersTable();
        roleComboBox.getItems().addAll("TeacherRole","ResponsibleForWorkloadRole","DeanRole");
    }

    @FXML
    void onCreateUserButtonClick() {
        if(loginField.getText().isEmpty() || passwordField.getText().isEmpty() || idField.getText().isEmpty() || roleComboBox.getValue() == null) {
            Reporter.alertErrorReporting("Ошибка", "Пожалуйста, заполните все поля.");
            return;
        }
        String login = loginField.getText();
        String password = passwordField.getText();
        int id= Integer.parseInt(idField.getText());
        String role = (String) roleComboBox.getValue();
        String departmentId = departmentIdField.getText();

        String federalRole;
        switch (role) {
            case "TeacherRole":
                federalRole = "teacher";
                break;
            case "ResponsibleForWorkloadRole":
                federalRole = "responsibleForWorkload";
                break;
            case "DeanRole":
                federalRole = "dean";
                break;
            default:
                federalRole="unknown";
            return;
        }

        if(departmentId.isEmpty()){
            DAO.createDatabaseUser(login, password, role,id);
            updateUsersTable();
            DAO.sendUserToServer(id, login, password, federalRole,
                    departmentId.isEmpty() ? null : Integer.parseInt(departmentId));
            Logger.info("User Created", UserSession.getInstance().getUsername(), "Administrator");
        }else {
            DAO.createDatabaseUser(login, password, role,id, Integer.parseInt(departmentId));
            updateUsersTable();
            DAO.sendUserToServer(id, login, password, federalRole,
                    departmentId.isEmpty() ? null : Integer.parseInt(departmentId));
            Logger.info("User Created", UserSession.getInstance().getUsername(), "Administrator");

        }

    }

    @FXML
    void onDisableUserButtonClick() {
        DAO.deleteDatabaseUserById(Integer.parseInt(idField.getText()));
        updateUsersTable();
        Logger.warn("User Deleted", UserSession.getInstance().getUsername(), "Administrator");

    }
    private void updateUsersTable(){
        List<Map<String, Object>> usersData = Executioner.executeQuery("SELECT * FROM Users");
        loadDataToTableView(usersTable, usersData);
    }



}
