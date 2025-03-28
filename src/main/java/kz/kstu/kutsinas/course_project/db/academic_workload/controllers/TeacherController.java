package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.Executioner;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.ViewLoader;

import java.util.List;
import java.util.Map;

public class TeacherController {
    @FXML
    private RadioButton viewButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TreeView<String> actionsList;
    @FXML
    private TableView tableView;

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
        TreeItem<String> selectedItem = actionsList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        String selectedAction = selectedItem.getValue();
        System.out.println("Выбрано: " + selectedAction);

        UserSession sessionContext = UserSession.getInstance();
        int id= sessionContext.getUserId();

        String query = switch (selectedAction) {
            case "Доступные виды нагрузки" -> "SELECT * FROM TypesOfAcademicWorkload";
            case "Данные преподавателя" -> "SELECT * FROM Teachers WHERE id = " + id;
            case "Нагрузка" -> "SELECT * FROM AcademicWorkload WHERE teacherID = " + id;
            case "Дисциплины" -> "SELECT * FROM Discipline";
            case "Группы" -> "SELECT * FROM AcademicGroup";
            case "Кафедры" -> "SELECT * FROM Department";
            case "Должности" -> "SELECT * FROM JobTitle";
            case "Допустимые виды нагрузки" -> "SELECT * FROM AvailableTypeOfWorkload";
            default -> null;
        };

        if (query != null) {
            List<Map<String, Object>> result = Executioner.executeQuery(query);
            updateTableView(result);
        }
    }

    private void updateTableView(List<Map<String, Object>> data) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        if (data.isEmpty()) return;
        Map<String, Object> firstRow = data.get(0);
        for (String columnName : firstRow.keySet()) {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().get(columnName).toString()));
            tableView.getColumns().add(column);
        }

        tableView.getItems().addAll(data);
    }

    @FXML
    protected void onLogoutButtonClick(){
        ViewLoader.loadStage("logout-view.fxml","Подтверждение выхода");
    }
}
