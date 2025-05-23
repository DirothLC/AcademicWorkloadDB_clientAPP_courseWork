package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import kz.kstu.kutsinas.course_project.db.academic_workload.dao.DeanDAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.Executioner;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.ViewLoader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeanController {
    @FXML
    private Button logoutButton;
    @FXML
    private Button addButton;

    @FXML
    private TextField dataField;

    @FXML
    private TreeView<String> actionsList;

    @FXML
    private TableView tableView;

    private final DeanDAO DAO= new DeanDAO();

    public void initialize(){
        tableView.setEditable(true);

        TreeItem<String> tableRoot= new TreeItem<>("Доступные таблицы:");
        TreeItem<String> queriesRoot = new TreeItem<>("Доступные запросы:");
        TreeItem<String> viewsRoot= new TreeItem<>("Доступные представления:");
        TreeItem<String> proceduresRoot= new TreeItem<>("Доступные хранимые процедуры:");

        TreeItem<String> typesOfAcademicWorkload= new TreeItem<>("Доступные виды нагрузки");
        TreeItem<String> teachers= new TreeItem<>("Список преподавателей");
        TreeItem<String> academicWorkload= new TreeItem<>("Нагрузка");
        TreeItem<String> discipline= new TreeItem<>("Дисциплины");
        TreeItem<String> academicGroup= new TreeItem<>("Группы");
        TreeItem<String> department= new TreeItem<>("Кафедры");
        TreeItem<String> jobTitle= new TreeItem<>("Должности");
        TreeItem<String> availableTypeOfWorkload = new TreeItem<>("Допустимые виды нагрузки");
        TreeItem<String> normative= new TreeItem<>("Норматив");

        tableRoot.getChildren().addAll(typesOfAcademicWorkload,teachers,academicWorkload,discipline,academicGroup,department,jobTitle,availableTypeOfWorkload,normative);
        tableRoot.setExpanded(true);

        TreeItem<String> workloadTypesByFirstTeacher = new TreeItem<>("Список видов учебных нагрузок I-го преподавателя");
        TreeItem<String> totalHoursAutumnByDepartment = new TreeItem<>("Суммарное количество часов нагрузки преподавателей I-той кафедры, запланированное на осень");
        TreeItem<String> springDisciplinesContaining = new TreeItem<>("Список дисциплин, запланированных весной в наименовании которых встречается слово...");
        TreeItem<String> plannedHoursByRate = new TreeItem<>("Вычислить плановое количество часов нагрузки для всех преподавателей, исходя из размера ставки");
        TreeItem<String> associateProfessorsCountByDepartment = new TreeItem<>("Сколько доцентов работает на I-той кафедре");

        queriesRoot.getChildren().addAll(workloadTypesByFirstTeacher,totalHoursAutumnByDepartment,springDisciplinesContaining,plannedHoursByRate,associateProfessorsCountByDepartment);



        TreeItem<String> teachersWorkloadByDepartment= new TreeItem<>("Нагрузка преподавателей");
        TreeItem<String> disciplineListOnSemesterOnGroupByDepartment= new TreeItem<>("Список дисциплин на семестр по группам");
        TreeItem<String> diplomaLeaderListByDepartment= new TreeItem<>("Список дипломных руководителей");

        viewsRoot.getChildren().addAll(teachersWorkloadByDepartment,disciplineListOnSemesterOnGroupByDepartment,diplomaLeaderListByDepartment);

        TreeItem<String> halfRateTeachersByDepartment= new TreeItem<>("Преподаватели с половинной ставкой");
        TreeItem<String> groupMoreThan20Poked= new TreeItem<>("Группы с количеством студентов более 20");
        TreeItem<String> studentsCount= new TreeItem<>("Количество студентов по кафедрам");

        proceduresRoot.getChildren().addAll(halfRateTeachersByDepartment,groupMoreThan20Poked, studentsCount);

        TreeItem<String> root= new TreeItem<>("Доступные действия:");
        root.getChildren().addAll(tableRoot,queriesRoot,viewsRoot,proceduresRoot);
        root.setExpanded(true);

        actionsList.setRoot(root);
    }

    @FXML
    public void onActionSelected() {


        TreeItem<String> selectedItem = actionsList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        String action = selectedItem.getValue();
        System.out.println("Выбрано: " + action);



        UserSession sessionContext = UserSession.getInstance();
        int id = sessionContext.getUserId();
        int departmentId = sessionContext.getDepartmentId();

        String query = DAO.querySelector(action, departmentId);
        DAO.extractTableNameFromKey(action);
        tableView.setEditable(true);

        if(!dataField.getText().isEmpty()){
            query = query.replace("{data}", dataField.getText());
        }

        List<Map<String, Object>> result = Executioner.executeQuery(query);
        updateTableView(result);



    }

    @FXML
    private void onAddButtonClick(){
        Map<String, Object> newRow = new HashMap<>();
        List<String> columnNames = new ArrayList<>();
        for (Object obj : tableView.getColumns()) {
            TableColumn<Map<String, Object>, ?> col = (TableColumn<Map<String, Object>, ?>) obj;
            columnNames.add(col.getText());
        }
        for (String column : columnNames) {
            newRow.put(column, null);
        }
        tableView.getItems().add(newRow);
    }

    @FXML
    private void onSaveButtonClick(){
        List<Map<String, Object>> items = tableView.getItems();
        if (items == null || items.isEmpty()) return;

        String tableName = DAO.getCurrentTable();

        for (Map<String, Object> row : items) {
            try {
                    DAO.insertRow(tableName, row);
            } catch (SQLException e) {
                Reporter.alertErrorReporting("Ошибка при сохранении строки: " , e.getMessage());
                e.printStackTrace();
            }
        }


    }

    @FXML
    private void onExportButtonClick(){
        Reporter.exportTableViewToExcel(tableView,(Stage) tableView.getScene().getWindow());
    }

    private void updateTableView(List<Map<String, Object>> data) {
        String tableName = DAO.getCurrentTable();

        tableView.getItems().clear();
        tableView.getColumns().clear();

        if (data.isEmpty()) return;

        Map<String, Object> firstRow = data.get(0);

        for (String columnName : firstRow.keySet()) {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(columnName);

            column.setCellValueFactory(cellData -> {
                Object value = cellData.getValue().get(columnName);
                return new SimpleStringProperty(value == null ? "" : value.toString());
            });

            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> {
                Map<String, Object> row = event.getRowValue();
                String newValue = event.getNewValue();
                row.put(columnName, newValue);
                updateRowInDatabase(tableName,row);
                System.out.println("Строка изменена:" + row);
            });

            tableView.getColumns().add(column);
        }

        tableView.getItems().addAll(data);
        tableView.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Удалить");
            deleteItem.setOnAction(e -> {
                Map<String, Object> item = row.getItem();
                if (item != null) {
                    deleteRowFromDatabase(tableName,item);
                    tableView.getItems().remove(item);
                    System.out.println("Строка удалена:" + item);
                }
            });
            menu.getItems().addAll(deleteItem);
            row.setContextMenu(menu);
            return row;
        });
    }


    private void updateRowInDatabase(String tableName, Map<String, Object> row) {
        try {
            DAO.updateRow(tableName, row);
        } catch (SQLException e) {
            e.printStackTrace();
            Reporter.alertErrorReporting("Ошибка при обновлении строки", e.getMessage());
        }
    }

    private void deleteRowFromDatabase(String tableName, Map<String, Object> row) {
        try {
            DAO.deleteRow(tableName, row);
        } catch (SQLException e) {
            e.printStackTrace();
            Reporter.alertErrorReporting("Ошибка при удалении строки", e.getMessage());
        }
    }


    @FXML
    protected void onLogoutButtonClick(){
        ViewLoader.loadStage("logout-view.fxml","Подтверждение выхода");
    }
}
