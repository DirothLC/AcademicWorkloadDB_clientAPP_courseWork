package kz.kstu.kutsinas.course_project.db.academic_workload.dto;

public class UserResponse {

        private String login;
        private String role;
        private Integer departmentId;
        private Integer userId; // если хочешь добавить в JSON ответ

        public String getLogin() { return login; }
        public String getRole() { return role; }
        public Integer getDepartmentId() { return departmentId; }
        public Integer getUserId() { return userId; }

        public void setLogin(String login) { this.login = login; }
        public void setRole(String role) { this.role = role; }
        public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
        public void setUserId(Integer userId) { this.userId = userId; }
}

