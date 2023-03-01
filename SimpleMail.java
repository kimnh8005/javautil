package md.util;
/* set CLASSPATH=%CLASSPATH%;activation.jar;mail.jar */

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
 
public class SimpleMail
{
	static String  d_email = "dibcheck@andwise.com";
	static String d_password = "07070900595";
	static String d_host = "smtp.gmail.com";
	static String d_port  = "465";
	static String m_subject = " [딥체커 - 웹 접근성 검사]가 완료되었습니다.";
	static String m_text_header = "아래의 링크를 따라 가시면 결과를 보실 수 있습니다.\n\n";
	static String m_text_footer = "\n\n최고의 퀄리티로 서비스하는 딥체커가 되겠습니다.";
    public synchronized static boolean sendMail(String title, String[] to, String key){
    	
    	boolean debug = false;
    	
        Properties props = new Properties();
	    props.put("mail.smtp.user", d_email);
	    props.put("mail.smtp.host", d_host);
	    
	    if(!"".equals(d_port))
	    	props.put("mail.smtp.port", d_port);
	    
	    if(!"".equals("true"))
	    	props.put("mail.smtp.starttls.enable","true");
	    
	    props.put("mail.smtp.auth", "true");
	    
	    if(debug){
	    	props.put("mail.smtp.debug", "true");
	    }else{
	    	props.put("mail.smtp.debug", "false");         
	    }
	    
	    if(!"".equals(d_port))
	    	props.put("mail.smtp.socketFactory.port", d_port);
	    
	    if(!"".equals("javax.net.ssl.SSLSocketFactory"))
	    	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
	    if(!"".equals("false"))
	    	props.put("mail.smtp.socketFactory.fallback", "false");
	 
        try
        {
			Session session = Session.getDefaultInstance(props, null);
			
			session.setDebug(debug);
			
			MimeMessage msg = new MimeMessage(session);
			
			msg.setText(m_text_header + key + m_text_footer);
			msg.setSubject(title + m_subject);
			msg.setFrom(new InternetAddress("support@dbdib.com"));
			
			if(to[0] != null && to[0] != "undefined" && to[0] != ""){
				for(int i=0;i<to.length;i++)
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
			}
			msg.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect(d_host, d_email, d_password);
			transport.sendMessage(msg, msg.getAllRecipients());                
			transport.close();
			return true;
	    }
	    catch (Exception mex)
	    {
	        mex.printStackTrace();
            return false;
	    }
    } 
    public static void main(String[] args)
    {
               //String[] to={"dibcheck@andwise.com"};
         //       String[] cc={"kj@dbdib.com"};
         //       String[] bcc={"kj@dbdib.com"};
                //This is for google
                //Test.sendMail("venkatesh@dfdf.com","password","smtp.gmail.com","465","true",
              	//		"true",true,"javax.net.ssl.SSLSocketFactory","false",to,cc,bcc,
              	//		"hi baba don't send virus mails..","This is my style...of reply..If u send virus mails..");
                //SimpleMail.sendMail(d_email, d_password, d_host, d_port, "true", "true", true, "javax.net.ssl.SSLSocketFactory", "false", to, null, null, m_subject, m_text_header + "http://www.dbdib.com/DibChecker/result/site_result.jsp?_key=A3E91382FC085FA39D41432D7E166D7D" + m_text_footer);
               //SimpleMail.sendMail(to,"http://www.dbdib.com/DibChecker/result/site_result.jsp?_key=A3E91382FC085FA39D41432D7E166D7D");
               //String to
               String[] to = {"abc@abc.com"};
               String[] toname = {"받는사람"};
               SimpleMail.sendMail("utf-8","noteampweb", "shxmdoavm", 
            		   "gmail-smtp.l.google.com", "25", "true", "true", true, "", "",  
            		   to , toname, "noteampweb@gamil.com", "노트앰프", "제목", "내용", null);
               
               
               //SimpleMail.sendMail("euc-kr","mailmaster@gongdan.go.kr", "dosi386", "61.42.73.70", "25", "", "", true, "", "",  to , toname, "mail.gongdan.go.kr", "성북공단", "테스트 메일","", "테스트 입니다");
    }
 
    public synchronized static boolean sendMail(String charset, String userName, String passWord, String host, String port, 
    		String starttls, String auth, boolean debug, String socketFactoryClass, String fallback, 
    		String[] toAddr, String[] toName, String fromAddr, String fromName, String subject,String text, String html){
    	
        Properties props = new Properties();
        
	    if(!"".equals(starttls))
	    	props.put("mail.smtp.starttls.enable",starttls);
	    
	    props.put("mail.smtp.user", userName);
	    props.put("mail.smtp.host", host);
	    
	    if(!"".equals(port))
	    	props.put("mail.smtp.port", port);
	    
	    
	    props.put("mail.smtp.auth", auth);
	    if(debug){
	    	props.put("mail.smtp.debug", "true");
	    }else{
	    	props.put("mail.smtp.debug", "false");         
	    }
	    
	    if(!"".equals(port))
	    	props.put("mail.smtp.socketFactory.port", port);
	    
	    if(!"".equals(socketFactoryClass))
	    	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	    
	    if(!"".equals(fallback))
	    	props.put("mail.smtp.socketFactory.fallback", fallback);
	 
        try
        {
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(debug);
			MimeMessage msg = new MimeMessage(session);
			msg.setHeader("Content-Type","text/html; charset="+charset);
			msg.setSubject(subject);
			
			
			msg.setFrom(new InternetAddress(fromAddr,fromName));
			for(int i=0;i<toAddr.length;i++){
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr[i], toName[i]));
			}
			
			if(text != null && !text.equals("")) {
				msg.setText(text);
			} else {

				MimeMultipart multipart = new MimeMultipart("related");

		        BodyPart messageBodyPart = new MimeBodyPart();
		        messageBodyPart.setContent(html, "text/html; charset="+charset);
		        multipart.addBodyPart(messageBodyPart);
		        
				msg.setContent(multipart);
			}
			
			msg.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect(host, userName, passWord);
			transport.sendMessage(msg, msg.getAllRecipients());
			
			transport.close();
			return true;
	    }
	    catch (Exception mex)
	    {
	        mex.printStackTrace();
            return false;
	    }
    }
    
}
