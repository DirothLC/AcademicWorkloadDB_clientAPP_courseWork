module kz.kstu.kutsinas.course_project.db.academic_workload.academicworkload_courseproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    requires com.microsoft.sqlserver.jdbc;
    requires java.sql;


    exports kz.kstu.kutsinas.course_project.db.academic_workload.runner;
    opens kz.kstu.kutsinas.course_project.db.academic_workload.runner to javafx.fxml;
    exports kz.kstu.kutsinas.course_project.db.academic_workload.controllers;
    opens kz.kstu.kutsinas.course_project.db.academic_workload.controllers to javafx.fxml;
}