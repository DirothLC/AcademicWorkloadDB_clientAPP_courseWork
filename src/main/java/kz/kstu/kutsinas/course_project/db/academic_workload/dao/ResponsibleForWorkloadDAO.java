package kz.kstu.kutsinas.course_project.db.academic_workload.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ResponsibleForWorkloadDAO {
    private Properties queries;

    public ResponsibleForWorkloadDAO() {
        queries = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("responsible_for_workload.queries.properties")) {
            if (input == null) {
                System.err.println("Не удалось найти файл свойств");
                return;
            }
            queries.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            System.out.println(queries);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки ресурса");
        }
    }


    public String querySelector(String selectedAction, int departmentId) {
        String query = queries.getProperty(selectedAction);
        System.out.println(selectedAction);
        if (query != null) {
            query = query.replace("{departmentId}", String.valueOf(departmentId));
            return query;
        } else {
            System.err.println("Запрос не найден");
            return null;
        }
    }
}




