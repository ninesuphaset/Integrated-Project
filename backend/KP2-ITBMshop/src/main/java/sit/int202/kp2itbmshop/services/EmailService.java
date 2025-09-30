package sit.int202.kp2itbmshop.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    // only link not onlyfans
//    public void sendEmail(String to, String link) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Verify your email for ITB MShop");
//        message.setText("Click the link to verify your email: \n" + link);
//        mailSender.send(message);
//    }

    public void sendEmail(String to, String link) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = """
            <html>
              <body style="font-family: Arial, sans-serif; background:#f6f9fc; padding:40px; text-align:center;">
                <table align="center" width="600" cellpadding="0" cellspacing="0"
                       style="background:#fff; border-radius:12px; padding:40px; box-shadow:0 6px 18px rgba(0,0,0,0.1);">
                  <tr>
                    <td align="center" style="padding-bottom:20px;">
                      <div style="font-size:24px; font-weight:bold; color:#0171E3; margin-top:14px;">ITB MShop KP2</div>
                      <hr style="width:60px; border:none; border-top:3px solid #0171E3; margin:20px auto;" />
                    </td>
                  </tr>
                  <tr>
                    <td align="center">
                      <p style="font-size:16px; color:#555; margin:0 0 25px; line-height:1.8;">
                        Thank you for registering. <br>
                        Please verify your email to activate your account.
                      </p>
            
                      <a href='%s' style="display:inline-block; padding:14px 32px; background:#0171E3; color:#fff;
                          font-weight:bold; text-decoration:none; border-radius:8px; font-size:16px; letter-spacing:0.5px;
                          box-shadow:0 4px 10px rgba(1,113,227,0.3);">
                        Verify My Account
                      </a>
            
                      <p style="margin-top:30px; font-size:13px; color:#666; line-height:1.5;">
                        If the button above doesn’t work, copy and paste this link into your browser:
                      </p>
                      <p style="font-size:13px; word-break:break-all; margin:0;">
                        <a href='%s' style="color:#0171E3; text-decoration:none;">%s</a>
                      </p>
            
                      <hr style="margin:35px 0; border:none; border-top:1px solid #eee;" />
            
                      <p style="font-size:12px; color:#999; line-height:1.6;">
                        This verification link will expire in <b>24 hours</b>.<br>
                        If you did not create an account, you can safely ignore this email.
                      </p>
                    </td>
                  </tr>
                </table>
              </body>
            </html>
            """.formatted(link, link, link);


            helper.setTo(to);
            helper.setSubject("Verify your email for ITB MShop");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
