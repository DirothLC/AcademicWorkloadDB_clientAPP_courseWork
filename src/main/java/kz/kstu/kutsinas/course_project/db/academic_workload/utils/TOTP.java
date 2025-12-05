package kz.kstu.kutsinas.course_project.db.academic_workload.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;

public class TOTP {
    private static final String HMAC_ALGO = "HmacSHA1";
    private static final int SECRET_SIZE = 20; // bytes
    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    public static String generateSecretBase32() {
        byte[] bytes = new byte[SECRET_SIZE];
        new SecureRandom().nextBytes(bytes);
        return base32Encode(bytes);
    }

    public static String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int currByte, digit;
        int i = 0, index = 0;
        int nextByte;
        while (i < data.length) {
            currByte = data[i] & 0xff;
            if (index > 3) {
                if ((i + 1) < data.length) {
                    nextByte = data[i + 1] & 0xff;
                } else {
                    nextByte = 0;
                }

                digit = currByte & (0xff >> index);
                index = (index + 5) % 8;
                digit = (digit << index) | (nextByte >> (8 - index));
                i++;
            } else {
                digit = (currByte >> (8 - (index + 5))) & 0x1f;
                index = (index + 5) % 8;
                if (index == 0) i++;
            }
            sb.append(BASE32_CHARS.charAt(digit));
        }
        return sb.toString();
    }

    public static byte[] base32Decode(String base32) {
        base32 = base32.trim().replace("=", "").toUpperCase(Locale.US);
        int size = base32.length();
        int buffer = 0;
        int bitsLeft = 0;
        byte[] result = new byte[size * 5 / 8];
        int index = 0;
        for (char c : base32.toCharArray()) {
            int val = BASE32_CHARS.indexOf(c);
            if (val < 0) continue;
            buffer <<= 5;
            buffer |= val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                result[index++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }
        if (index == result.length) return result;
        byte[] out = new byte[index];
        System.arraycopy(result, 0, out, 0, index);
        return out;
    }

    public static String getOtp(String base32Secret, long forTimeSeconds) {
        byte[] key = base32Decode(base32Secret);
        long t = forTimeSeconds / TIME_STEP_SECONDS;
        return generateTOTP(key, t, CODE_DIGITS);
    }

    private static String generateTOTP(byte[] key, long t, int digits) {
        try {
            byte[] data = new byte[8];
            long value = t;
            for (int i = 8; i-- > 0; value >>>= 8) {
                data[i] = (byte) value;
            }

            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(key, HMAC_ALGO));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xf;
            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            int otp = binary % (int) Math.pow(10, digits);
            return String.format("%0" + digits + "d", otp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Проверка кода с "окном" ±1 шага (30с по дефолту).
     */
    public static boolean verifyCode(String base32Secret, String code) {
        long now = Instant.now().getEpochSecond();
        for (int i = -1; i <= 1; i++) {
            String candidate = getOtp(base32Secret, now + i * TIME_STEP_SECONDS);
            if (candidate.equals(code)) return true;
        }
        return false;
    }

    /**
     * Создаёт otpauth:// URL для отображения в QR
     */
    public static String getOtpAuthURL(String issuer, String accountName, String secretBase32) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                urlEncode(issuer), urlEncode(accountName), secretBase32, urlEncode(issuer));
    }

    private static String urlEncode(String s) {
        return s.replace(" ", "%20").replace("@", "%40");
    }

}
