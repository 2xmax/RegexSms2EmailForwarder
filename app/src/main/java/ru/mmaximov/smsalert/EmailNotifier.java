package ru.mmaximov.smsalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import ru.mmaximov.smsalert.biz.Message;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Properties;

class EmailNotifier {
    private final Context ctx;

    public EmailNotifier(Context ctx) {
        this.ctx = ctx;
    }

    private static String substituteTemplate(String template, Message msg) {
        return template.replace("{phone}", msg.getPhone()).replace("{text}", msg.getText());
    }

    public void notify(Message msg) {
        Resources resources = ctx.getResources();
        sendEmail(substituteTemplate(resources.getString(R.string.email_sender), msg),
                substituteTemplate(resources.getString(R.string.email_subject), msg),
                substituteTemplate(resources.getString(R.string.email_body), msg));
    }

    private void sendEmail(String senderName, String subject, String body) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        String to = prefs.getString("email_to", null);
        final String username = prefs.getString("smtp_username", null);
        final String password = prefs.getString("smtp_password", null);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", prefs.getString("smtp_server", null).toLowerCase(Locale.US));
        props.put("mail.smtp.host", prefs.getString("smtp_server", null));
        props.put("mail.smtp.port", prefs.getString("smtp_port", null));
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username, senderName));
            msg.addRecipient(MimeMessage.RecipientType.TO,
                    new InternetAddress(to, to));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
        } catch (Exception e) {
            Log.e("email sender", e.toString());
        }
    }
}
