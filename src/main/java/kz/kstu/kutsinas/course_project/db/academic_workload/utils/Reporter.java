package kz.kstu.kutsinas.course_project.db.academic_workload.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Reporter {
    public static void alertConfirmReporting(String title, String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void alertErrorReporting(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void exportTableViewToExcel(TableView<Map<String, Object>> tableView, Stage parentStage) {
        if (tableView.getItems().isEmpty()) {
            alertErrorReporting("Ошибка", "Нет данных для экспорта.");
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Экспорт");

        // Заголовки
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < tableView.getColumns().size(); col++) {
            String columnName = tableView.getColumns().get(col).getText();
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columnName);
        }

        // Данные
        for (int rowIdx = 0; rowIdx < tableView.getItems().size(); rowIdx++) {
            Row row = sheet.createRow(rowIdx + 1);
            Map<String, Object> rowData = tableView.getItems().get(rowIdx);

            for (int col = 0; col < tableView.getColumns().size(); col++) {
                String key = tableView.getColumns().get(col).getText();
                Object value = rowData.get(key);
                Cell cell = row.createCell(col);
                cell.setCellValue(value == null ? "" : value.toString());
            }
        }

        // Сохраняем файл
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить как Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("export.xlsx");

        try {
            var file = fileChooser.showSaveDialog(parentStage);
            if (file != null) {
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                alertConfirmReporting("Успех", "Файл успешно сохранён: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            alertErrorReporting("Ошибка", "Не удалось сохранить файл: " + e.getMessage());
        }
    }


    }


