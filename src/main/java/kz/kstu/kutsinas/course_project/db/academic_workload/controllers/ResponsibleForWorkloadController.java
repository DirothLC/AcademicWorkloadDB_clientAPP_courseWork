package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kz.kstu.kutsinas.course_project.db.academic_workload.dao.ResponsibleForWorkloadDAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.Executioner;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.ViewLoader;

import java.util.List;
import java.util.Map;

public class ResponsibleForWorkloadController  {
    @FXML
    private RadioButton viewButton;
    @FXML
    private RadioButton insertButton;
    @FXML
    private RadioButton updateButton;
    @FXML
    private RadioButton deleteButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TreeView<String> actionsList;

    @FXML
    private TableView tableView;

    private final ResponsibleForWorkloadDAO DAO= new ResponsibleForWorkloadDAO();

    public void initialize(){
        tableView.setEditable(true);

        TreeItem<String> tableRoot= new TreeItem<>("Доступные таблицы:");
        TreeItem<String> viewsRoot= new TreeItem<>("Доступные представления:");
        TreeItem<String> proceduresRoot= new TreeItem<>("Доступные хранимые процедуры:");

        TreeItem<String> typesOfAcademicWorkload= new TreeItem<>("Доступные виды нагрузки");
        TreeItem<String> teachers= new TreeItem<>("Список преподавателей кафедры");
        TreeItem<String> academicWorkload= new TreeItem<>("Нагрузка кафедры");
        TreeItem<String> discipline= new TreeItem<>("Дисциплины");
        TreeItem<String> academicGroup= new TreeItem<>("Группы кафедры");
        TreeItem<String> department= new TreeItem<>("Кафедры");
        TreeItem<String> jobTitle= new TreeItem<>("Должности");
        TreeItem<String> availableTypeOfWorkload = new TreeItem<>("Допустимые виды нагрузки");
        TreeItem<String> normative= new TreeItem<>("Норматив");

        tableRoot.getChildren().addAll(typesOfAcademicWorkload,teachers,academicWorkload,discipline,academicGroup,department,jobTitle,availableTypeOfWorkload,normative);
        tableRoot.setExpanded(true);

        TreeItem<String> teachersWorkloadByDepartment= new TreeItem<>("Нагрузка преподавателей кафедры");
        TreeItem<String> disciplineListOnSemesterOnGroupByDepartment= new TreeItem<>("Список дисциплин на семестр по группам кафедры");
        TreeItem<String> diplomaLeaderListByDepartment= new TreeItem<>("Список дипломных руководителей по кафедре");

        viewsRoot.getChildren().addAll(teachersWorkloadByDepartment,disciplineListOnSemesterOnGroupByDepartment,diplomaLeaderListByDepartment);

        TreeItem<String> halfRateTeachersByDepartment= new TreeItem<>("Преподаватели с половинной ставкой по кафедре");
        TreeItem<String> groupMoreThan20Poked= new TreeItem<>("Группы с количеством студентов более 20 по кафедре");

        proceduresRoot.getChildren().addAll(halfRateTeachersByDepartment,groupMoreThan20Poked);

        TreeItem<String> root= new TreeItem<>("Доступные действия:");
        root.getChildren().addAll(tableRoot,viewsRoot,proceduresRoot);
        root.setExpanded(true);

        actionsList.setRoot(root);
    }

    @FXML
    public void onActionSelected() {


        TreeItem<String> selectedItem = actionsList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        String selectedAction = selectedItem.getValue();
        System.out.println("Выбрано: " + selectedAction);
        String action = selectedAction;
        if(viewButton.isSelected());
        else if (updateButton.isSelected())action+="U";
        else if (insertButton.isSelected())action+="I";
        else if (deleteButton.isSelected())action+="D";

        UserSession sessionContext = UserSession.getInstance();
        int id = sessionContext.getUserId();
        int departmentId = sessionContext.getDepartmentId();

        String query = DAO.querySelector(action, departmentId);
        if(viewButton.isSelected()) {
            tableView.setEditable(true);
            List<Map<String, Object>> result = Executioner.executeQuery(query);
            updateTableView(result);
        }


    }

    @FXML
    public void onSelectButtonSelected(){
       // tableView.setEditable(false);

    }

    @FXML
    public void onInsertButtonSelected(){
        tableView.setEditable(true);

    }

    @FXML
    public void onUpdateButtonSelected(){
        tableView.setEditable(true);
    }

    @FXML
    public void onDeleteButtonSelected(){
        tableView.setEditable(true);
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

