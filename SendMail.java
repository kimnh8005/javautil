package md.util;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;

public class SendMail {
  /**
 * @uml.property  name="mailHost"
 */
private String mailHost;
  /**
 * @uml.property  name="mailHostID"
 */
private String mailHostID;
  /**
 * @uml.property  name="mailHostPass"
 */
private String mailHostPass;

  /**
 * @uml.property  name="mailPort"
 */
private String mailPort;

  /**
 * @uml.property  name="fromAddr"
 */
private String fromAddr;
  /**
 * @uml.property  name="fromName"
 */
private String fromName;
  /**
 * @uml.property  name="toAddrs"
 */
private String toAddrs;
  /**
 * @uml.property  name="toNames"
 */
private String toNames;
  /**
 * @uml.property  name="ccAddrs"
 */
private String ccAddrs;
  /**
 * @uml.property  name="ccNames"
 */
private String ccNames;
  /**
 * @uml.property  name="bccAddrs"
 */
private String bccAddrs;
  /**
 * @uml.property  name="bccNames"
 */
private String bccNames;
  /**
 * @uml.property  name="subject"
 */
private String subject;
  /**
 * @uml.property  name="textContents"
 */
private String textContents;
  /**
 * @uml.property  name="htmlContents"
 */
private String htmlContents;
  /**
 * @uml.property  name="attachPath"
 */
private String attachPath;
  /**
 * @uml.property  name="attachs"
 */
private String attachs;
                                 
  public SendMail(String mailHost) {
    this.mailHost = mailHost;
  }

  public SendMail(String mailHost, String mailPort) {
    this.mailHost = mailHost;
	this.mailPort = mailPort;
  }

  public SendMail(String mailHost, String mailHostID, String mailHostPass) {
    this.mailHost = mailHost;
    this.mailHostID = mailHostID;
    this.mailHostPass = mailHostPass;
  }
  
  public SendMail(String mailHost, String mailHostID, String mailHostPass, String mailPort) {
    this.mailHost = mailHost;
    this.mailHostID = mailHostID;
    this.mailHostPass = mailHostPass;
	this.mailPort = mailPort;
  }
  

  /**
   * 메일을 발송한다.
   *
   * @param fromAddr      보내는 사람 메일 주소
   * @param fromName      보내는 사람 이름
   * @param toAddrs       받는 주소 목록(구분자 ;)
   * @param toNames       받는 사람 이름 목록(구분자 ;). 받는 주소와 개수가 같아야 한다.
   * @param subject       메일 제목
   * @param textContents  텍스트 본문
   * @param htmlContents  HTML 본문
   */
  public void sendMail(String fromAddr, String fromName, String toAddrs, String toNames,
                       String subject, String textContents, String htmlContents)
      throws MessagingException, UnsupportedEncodingException {
    sendMail(fromAddr, fromName, toAddrs, toNames, null, null,
             subject, textContents, htmlContents, null, null);
  }

  /**
   * 메일을 발송한다.
   *
   * @param fromAddr      보내는 사람 메일 주소
   * @param fromName      보내는 사람 이름
   * @param toAddrs       받는 주소 목록(구분자 ;)
   * @param toNames       받는 사람 이름 목록(구분자 ;). 받는 주소와 개수가 같아야 한다.
   * @param ccAddrs       참조 주소 목록(구분자 ;)
   * @param ccNames       참조 이름 목록(구분자 ;). 참조 주소와 개수가 같아야 한다.
   * @param subject       메일 제목
   * @param textContents  텍스트 본문
   * @param htmlContents  HTML 본문
   * @param attachPath    첨부파일이 저장된 위치
   * @param attachs       첨부파일명 목록(구분자 ;)
   */
  public void sendMail(String fromAddr, String fromName, String toAddrs, String toNames,
                       String ccAddrs, String ccNames, String subject,
                       String textContents, String htmlContents,
                       String attachPath, String attachs)
      throws MessagingException, UnsupportedEncodingException {
    sendMail(fromAddr, fromName, toAddrs, toNames,
             ccAddrs, ccNames, null, null, subject, textContents, htmlContents,
             attachPath, attachs);
  }

  /**
   * 메일을 발송한다.
   *
   * @param fromAddr      보내는 사람 메일 주소
   * @param fromName      보내는 사람 이름
   * @param toAddrs       받는 주소 목록(구분자 ;)
   * @param toNames       받는 사람 이름 목록(구분자 ;). 받는 주소와 개수가 같아야 한다.
   * @param ccAddrs       참조 주소 목록(구분자 ;)
   * @param ccNames       참조 이름 목록(구분자 ;). 참조 주소와 개수가 같아야 한다.
   * @param bccAddrs      숨은 참조 주소 목록(구분자 ;)
   * @param bccNames      숨은 참조 이름 목록(구분자 ;). 참조 주소와 개수가 같아야 한다.
   * @param subject       메일 제목
   * @param textContents  텍스트 본문
   * @param htmlContents  HTML 본문
   * @param attachPath    첨부파일이 저장된 위치
   * @param attachs       첨부파일명 목록(구분자 ;)
   */
  public void sendMail(String fromAddr, String fromName, String toAddrs, String toNames,
                       String ccAddrs, String ccNames, String bccAddrs, String bccNames,
                       String subject, String textContents, String htmlContents,
                       String attachPath, String attachs)
      throws MessagingException, UnsupportedEncodingException {
    this.fromAddr = fromAddr;
    this.fromName = fromName;
    this.toAddrs = toAddrs;
    this.toNames = toNames;
    this.ccAddrs = ccAddrs;
    this.ccNames = ccNames;
    this.bccAddrs = bccAddrs;
    this.bccNames = bccNames;
    this.subject = subject;
    this.textContents = textContents;
    this.htmlContents = htmlContents;
    this.attachPath = attachPath;
    this.attachs = attachs;

    // 1. process mail parameters
    String[] toAddr = stringTokenizer( toAddrs, ";" );
    String[] toName = stringTokenizer( toNames, ";" );

    String[] ccAddr = stringTokenizer( ccAddrs, ";" );
    String[] ccName = stringTokenizer( ccNames, ";" );

    String[] bccAddr = stringTokenizer( bccAddrs, ";" );
    String[] bccName = stringTokenizer( bccNames, ";" );
    File[] attach = null;

    String[] attachName = stringTokenizer( attachs, ";" );
    if( attachPath != null && attachName != null && attachName.length > 0 ) {
      ArrayList tmp = new ArrayList();
      for( int i = 0; i < attachName.length; i++ ) {
        File file = new File( attachPath + File.separator + attachName[i] );
        if( file.exists() && file.isFile() ) tmp.add( file );
      }
      attach = new File[ tmp.size() ];
      tmp.toArray( attach );
    }

    // 2. set mail
    /*
     * SSL로 전송할 경우에는 세션만들때 넣는 Properties에 다음 속성을 더 넣어준다.
     * props.put("mail.smtp.startls.enable", "true");
     * props.put("mail.smtp.auth", "true");
     * props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
     * props.put("mail.smtp.socketFactory.fallback", "false");
     * props.put("mail.smtp.socketFactory.port", "port");
     * */
    Session mailSession = null;
    Properties props = new Properties();
    props.put( "mail.smtp.host", this.mailHost );
    if( this.mailHostID != null && !this.mailHostID.equals( "" ) ) {
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable","true");
      props.put("mail.smtp.socketFactory.port", mailPort); //googleMail
      if(!"".equals("javax.net.ssl.SSLSocketFactory"))
    	  	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
      if(!"".equals("false"))
    	  	props.put("mail.smtp.socketFactory.fallback", "false");
	  // 포트 설정 추가
	  if(this.mailPort != null && !this.mailPort.equals( "" )){
		props.put( "mail.smtp.port", mailPort );//genesis
		//System.out.println("mailPort in : "+mailPort);
	  }
	  
	  //System.out.println("mailPort out : "+mailPort);

      javax.mail.Authenticator auth = new MyAuthenticator( this.mailHostID, this.mailHostPass );
      mailSession = Session.getInstance( props, auth );
    } else {
    	
//    	 포트 설정 추가
  	  if(this.mailPort != null && !this.mailPort.equals( "" )){
  		props.put( "mail.smtp.port", mailPort );//genesis
  		//System.out.println("else mailPort in : "+mailPort);
  	  }
  	  
  	  //System.out.println("else mailPort out : "+mailPort);
  	  
      mailSession = Session.getDefaultInstance( props, null );
    }
    mailSession.setDebug( false );

    MimeMessage msg = new MimeMessage( mailSession );

    // 2.1 set send date
    msg.setSentDate( new Date() );

    // 2.2 set from
    if( fromName != null ) {
      msg.setFrom( new InternetAddress( fromAddr, fromName ) );
    } else {
      msg.setFrom( new InternetAddress( fromAddr ) );
    }

    // 2.3 set recipient
    if( toAddr != null && toAddr.length > 0 ) {
      InternetAddress[] to = new InternetAddress[ toAddr.length ];
      for( int i = 0; i < toAddr.length; i++ ) {
        if( toName != null && toName.length >= i+1 ) {
          to[i] = new InternetAddress( toAddr[i], toName[i] );
        } else {
          to[i] = new InternetAddress( toAddr[i] );
        }
      }
      msg.setRecipients( Message.RecipientType.TO, to );
    }

    // 2.4 set cc
    if( ccAddr != null && ccAddr.length > 0 ) {
      InternetAddress[] cc = new InternetAddress[ ccAddr.length ];
      for( int i = 0; i < ccAddr.length; i++ ) {
        if( ccName != null && ccName.length >= i+1 ) {
          cc[i] = new InternetAddress( ccAddr[i], ccName[i] );
        } else {
          cc[i] = new InternetAddress( ccAddr[i] );
        }
      }
      msg.setRecipients( Message.RecipientType.CC, cc );
    }

    // 2.5 set bcc
    if( bccAddr != null && bccAddr.length > 0 ) {
      InternetAddress[] bcc = new InternetAddress[ bccAddr.length ];
      for( int i = 0; i < bccAddr.length; i++ ) {
        if( bccName != null && bccName.length >= i+1 ) {
          bcc[i] = new InternetAddress( bccAddr[i], bccName[i] );
        } else {
          bcc[i] = new InternetAddress( bccAddr[i] );
        }
      }
      msg.setRecipients( Message.RecipientType.BCC, bcc );
    }

    // 2.6 set subject
    msg.setSubject( subject, "euc-kr" );

    // 2.7 set contents

    MimeMultipart mp = new MimeMultipart();

    // 2.7.1 set textContents
    if( textContents != null ) {

      MimeBodyPart mbpT = new MimeBodyPart();
      mbpT.setDataHandler( new DataHandler( new ByteArrayDataSource(
          textContents, "text/plain; charset=euc-kr" ) ) );
      mbpT.setHeader("Content-Transfer-Encoding", "7bit");
      mp.addBodyPart(mbpT);
    }

    // 2.7.2 set htmlContents
    if( htmlContents != null ) {

      MimeBodyPart mbpH = new MimeBodyPart();
      mbpH.setDataHandler( new DataHandler( new ByteArrayDataSource(
          htmlContents, "text/html; charset=euc-kr" ) ) );
      mbpH.setHeader("Content-Transfer-Encoding", "7bit");
      mp.addBodyPart(mbpH);
    }

    // 2.7.3 set attachments
    for( int i = 0; attach != null && i < attach.length; i++ ) {
      if( attach[i].exists() ) {
        MimeBodyPart mbpA = new MimeBodyPart();
        mbpA.setDataHandler( new DataHandler( new FileDataSource( attach[i] ) ) );
        mbpA.setFileName( toEng( attach[i].getName() ) );
        mp.addBodyPart( mbpA );
      }
    }

    msg.setContent(mp);

    // 3. send mail
    Transport.send(msg);

    // 4. delete attach files
    //for( int i = 0; attach != null && i < attach.length; i++ ) attach[i].delete();
  }

  public  String[] stringTokenizer(String str, String delim) {
    if( str == null ) {
      return null;
    } else {
      StringTokenizer st = new StringTokenizer(str, delim);

      ArrayList resultList = new ArrayList();
      String [] result = null;

      while (st.hasMoreTokens()) {
        resultList.add(st.nextToken().trim());
      }

      result = new String[resultList.size()];
      resultList.toArray(result);

      return result;
    }
  }
  
 
   /**
   * KSC5601로 인코딩된 문자열을 ISO8859_1로 변환한다.
   *
   * @param str5601 KSC5601로 encoding된 String
   * @return ISO8859_1로 변환된 String
   */
  public  String toEng( String str5601 ) {

    if( str5601 == null ) return null;

    String str8859 = null;

    try {
      str8859 = new String( str5601.getBytes("KSC5601"), "8859_1" );
    } catch( UnsupportedEncodingException e ) {
      str8859 = str5601;
    }

    return str8859;
  }
  
  
  public String toString()  {
    StringBuffer buf = new StringBuffer();
    buf.append( "<pre>\n" );
    buf.append( this.getClass().getName() + " -\n" );
    buf.append( "  mailHost : " + this.mailHost + "\n" );
    buf.append( "  mailHostID : " + this.mailHostID + "\n" );
    buf.append( "  mailHostPass : " + this.mailHostPass + "\n" );
    buf.append( "  fromAddr : " + this.fromAddr + "\n" );
    buf.append( "  fromName : " + this.fromName + "\n" );
    buf.append( "  toAddrs : " + this.toAddrs + "\n" );
    buf.append( "  toNames : " + this.toNames + "\n" );
    buf.append( "  ccAddrs : " + this.ccAddrs + "\n" );
    buf.append( "  ccNames : " + this.ccNames + "\n" );
    buf.append( "  bccAddrs : " + this.bccAddrs + "\n" );
    buf.append( "  bccNames : " + this.bccNames + "\n" );
    buf.append( "  subject : " + this.subject + "\n" );
    buf.append( "  textContents : " + this.textContents + "\n" );
    buf.append( "  htmlContents : " + this.htmlContents + "\n" );
    buf.append( "  attachPath : " + this.attachPath + "\n" );
    buf.append( "  attachs : " + this.attachs + "\n" );
    buf.append( "</pre>" );

    return buf.toString();
  }

  /* This class implements a typed DataSource from :
  *  an InputStream
  *  a byte array
  *  a String
   */
  class ByteArrayDataSource implements DataSource {
    private byte[] data; // data
    private String type; // content-type

    /* Create a datasource from an input stream */
    ByteArrayDataSource( InputStream is, String type ) {
      this.type = type;
      try {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int ch;

        while ( ( ch = is.read() ) != -1 )
          // XXX : must be made more efficient by
          // doing buffered reads, rather than one byte reads
          os.write( ch );
        data = os.toByteArray();

        } catch( IOException e ) {}
    }

    /* Create a datasource from a byte array */
    ByteArrayDataSource( byte[] data, String type ) {
      this.data = data;
      this.type = type;
    }

    /* Create a datasource from a String */
    ByteArrayDataSource(String data, String type) {
      try {
        // Assumption that the string contains only ascii
        // characters ! Else just pass in a charset into this
        // constructor and use it in getBytes()
        this.data = data.getBytes( "KSC5601" );
        //this.data = data.getBytes();
        } catch( UnsupportedEncodingException e ) {}
        //} catch (Exception uex) { }
        this.type = type;
    }

    public InputStream getInputStream() throws IOException {
      if ( data == null ) throw new IOException( "no data" );
      return new ByteArrayInputStream( data );
    }

    public OutputStream getOutputStream() throws IOException {
      throw new IOException( "cannot do this" );
    }

    public String getContentType() {
      return type;
    }

    public String getName() {
      return "dummy";
    }
  }

  class MyAuthenticator extends javax.mail.Authenticator {
    javax.mail.PasswordAuthentication pa;

    public MyAuthenticator( String mailHostID, String mailHostPass ) {
      pa = new javax.mail.PasswordAuthentication( mailHostID, mailHostPass );
    }

    public javax.mail.PasswordAuthentication getPasswordAuthentication() {
      return pa;
    }
  }
}