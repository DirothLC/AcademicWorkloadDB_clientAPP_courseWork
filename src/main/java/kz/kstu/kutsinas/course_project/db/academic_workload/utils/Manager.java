package kz.kstu.kutsinas.course_project.db.academic_workload.utils;

import javafx.stage.Stage;
import javafx.stage.Window;

public class Manager {
    public static void closeAllWindows() {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                ((Stage) window).close();
            }
        }
    }
}
