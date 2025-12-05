package kz.kstu.kutsinas.course_project.db.academic_workload.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import kz.kstu.kutsinas.course_project.db.academic_workload.dao.TwoFADAO;
import kz.kstu.kutsinas.course_project.db.academic_workload.service.UserSession;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.Reporter;
import kz.kstu.kutsinas.course_project.db.academic_workload.utils.TOTP;


import java.awt.image.BufferedImage;


public class TwoFAController {
    @FXML
    private ImageView qrCode;
    @FXML
    private TextField otpField;
    @FXML
    private Label secretLabel;

    private final TwoFADAO dao = new TwoFADAO();



    @FXML
    private void onActivButtonClick() {
        Integer userId = UserSession.getInstance().getUserId();
        if (userId == null) {
            Reporter.alertErrorReporting("Ошибка", "Пользователь не найден в сессии.");
            return;
        }

        if (dao.isEnabled(userId)) {
            Reporter.alertConfirmReporting("Инфо", "2FA уже активирована для этого пользователя.");
            return;
        }

        String secret = TOTP.generateSecretBase32();
        String saved = dao.createOrGetSecret(userId, secret);
        if (saved == null) {
            Reporter.alertErrorReporting("Ошибка", "Не удалось создать запись 2FA в базе.");
            return;
        }

        String usedSecret = saved;

        secretLabel.setText("SECRET: " + usedSecret);

        String issuer = "AcademicWorkload";
        String account = UserSession.getInstance().getUsername();
        String otpAuthUrl = TOTP.getOtpAuthURL(issuer, account, usedSecret);

        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage bf = MatrixToImageWriter.toBufferedImage(bitMatrix);
            Image fxImage = SwingFXUtils.toFXImage(bf, null);
            qrCode.setImage(fxImage);
        } catch (WriterException e) {
            e.printStackTrace();
            Reporter.alertErrorReporting("Ошибка", "Не удалось сгенерировать QR. Скопируйте секрет вручную.");
        }

        Reporter.alertConfirmReporting("Готово", "Сканируйте QR в Google Authenticator и введите код ниже для подтверждения активации.");
    }

    @FXML
    private void onDeactiveButtonClick() {
        Integer userId = UserSession.getInstance().getUserId();
        if (userId == null) {
            Reporter.alertErrorReporting("Ошибка", "Пользователь не найден в сессии.");
            return;
        }

        boolean disabled = dao.disable2FA(userId);
        if (disabled) {
            Reporter.alertConfirmReporting("Готово", "2FA деактивирован.");
            qrCode.setImage(null);
            if (secretLabel != null) secretLabel.setText("");
        } else {
            Reporter.alertErrorReporting("Ошибка", "Не удалось деактивировать 2FA.");
        }
    }

    @FXML
    private void onEnterButtonClick() {
        Integer userId = UserSession.getInstance().getUserId();
        if (userId == null) {
            Reporter.alertErrorReporting("Ошибка", "Пользователь не найден в сессии.");
            return;
        }
        String code = otpField.getText().trim();
        if (code.isEmpty()) {
            Reporter.alertErrorReporting("Ошибка", "Введите код.");
            return;
        }

        String secret = dao.getSecretForUser(userId);
        if (secret == null) {
            Reporter.alertErrorReporting("Ошибка", "Секрет для пользователя не найден.");
            return;
        }

        boolean ok = TOTP.verifyCode(secret, code);
        if (ok) {
            boolean enabled = dao.enable2FA(userId);
            if (enabled) {
                Reporter.alertConfirmReporting("Успех", "2FA успешно активирован.");
            } else {
                Reporter.alertErrorReporting("Ошибка", "Ошибка при включении 2FA в базе.");
            }
        } else {
            Reporter.alertErrorReporting("Ошибка", "Неверный код. Попробуйте снова.");
        }
    }
}
