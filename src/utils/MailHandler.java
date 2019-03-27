package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MailHandler {

	private String email;
	private String host;
	private String name;
	private String password;
	private int port;


	public MailHandler() throws IOException {
		loadSettings();
	}


	private void loadSettings() throws IOException {
		List<String> lines = ResourceUtils.getLines("mail.txt");
		if (lines.size() < 5) throw new IOException("Invalid mail info");

		host = lines.get(0);
		port = Integer.parseInt(lines.get(1));
		email = lines.get(2);
		name = lines.get(3);
		password = lines.get(4);
	}


	public int sendEmail(String toEmail, String subject, String body) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", host); //SMTP Host
			props.put("mail.smtp.port", Integer.toString(port)); //TLS Port
			props.put("mail.smtp.auth", "true"); //enable authentication
			props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

			//create Authenticator object to pass in Session.getInstance argument
			Authenticator auth = new Authenticator() {
				//override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(email, password);
				}
			};
			Session session = Session.getInstance(props, auth);
			MimeMessage msg = new MimeMessage(session);
			//set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress(email, name));

			msg.setReplyTo(InternetAddress.parse(email, false));

			msg.setSubject(subject, "UTF-8");

			msg.setText(body, "UTF-8");

			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			Transport.send(msg);

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}
}