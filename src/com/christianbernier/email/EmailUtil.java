package com.christianbernier.email;

import java.util.Date;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	public static void sendEmail(Session session, String toEmail, String subject, String body, int num, String nextSunday, String service){
		try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");
	      msg.setFrom(new InternetAddress("YOUR_EMAIL_ADDRESS_HERE", "YOUR_NAME_HERE"));

	      msg.setReplyTo(InternetAddress.parse("YOUR_REPLY_TO_EMAIL_HERE", false));

	      msg.setSubject(subject, "UTF-8");

	      msg.setContent(body, "text/html");

	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, "TO_FIELD");
	      msg.setRecipients(Message.RecipientType.CC, "CC_FIELD");
	      msg.setRecipients(Message.RecipientType.BCC, "BCC_FIELD");
	      System.out.println("Sending " + service + " email for " + nextSunday + "...");
    	  Transport.send(msg);  

	      System.out.println("Sent email for " + service + "!");
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}
}