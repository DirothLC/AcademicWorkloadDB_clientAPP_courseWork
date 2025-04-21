package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kz.kstu.kutsinas.course_project.db.academic_workload.dao.AdministratorDAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.ViewLoader;

import java.util.List;
import java.util.Map;

public class AdministratorController {
    @FXML
    private TableView auditTable;

    protected final AdministratorDAO DAO = new AdministratorDAO();

    public void initialize(){
        List<Map<String, Object>> auditData = DAO.getAuditLogs();
        loadDataToTableView(auditTable, auditData);
    }

    @FXML
    private void onCreateUserButtonClick(){
    ViewLoader.loadStage("administrator_functions_views/backup-view.fxml","Управление пользователями");
    }

    @FXML
    private void onBackupButtonClick(){
    ViewLoader.loadStage("administrator_functions_views/backup-view.fxml","Резервное копирование");
    }

    @FXML
    private void onLogoutButtonClick(){
        ViewLoader.loadStage("logout-view.fxml","Подтверждение выхода");
    }

    protected void loadDataToTableView(TableView<Map<String, Object>> tableView, List<Map<String, Object>> data) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        if (data == null || data.isEmpty()) return;

        Map<String, Object> firstRow = data.get(0);

        for (String key : firstRow.keySet()) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
            column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(key)));
            column.setPrefWidth(150);
            tableView.getColumns().add(column);
        }

        tableView.getItems().addAll(data);
    }
}
