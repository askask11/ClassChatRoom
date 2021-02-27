package com.classchatroom.model;

/*
 * Java Mailer
 * by Seung-Gu Lee
 * Use this class to send emails to the user. Call the constructor to do so.
 * Make sure Java Mail library API is set.
 */

import cn.hutool.setting.Setting;
import static com.classchatroom.model.OSSAccessor.logError;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;





public class JavaMailer 
{
    public JavaMailer(String email, String subject, String content)
    {
        // Declarations
        String from = "classchat0@gmail.com";
        String fromName = "ClassChat";
        String to = email;

        // Sending email account info
        // Please do not try to log in with this password!
        // FIXED by Jianqing: Please load the setting file into default package (main/java) to retrieve cridentials.
        Setting s = new Setting("mail.setting");
        String smtpUsername = s.get("user");
        String smtpPassword = s.get("pass");
        String host = "smtp.gmail.com";
        int port = 587;

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", port); 
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.imap.ssl.enable", "true"); // required for Gmail
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
//        props.put("mail.debug", "true");
        
       

        Session mailSession = Session.getDefaultInstance(props);

        // Send email through SMTP
        try {
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, fromName));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setContent(content, "text/html;");

            Transport transport = mailSession.getTransport();

            try {
                System.out.println("Sending email: \"" + subject + "\" to " + email + "...");
                transport.connect(host, smtpUsername, smtpPassword);
                transport.sendMessage(msg, msg.getAllRecipients());
                System.out.println("Email sent! If you didn't receive it, please check your spam box.");
            }
            catch(MessagingException me)
            {
                System.out.println("Mail sending failed, MessagingException Line 67");
                me.printStackTrace();
                logError(me);
            }
            finally 
            {
                transport.close();
            }
        }
        catch(MessagingException me)
        {
            System.out.println("Mail sending failed, MessagingException Line 77");
            me.printStackTrace();
            logError(me);
        }
        catch(Exception ex)
        {
            System.out.println("Mail sending failed, Exception Line 82");
            ex.printStackTrace();
            logError(ex);
        }
    }
}
