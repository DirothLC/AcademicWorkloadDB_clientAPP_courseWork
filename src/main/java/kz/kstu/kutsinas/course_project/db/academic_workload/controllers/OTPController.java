package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kz.kstu.kutsinas.course_project.db.academic_workload.dao.TwoFADAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Logger;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.TOTP;

public class OTPController {
    @FXML
    private TextField otpField;

    private Runnable onSuccess;

    public void setCallback(Runnable callback) {
        this.onSuccess = callback;
    }

    @FXML
    private void onConfirmButtonClick() {
        String otp = otpField.getText().trim();

        if (otp.length() != 6) {
            Reporter.alertErrorReporting("Ошибка", "Введите 6-значный код");
            return;
        }

        int userId = UserSession.getInstance().getUserId();
        TwoFADAO dao = new TwoFADAO();
        String secret = dao.getSecret(userId);

        if (secret == null) {
            Reporter.alertErrorReporting("Ошибка", "2FA не настроена");
            return;
        }

        boolean ok = TOTP.verifyCode(secret, otp);

        if (!ok) {
            Reporter.alertErrorReporting("Ошибка", "Неверный код");
            Logger.warn("IncorrectOTP",UserSession.getInstance().getUsername(), "OTPController");
            return;
        }

        Stage stage = (Stage) otpField.getScene().getWindow();
        stage.close();

        if (onSuccess != null)
            onSuccess.run();
    }
}
