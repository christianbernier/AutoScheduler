	/*
	 * AutoScheduler
	 * Created by Christian Bernier
	 * Written for Grace Chapel LEXSM HSM and MSM Services
	 * 
	 * If you have any issues, please contact Christian
	 * 
	 * */

package com.christianbernier.email;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONException;

public class TLSEmail {
	
	static final Random rand = new Random();

	/**
	   Outgoing Mail (SMTP) Server
	   requires TLS or SSL: smtp.gmail.com (use authentication)
	   Use Authentication: Yes
	   Port for TLS/STARTTLS: 587
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws IOException, JSONException {
		String path = "creds.config";
		File file = new File(path);
		Scanner contents = null;
		try {
			contents = new Scanner(file);
		} catch(FileNotFoundException e) {
			System.exit(-1);
		}
		
		String u = contents.nextLine().substring(10);
		String p = contents.nextLine().substring(10);
		final String fE = u;
		final String fP = p;
		
		
		String[] HSM_Emails = {
				//Email List 1
		};
		
		String[] MSM_Emails = {
				//Email List 2
			};
		
		Date today = new Date();
		Date nSun = calcNextSunday(today);
		
		String nextSunday = Integer.toString(nSun.getMonth() + 1) + "/" + Integer.toString(nSun.getDate());
		
		String HSM_urlToFetch = "YOUR_SPREADSHEET_JSON_FILE_HERE_1";
    	HashMap<String, String> HSM_roles = Gson.fetchPeopleForDate(HSM_urlToFetch, "", nextSunday);
    	
    	String MSM_urlToFetch = "YOUR_SPREADSHEET_JSON_FILE_HERE_2";
    	HashMap<String, String> MSM_roles = Gson.fetchPeopleForDate(MSM_urlToFetch, "", nextSunday);
		
    	//For each role, make a line like such below
    	
		String HSM_TechDirector	= HSM_roles.get("D");	//Director
		String HSM_ProPresenter	= HSM_roles.get("PP/S");//ProPresenter/Spotify
		String HSM_AudioLights	= HSM_roles.get("A/L");	//Audio/Lights
		String HSM_Camera		= HSM_roles.get("V");	//Video
		
		String MSM_TechDirector	= MSM_roles.get("D");	//Director
		String MSM_ProPresenter	= MSM_roles.get("PP/S");//ProPresenter/Spotify
		String MSM_AudioLights	= MSM_roles.get("A/L");	//Audio/Lights
		String MSM_Alternate	= MSM_roles.get("HSM");	//High School
		String MSM_SanctuaryCam;
		if(MSM_roles.get("SC") != null) {
			MSM_SanctuaryCam = MSM_roles.get("SC");	//Scantuary Camera
		}
		if(MSM_roles.get("S") != null) {
			MSM_SanctuaryCam = MSM_roles.get("S"); //Scantuary Camera
		} else {
			MSM_SanctuaryCam = null;
		}
		
		String HSM_Message = generateHSMMessage(HSM_TechDirector, HSM_ProPresenter, HSM_AudioLights, HSM_Camera);
		String MSM_Message = generateMSMMessage(MSM_TechDirector, MSM_ProPresenter, MSM_AudioLights, MSM_Alternate, MSM_SanctuaryCam);
		
		final String HSM_toField = generateTo(HSM_Emails);
		final String MSM_toField = generateTo(MSM_Emails);
		
		System.out.println("Starting program...");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
		props.put("mail.smtp.port", "587"); //TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
		
                //create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fE, fP);
			}
		};
		Session session = Session.getInstance(props, auth);
		
		
		
		EmailUtil.sendEmail(session, HSM_toField, "HSM Schedule for " + nextSunday, HSM_Message, 1, nextSunday, "HSM");
		EmailUtil.sendEmail(session, MSM_toField, "MSM Schedule for " + nextSunday, MSM_Message, 1, nextSunday, "MSM");
		
	}
	public static String generateTo(String[] emails) {
		int len = 0;
		String ret = "";
		for(String e : emails) {
			if(!e.isEmpty()) {
				len++;
			}
		}
		for(int i = 0; i < len - 1; i++) {
			ret += emails[i] + ", ";
		}
		ret += emails[len - 1];
		
		return ret;
	}
	public static String generateMessage(int s) {
		String msg = "";
		for(int i = 0; i < s; i++) {
			msg += String.valueOf(rand.nextLong());
		}
		return msg;
	}
	public static String generateHSMMessage(String d, String p, String a, String c) {
		Date today = new Date();
		Date d2P = DateUtils.addDays(today, 2);
		Date nSun = calcNextSunday(today);
		
		String nextSundayMonth 		= Integer.toString(nSun.getMonth() + 1);
		String nextSundayDay		= Integer.toString(nSun.getDate());
		String nextSundayYear		= Integer.toString(nSun.getYear() + 1900);
		String nextSundayString		= generateFullDateString(nextSundayMonth, nextSundayDay, nextSundayYear);
		String thisWednesdayMonth	= Integer.toString(d2P.getMonth() + 1);
		String thisWednesdayDay		= Integer.toString(d2P.getDate());
		
		String dayOfWeekInTwoDays	= intDayToStringDay(d2P.getDay());
		
		String msgOpening = "Hi everyone,<br>Below is the current schedule for next Sunday (" + nextSundayMonth + "/" + nextSundayDay + "). Please let me know as soon as possible if you aren't coming this upcoming Sunday. <strong>Please respond to this email if you're scheduled by " + dayOfWeekInTwoDays + " (" + thisWednesdayMonth + "/" + thisWednesdayDay + ") so I know you got it.</strong> Please try to arrive at the booth 5 minutes before the beginning of service for last minute changes.";
		
		String msgSchedule = "<br><br><strong>HSM - " + nextSundayString + "</strong>"
				+ (d == null ? "" : ("<br><i>Tech Director</i>: " + d))
				+ (p == null ? "" : ("<br><i>ProPresenter/Spotify</i>: " + p)) 
				+ (a == null ? "" : ("<br><i>Audio/Lights</i>: " + a)) 
				+ (c == null ? "" : ("<br><i>Camera Operator</i>: " + c));
		
		String msgClosing = "<br><br>Thank you for serving,<br>Auto-Scheduling Bot<br><br>-------<br>This message was sent automatically. If you believe there is a mistake, please contact Christian.";
		
		return msgOpening + msgSchedule + msgClosing;
		
	}
	
	public static String generateMSMMessage(String d, String p, String a, String alt, String cam) {
		Date today = new Date();
		Date d2P = DateUtils.addDays(today, 2);
		Date nSun = calcNextSunday(today);
		
		String nextSundayMonth 		= Integer.toString(nSun.getMonth() + 1);
		String nextSundayDay		= Integer.toString(nSun.getDate());
		String nextSundayYear		= Integer.toString(nSun.getYear() + 1900);
		String nextSundayString		= generateFullDateString(nextSundayMonth, nextSundayDay, nextSundayYear);
		String thisWednesdayMonth	= Integer.toString(d2P.getMonth() + 1);
		String thisWednesdayDay		= Integer.toString(d2P.getDate());
		
		String dayOfWeekInTwoDays	= intDayToStringDay(d2P.getDay());
		
		String msgOpening = "Hi everyone,<br>Below is the current schedule for next Sunday (" + nextSundayMonth + "/" + nextSundayDay + "). Please let me know as soon as possible if you aren't coming this upcoming Sunday. <strong>Please respond to this email if you're scheduled by " + dayOfWeekInTwoDays + " (" + thisWednesdayMonth + "/" + thisWednesdayDay + ") so I know you got it.</strong> Please try to arrive at the booth 5 minutes before the beginning of service for last minute changes.";
		
		String msgSchedule = "<br><br><strong>MSM - " + nextSundayString + "</strong>"
				+ (d == null ? "" : ("<br><i>Tech Director</i>: " + d))
				+ (p == null ? "" : ("<br><i>ProPresenter/Spotify</i>: " + p)) 
				+ (a == null ? "" : ("<br><i>Audio/Lights</i>: " + a))
				+ (alt == null ? "" : ("<br><i>HSM</i>: " + alt))
				+ (cam == null ? "" : ("<br><i>Sanctuary Camera</i>: " + cam));
		
		String msgClosing = "<br><br>Thank you for serving,<br>Auto-Scheduling Bot<br><br>-------<br>This message was sent automatically. If you believe there is a mistake, please contact Christian.";
		
		return msgOpening + msgSchedule + msgClosing;
	}
	public static String intDayToStringDay(int d) {
		switch(d) {
			case 0: return "Sunday"; 
			case 1: return "Monday";
			case 2: return "Tuesday";
			case 3: return "Wednesday";
			case 4: return "Thursday";
			case 5: return "Friday";
			case 6: return "Saturday";
			default: throw new Error("Invalid date");
		}
	}
	public static Date calcNextSunday(Date d) {
		while(d.getDay() != 0) {
			d = DateUtils.addDays(d,  1);
		}
		
		return d;
	}
	public static String generateFullDateString(String m, String d, String y) {
		int month = Integer.parseInt(m);
		int day = Integer.parseInt(d);
		int year = Integer.parseInt(y);
		String ending = "";
		String monthStr = "";
		switch(day % 10) {
			case 1: ending = "st"; break;
			case 2: ending = "nd"; break;
			case 3: ending = "rd"; break;
			default: ending = "th";
		}
		if(day == 11 || day == 12) {
			ending = "th";
		}
		switch(month){
			case 1: monthStr = "January"; break;
			case 2: monthStr = "February"; break;
			case 3: monthStr = "March"; break;
			case 4: monthStr = "April"; break;
			case 5: monthStr = "May"; break;
			case 6: monthStr = "June"; break;
			case 7: monthStr = "July"; break;
			case 8: monthStr = "August"; break;
			case 9: monthStr = "September"; break;
			case 10: monthStr = "October"; break;
			case 11: monthStr = "November"; break;
			case 12: monthStr = "December";
		}
		return monthStr + " " + day + ending + ", " + year;
	}
}