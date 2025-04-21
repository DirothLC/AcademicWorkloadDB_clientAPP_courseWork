package kz.kstu.kutsinas.course_project.db.academic_workload.controllers.admin_functions_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kz.kstu.kutsinas.course_project.db.academic_workload.controllers.AdministratorController;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;

import java.io.File;

public class BackupController extends AdministratorController {

    @FXML
    private TextField folderField;
    @FXML
    private TextField titleField;

    @FXML
    private Button restoreButton;
    @FXML
    private Button selectFolder;


    @FXML
    private void onBackupButtonClick(){
        if(folderField.getText().isEmpty()||titleField.getText().isEmpty()){
            Reporter.alertErrorReporting("Ошибка","Заполните все поля");
            return;
        }
        String folderPath = folderField.getText();
        String fileName = titleField.getText();
        DAO.createDatabaseBackup(folderPath, fileName);


    }

    @FXML
    private void onRestoreButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл резервной копии");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Backup files (*.bak)", "*.bak");

        Stage stage = (Stage) restoreButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            folderField.setText(selectedFile.getAbsolutePath());
            DAO.restoreDatabaseFromBackup(selectedFile);
        } else {
            Reporter.alertConfirmReporting("Отмена", "Выбор файла отменен");
        }



    }

    @FXML
    private void onSelectFolderClicked() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для сохранения");

        Stage stage = (Stage) selectFolder.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            folderField.setText(selectedDirectory.getAbsolutePath());
        } else {
            Reporter.alertConfirmReporting("Отмена", "Выбор папки отменен");
        }
    }

    @Override
    public void initialize(){}

}
