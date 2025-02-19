package kz.kstu.kutsinas.course_project.db.academic_workload.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneLoader {

    public static void loadScene(String fxmlFile, Node node) {
        try {
            Parent root = FXMLLoader.load(SceneLoader.class.getResource("/kz/kstu/kutsinas/course_project/db/academic_workload/views/" + fxmlFile));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
