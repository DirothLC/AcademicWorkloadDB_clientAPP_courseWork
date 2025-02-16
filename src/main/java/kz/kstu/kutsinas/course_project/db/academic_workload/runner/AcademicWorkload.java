package kz.kstu.kutsinas.course_project.db.academic_workload.runner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AcademicWorkload extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AcademicWorkload.class.getResource("/kz/kstu/kutsinas/course_project/db/academic_workload/views/authorization-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Academic Workload");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}