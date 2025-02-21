package kz.kstu.kutsinas.course_project.db.academic_workload.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewLoader {

    public static void loadScene(String fxmlFile, Node node) {
        try {
            Parent root = FXMLLoader.load(ViewLoader.class.getResource("/kz/kstu/kutsinas/course_project/db/academic_workload/views/" + fxmlFile));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось загрузить сцену");
        }
    }

    public static void loadStage(String fxmlFile, String title){
        try {
        FXMLLoader fxmlLoader = new FXMLLoader((ViewLoader.class.getResource("/kz/kstu/kutsinas/course_project/db/academic_workload/views/" + fxmlFile)));
        Parent root = fxmlLoader.load();

        Stage stage=new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Не удалось загрузить окно");
        }
    }
}
