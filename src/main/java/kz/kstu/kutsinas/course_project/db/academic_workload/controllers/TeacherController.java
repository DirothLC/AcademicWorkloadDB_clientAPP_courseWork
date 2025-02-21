package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.ViewLoader;

public class TeacherController {
    @FXML
    private RadioButton viewButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TreeView<String> actionsList;

    public void initialize(){
        TreeItem<String> root= new TreeItem<>("Доступные действия:");

        TreeItem<String> typesOfAcademicWorkload= new TreeItem<>("Доступные виды нагрузки");
        TreeItem<String> teachers= new TreeItem<>("Данные преподавателя");
        TreeItem<String> academicWorkload= new TreeItem<>("Нагрузка");
        TreeItem<String> discipline= new TreeItem<>("Дисциплины");
        TreeItem<String> academicGroup= new TreeItem<>("Группы");
        TreeItem<String> department= new TreeItem<>("Кафедры");
        TreeItem<String> jobTitle= new TreeItem<>("Должности");
        TreeItem<String> availableTypeOfWorkload = new TreeItem<>("Допустимые виды нагрузки");

        root.getChildren().addAll(typesOfAcademicWorkload,teachers,academicWorkload,discipline,academicGroup,department,jobTitle,availableTypeOfWorkload);
        root.setExpanded(true);

        actionsList.setRoot(root);
    }
    @FXML
    protected void onActionSelected(){

    }
    @FXML
    protected void onLogoutButtonClick(){
        ViewLoader.loadStage("logout-view.fxml","Подтверждение выхода");
    }
}
