package md.util;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONValue;

//import sun.misc.BASE64Decoder;

import com.oreilly.servlet.Base64Decoder;

import md.cms.component.ABComponent;
import md.cms.util.JSON;
import md.database.Database;

public class Util {
	public static void main(String[] args) {
		String dir = "/test0/test1/";
		String link = "../../test2/test3/ttt?a=123";
		System.out.println(MakeURL(dir, link));

	}
	
	public static void clearAttributes(HttpServletRequest req) {
		
		Enumeration e = req.getAttributeNames();
		String key = null;
		while(e.hasMoreElements()) {
			key = (String)e.nextElement();
			//System.out.println(key+"=======>"+req.getAttribute(key));
			clearMem(req.getAttribute(key));
		}
		
	}
	
	public static void clearAttributes(HttpSession ss) {
		
		
		Enumeration e = ss.getAttributeNames();
		String key = null;
		while(e.hasMoreElements()) {
			key = (String)e.nextElement();
			ss.removeAttribute(key);
			//System.out.println(key+"=======>"+req.getAttribute(key));
			//clearMem(ss.getAttribute(key));
			
		}
		
	}
	
	public static void clearMem(Object obj) {
	
		clearMem(obj, false);
		
	}
	
	public static void clearMem(Object obj, boolean force) {
		
		//System.out.println(obj);
		String key = null;
		Object element = null;
		if( obj instanceof Hashtable) {
			
			if( force || !"Y".equals( ((Hashtable)obj).get("_is_static_") )) {
				//System.out.println("clear:"+obj);
				// static 이면 skip
				Enumeration en = ((Hashtable)obj).keys();
				/*
				while(en.hasMoreElements()) {
					element = (Object)en.nextElement();
					clearMem(element);
				}*/
				while(en.hasMoreElements()) {
					key  = (String)en.nextElement();
					clearMem(((Hashtable)obj).get(key), force);
				}
				
				en = null;
				((Hashtable)obj).clear();
				
			}
			
		} else if( obj instanceof AbstractMap) {

			if( force || !"Y".equals( ((AbstractMap)obj).get("_is_static_") )) {
			
				//System.out.println("clear:"+obj);
				//Iterator it = ((AbstractMap)obj).entrySet().iterator();
				
				Iterator it2 = ((AbstractMap)obj).keySet().iterator();
				while(it2.hasNext()) {
					key = (String)it2.next();
						//System.out.println(key);
					//System.out.println("element=====>"+element.getClass().getName()+"=======>"+ ((AbstractMap)obj).get(key));
					clearMem(((AbstractMap)obj).get(key), force);
					
				}
				/*
				while(it.hasNext()) {
					element = (Object)it.next();
					System.out.println("element=====>"+element.getClass().getName()+"=======>"+ element);
					clearMem(element);
				}*/
				((AbstractMap)obj).clear();
				it2 = null;
			}
		} else if( obj instanceof AbstractList) {
			for(int i=0; i<((AbstractList)obj).size(); i++) {
				clearMem(((AbstractList)obj).get(i));
			}
			((AbstractList)obj).clear();
		} else if( obj instanceof JSON ) {
			((JSON)obj).clear();
		} else if( obj instanceof StringBuffer) {
			((StringBuffer)obj).setLength(0);
		} else if( obj instanceof ABComponent) {
			((ABComponent)obj).clear();
		}
		obj = null;
		
	}

	/*
	 * dir: /test/test/ 
	 */
	public static String MakeURL(String dir, String link) {
		int idx = 0;
		String rtn = link.replaceAll("&amp;", "&");
		if(!rtn.startsWith("/")) {
			rtn = dir + rtn;
		}
		String query = "";
		if((idx=rtn.indexOf("?")) != -1) {
			query = rtn.substring(idx);
			rtn = rtn.substring(0, idx);
		}
		// 
		while((idx=rtn.indexOf("//")) > -1) {
			rtn = rtn.replaceAll("//", "/");
		}
		// /./ 
		while((idx=rtn.indexOf("/./")) > -1) {
			rtn = rtn.replaceAll("/./", "/");
		}
		// .
		while((idx=rtn.indexOf("/../")) > -1) {
			if(rtn.startsWith("/../")) {
				rtn = rtn.substring(3);
			} else {
				int idx2 = rtn.lastIndexOf("/", idx-1);
				rtn = rtn.substring(0, idx2) + rtn.substring(idx+3);
			}
		}
		return rtn+query;
	}	
	

	public static boolean checkCharacter(char[] str, char c) {
		for(int i=0;i<str.length;i++) {
			if(c == str[i]) {
				return true;
			}
		}
		return false;
	}

	public static String stripQuote(String v) {
		String rtn = v;
		try {
			char s = v.charAt(0);
			char e = v.charAt(v.length()-1);
			if(s == e && (s == '\'' || s == '\"')) {
				rtn = v.substring(1, v.length()-1);
			}
		} catch(Exception e) {
		}
		return rtn;
	}
	
	public static String toHTML(Object str) {
		return QuoteHTMLEncoding(Util.toStr(str));
	}
	
	public static String toHTML(String str) {
		return QuoteHTMLEncoding(str);
	}
	
	private static final String[] qSearchList = {"'","\""};
	private static final String[] qreplacementList = {"&#039;","&quot;"};
	public static String QuoteHTMLEncoding(String str ) {
		/*
		if(str == null) return "";
		return StringUtils.replaceEach(str, qSearchList, qreplacementList);
		*/
		
		if(str == null) return "";

		str = HTMLEncoding(str);
		str = StringUtils.replace(str, "'","&#039;") ;	// 큰 따옴표 
		str = StringUtils.replace(str, "\"","&quot;") ;	// 큰 따옴표
		
		return str;
		
	}

	private static final String[] hSearchList = {"&",">","<"};
	private static final String[] hreplacementList = {"&amp;","&gt;","&lt;"};
	public static String HTMLEncoding(String str) {
		/*
		if(str == null) return "";
		return StringUtils.replaceEach(str, hSearchList, hreplacementList);
		*/
		if(str == null) return "";
		
		str = StringUtils.replace(str, "&", "&amp;");	// &
		str = StringUtils.replace(str, ">", "&gt;");
		str = StringUtils.replace(str, "<", "&lt;");
		
		return str;
	}
	
	private static final String[] tSearchList = {" ","\t","\n"};
	private static final String[] treplacementList = {"&#160;","&#160;&#160;&#160;&#160;","<br/>"};
	public static String TextToHtml(String str) {
		/*
		if(str == null) return "";
		return StringUtils.replaceEach(str, tSearchList, treplacementList);
		*/
		
		if(str == null) return "";
		//str = 	getRemoveScript(str);
		str = 	HTMLEncoding(str);
		str =  	StringUtils.replace(str, " ", "&#160;");	// &
		str =   StringUtils.replace(str, "\t","&#160;&#160;&#160;&#160;");
		str =  	StringUtils.replace(str, "\n", "<br/>");	// &
		
		return str;
	
	}
	
	/**
	 * 태그를 제거한다.
	 * @param content
	 * @return
	 */
	public static String getRemoveTag(String content) {
		
		 return toStr(content).replaceAll("<(/)?([a-zA-Z\\:\\s]*)(\\s[a-zA-Z\\:\\s]*=[^>]*)?(\\s)*(/)?>", "");
		
	}
	
	/**
	 * 인자롤 넘어온 값이 null 이거나 공백이면 참을 리턴
	 * @param obj
	 * @return
	 */
	public static boolean isEmptyString(Object obj) {
		
		return Util.toStr(obj).equals("");
		
	}
	
	
	public static String getRemoveScript(String strContent){

		if(strContent == null) return "";
		
		  //Pattern patternTag = Pattern.compile("\\<(\\/?)(\\w+)*([^<>]*)>");
		  String badTag = "script|applet|form|frame|frameset|base";	
		
		  Pattern patternScript=Pattern.compile("(?i)\\<\\s*("+badTag+")(\\>)?(.*?)("+badTag+")?\\s*>",Pattern.DOTALL | Pattern.MULTILINE);
		  Pattern patternJava=Pattern.compile("(?i)javascript|vbscript",Pattern.DOTALL | Pattern.MULTILINE);
		  //Pattern patternScript=Pattern.compile("(?i)\\<[ ]?script",Pattern.DOTALL | Pattern.MULTILINE);
		  
		  //Pattern patternMouseOver=Pattern.compile("(?i)on(.*?)=[\"']?([^>\"']+)[\"']*",Pattern.DOTALL | Pattern.MULTILINE);
		  Pattern patternMouseOver=Pattern.compile("(?i)\\<[^(on[a-z]+)|>]*on[a-z]+\\s*=\\s*[\\s\"']*([^>\"']+)[\\s\"']*",Pattern.DOTALL | Pattern.MULTILINE);
		  //Pattern patternMouseOut=Pattern.compile("(?i) onmouseout=[\"']?([^>\"']+)[\"']*",Pattern.DOTALL | Pattern.MULTILINE);
		  //Pattern patternMouseClick=Pattern.compile("(?i) onclick=[\"']?([^>\"']+)[\"']*",Pattern.DOTALL | Pattern.MULTILINE);

		  
		  Matcher matcher=patternScript.matcher(strContent);
		  //strContent = matcher.replaceAll("");
		  StringBuffer sb = new StringBuffer();
		  while(matcher.find()) {
			  String str = matcher.group();
			  //System.out.println(str);
			  //strContent = Util.ReplaceAll(strContent, str, "");
			  //matcher.appendReplacement(sb, Util.HTMLEncoding(str));
			  matcher.appendReplacement(sb, "");
		  }
		  matcher.appendTail(sb);
		  strContent = sb.toString();
		  
		  
		  matcher=patternJava.matcher(strContent);
		  strContent = matcher.replaceAll("");
		  
		  matcher=patternMouseOver.matcher(strContent);
		  sb = new StringBuffer();
		  while(matcher.find()) {
			  String str = matcher.group();
			  //strContent = Util.ReplaceAll(strContent, str, "");
			  //System.out.println(str);
			  matcher.appendReplacement(sb, str.substring(0,str.indexOf("on")));
		  }
		  matcher.appendTail(sb);
		  strContent = sb.toString();
		  
		  
		  
		  return strContent;
	}
	
	
	public static String readLine(InputStream is, String encoding, int max_buffer) throws Exception {
		byte[] data = new byte[max_buffer];
		int idx = 0;
		while(true) {
			int b = is.read();
			if(b == -1) {
				if(idx == 0) {
					String rtn = null;
					return rtn;
				} else {
					String rtn = new String(data, 0, idx, encoding);
					return rtn;
				}
			} else {
				data[idx] = (byte)b;
				idx++;
				if(b == '\n') {
					String rtn = new String(data, 0, idx, encoding);
					return rtn;
				}
			}
			if(idx == max_buffer) {
				String rtn = new String(data, encoding);
				return rtn;
			}
		}
	}
	
	public static String toStr(String value) {
		if(value == null) {
			return "";
		}
		
		return value;
	}
	
	public static String toStr(Object object) {
		if(object == null) {
			return "";
		}
		return String.valueOf(object);
	}
	
	public static String toStr(Object object, String defValue) {
		if(object == null) {
			return defValue;
		}
		return String.valueOf(object);
	}
	
	/*
	public static String ReplaceAll(String strSource, String strFrom, String strTo)
	{
	    int index = strSource.indexOf(strFrom);
	    StringBuffer buf    = new StringBuffer();
	        
	    if(index<0)
	    {
	        return strSource;
	    }
	    buf.append(strSource.substring(0, index));
	    buf.append(strTo);
	    if (index + strFrom.length() <strSource.length())
	    {
	        buf.append(ReplaceAll(strSource.substring(index+strFrom.length(), strSource.length()), strFrom, strTo));
	    }
	    return buf.toString();
	}
	*/	
	
	public static boolean nullOrEmpty( Object obj ) {
		
		return obj == null || obj.equals("");
	}
	
	public static String ReplaceAll(String strOrig, String strFind, String strReplace) {
		
		if(strOrig == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer(strOrig);
		String toReplace = "";
		if (strReplace == null) 
			toReplace = "";
		else 
			toReplace = strReplace;
		
		int pos = strOrig.length();
		while (pos > -1) {
			pos = strOrig.lastIndexOf(strFind, pos);
			if (pos > -1) 
				sb.replace(pos, pos+strFind.length(), toReplace);
			pos = pos - strFind.length();
		}
		return sb.toString();
	}
	
	
	public static StringBuffer ReplaceAll(StringBuffer strOrig, String strFind, String strReplace) {
		
		if(strOrig == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer(strOrig.toString());
		String toReplace = "";
		if (strReplace == null) 
			toReplace = "";
		else 
			toReplace = strReplace;
		
		int pos = strOrig.length();
		while (pos > -1) {
			pos = strOrig.lastIndexOf(strFind, pos);
			if (pos > -1) 
				sb.replace(pos, pos+strFind.length(), toReplace);
			pos = pos - strFind.length();
		}
		return sb;
	}
	
	
	/****************************************
	/* 한글변환
	****************************************/
	public static String toKor(String str) {
		if(str == null)	return "" ;
		try	{
			str	= new String(str.getBytes("8859_1"), "KSC5601")	;
		} catch(Exception e) {
		}
		return str ;
	}
	
	
	public static String toUTF8(String str) {
		if(str == null)	return "" ;
		try	{
			str = new String(str.getBytes("8859_1"),"UTF-8");
		} catch(Exception e) {
			
		}
		return str;
		
	}

	public static String zerofill(int num, int size) throws Exception {
		/*
		StringBuffer zero = new StringBuffer();   
		for (int i = 0; i < size; i++) {   
		  zero.append("0");   
		}   
		DecimalFormat df = new DecimalFormat(zero.toString());
		*/
		
		return String.format("%0"+size+"d", num);
		//return df.format(num);   
	}   
	  
	public static String zerofill(double num, int size) throws Exception {
		/*
		StringBuffer zero = new StringBuffer();   
		for (int i = 0; i < size; i++) {   
		  zero.append("0");   
		}   
		DecimalFormat df = new DecimalFormat(zero.toString());   
		return df.format(num);*/   
		return String.format("%0"+size+"d", num);
	}   
	
	public static String getCurDate( String format ) {
		Date date =	new	Date ()	;
		SimpleDateFormat formatter = new SimpleDateFormat ( format ) ;
		return	formatter.format(date) ;
	}


	public static String getCurDate() {
		Date date =	new	Date ()	;
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd") ;
		return	formatter.format(date) ;
	}
	
	public static String getCurTime() {
		Date date =	new	Date ()	;
		SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm:ss") ;
		return	formatter.format(date) ;
	}
	
	
	
	// 날짜의 차이 구하기
	public static int betweenDate( String sDate, String cDate ) throws Exception {

		int count = 0;
		
		try {
		
			cDate = ReplaceAll(cDate,"-", "");
			sDate = ReplaceAll(sDate,"-", "");
			
			Calendar cal = Calendar.getInstance();
			cal.set( Integer.parseInt( cDate.substring(0,4) ),
					  Integer.parseInt( cDate.substring(4,6) ) -1,
					  Integer.parseInt( cDate.substring(6,8) ) );
	
	
			Calendar cal2 = Calendar.getInstance ( );
			cal2.set( Integer.parseInt( sDate.substring(0,4) ),
					  Integer.parseInt( sDate.substring(4,6) ) -1,
					  Integer.parseInt( sDate.substring(6,8) ) );
	
			if( Integer.parseInt(cDate) > Integer.parseInt(sDate) ) {
				count = -1;
				while ( !cal2.after ( cal ) )
				{
					count++;
					cal2.add ( Calendar.DATE, 1 ); // 다음날로 바뀜
	
				}
			} else if( Integer.parseInt(cDate) == Integer.parseInt(sDate) ) {
	
				count = 0;
	
			} else {
				count = 1;
				while ( !cal.after ( cal2 ) )
				{
					count--;
					cal.add ( Calendar.DATE, 1 ); // 다음날로 바뀜
	
				}
			}
		
		} catch( Exception e) {
		}

		return count;
	
	}
	

	// 이달의 첫번째 날짜 스트링 구하기
	public static String getFirstDate() throws Exception {
		return getFirstDate("yyyy-MM-dd");
	}
	
	// 이달의 첫번째 날짜 스트링 구하기
	public static String getFirstDate( String format ) throws Exception {

		Calendar day = Calendar.getInstance();
		Date date = new Date();
		day.setTime ( date );
		day.set( Calendar.DATE, 1 ) ; 

		SimpleDateFormat formatter = new SimpleDateFormat(format) ;

		return	formatter.format(day.getTime()) ;

	}
	// 이달의 마지막 날짜 스트링 구하기
	public static String getLastDate( ) throws Exception {
		return getLastDate("yyyy-MM-dd");
	}
	
	// 특정달의 마지막 날짜 스트링
	public static String getLastDate(String year, String month, String format ) {
		
		SimpleDateFormat dFormat = new SimpleDateFormat ( format );

		Calendar calendar = Calendar.getInstance();
		calendar.set( Integer.parseInt( year ),
					  Integer.parseInt( month ) -1,  1  );
		
		calendar.set( Calendar.DATE, calendar.getActualMaximum(Calendar.DATE) ) ; 
		
		return	dFormat.format(calendar.getTime()) ;
		
	}
	
	public static int getLastDate(String year, String month ) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set( Integer.parseInt( year ),
					  Integer.parseInt( month ) -1,  1  );
		
		return	calendar.getActualMaximum(Calendar.DATE);
		
	}
	

	// 이달의 마지막 날짜 스트링 구하기
	public static String getLastDate( String format ) throws Exception {

		Calendar day = Calendar.getInstance();
		Date date = new Date();
		day.setTime ( date );
		day.set( Calendar.DATE, day.getActualMaximum(Calendar.DATE) ) ; 

		SimpleDateFormat formatter = new SimpleDateFormat(format) ;

		return	formatter.format(day.getTime()) ;

	}
	
	// 일자 + -
	public static String incDate( int aStep ) {

		return incDate(getCurDate(), aStep, false);

	}
	
	// 일자 + -
	public static String incDate( String aDate, int aStep ) {

		return incDate(aDate, aStep, false);

	}
	
	public static String incDate( String aDate, int aStep, boolean skipHoliday ) {
		return incDate( aDate, aStep, skipHoliday, "yyyy-MM-dd" );
	}
	
	
	// 일자 + -
	public static String incDate( String aDate, int aStep, boolean skipHoliday, String format ) {

		SimpleDateFormat dFormat = new SimpleDateFormat ( format );

		Calendar calendar = getCalendar(aDate);
		
		if(skipHoliday) {
			int stp = 0;
			while(true) {
				calendar.add( Calendar.DATE , 1 );
				int yoil = calendar.get(Calendar.DAY_OF_WEEK); 
				if( yoil == 1 || yoil == 7) {
					continue;
				}
				stp ++;
				if (stp == aStep) break;
			}
		} else {
			calendar.add( Calendar.DATE , aStep );
		}

		return dFormat.format( calendar.getTime() );

	}
	

	public static String incYear( String aDate, int aStep ) {
		return incYear(aDate, aStep, "yyyy-MM-dd");
	}
	
	// 년 +-
	public static String incYear( String aDate, int aStep, String format ) {

		SimpleDateFormat dFormat = new SimpleDateFormat ( format );

		Calendar calendar = getCalendar(aDate);
		calendar.add( Calendar.YEAR , aStep );

		return dFormat.format( calendar.getTime() );


	}

	public static Date getDate( String aDate ) {

		Calendar calendar = getCalendar(aDate);
		
		return calendar.getTime();
		
	}
	
	public static String incMonth( String aDate, int aStep ) {
		return incMonth( aDate, aStep, "yyyy-MM-dd" );
	}
	
	// 월 +-
	public static String incMonth( String aDate, int aStep, String format ) {

		SimpleDateFormat dFormat = new SimpleDateFormat ( format );

		Calendar calendar = getCalendar(aDate);

		calendar.add( Calendar.MONTH , aStep );

		return dFormat.format( calendar.getTime() );


	}
	
	public static String incMonth( Calendar cal, int aStep ) {
		return incMonth( cal, aStep, "yyyy-MM-dd" );
	}
	public static String incMonth( Calendar cal, int aStep, String format ) {

		SimpleDateFormat dFormat = new SimpleDateFormat ( format );
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),cal.get(Calendar.HOUR)
				,cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));

		calendar.add( Calendar.MONTH , aStep );

		return dFormat.format( calendar.getTime() );


	}
	
	
	//특정일의 일요일을 일자로..
	public static String firstDateOfWeek(String aDate, String fmt ) {

		SimpleDateFormat dFormat = new SimpleDateFormat ( fmt );

		Calendar calendar = getCalendar(aDate);

		int yoil = calendar.get(Calendar.DAY_OF_WEEK); 
		
		 
		calendar.add( Calendar.DATE , -(yoil-1) );

		return dFormat.format( calendar.getTime() );
		
	}
	
	//특정일의 토요일을 일자로..
	public static String lastDateOfWeek(String aDate, String fmt ) {

		SimpleDateFormat dFormat = new SimpleDateFormat ( fmt );

		Calendar calendar = getCalendar(aDate);

		int yoil = calendar.get(Calendar.DAY_OF_WEEK); 
		
		 
		calendar.add( Calendar.DATE , (7-yoil) );

		return dFormat.format( calendar.getTime() );
		
	}
	
	// 요일 
	public static String getWeekName(String aDate) {

		Calendar calendar = getCalendar(aDate);
		int yoil = calendar.get(Calendar.DAY_OF_WEEK);
		return intToYoil(yoil);
		 
	}
		
	public static Calendar getCalendar( String aDate ) {

		Calendar calendar = Calendar.getInstance();
		
		if(aDate == null) {
			return calendar;
		}
		
		if(aDate.length() >= 10) {
		calendar.set( Integer.parseInt( aDate.substring(0,4) ),
					  Integer.parseInt( aDate.substring(5,7) ) -1,
					  Integer.parseInt( aDate.substring(8,10) ) );
		} else {
			calendar.set( Integer.parseInt( aDate.substring(0,4) ),
					  Integer.parseInt( aDate.substring(4,6) ) -1,
					  Integer.parseInt( aDate.substring(6,8) ) );
			
		}
		
		return calendar;
		
	}
	
	public static Date stringToDate(String dt ) {

		Calendar cal = getCalendar( dt );
		
		return cal.getTime();
	}
	
	public static String dateToString(Date dt, String fmt) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		SimpleDateFormat dFormat = new SimpleDateFormat ( fmt );

		return dFormat.format( cal.getTime() );
		
	}
	
	public static String calendarToDateString(Calendar calendar, String fmt) {

		SimpleDateFormat dFormat = new SimpleDateFormat ( fmt );

		return dFormat.format( calendar.getTime() );
		
	}
	
	public static String getSubStr(String str2, int len, String tail) throws Exception{
        String str = "";
        try{
            str = str2;
        }catch(Exception e){
            System.out.println(e.toString());
            str = str2;
        }
        if(str.getBytes().length <= len)
	        return str2;
        StringCharacterIterator sci = new StringCharacterIterator(str);
        StringBuffer sb = new StringBuffer();
        sb.append(sci.first());
        for(int i = 1; i < len; i++)
            if(i < len - 1){
                    char c = sci.next();
                    sb.append(c);
            }else{
                    char c = sci.next();
                    sb.append(c);
            }

        sb.append(tail);
        return sb.toString();
	}
	/*
	 * 가나다라" 에서 2바이트까지 자르고 싶을경우 strCut("가나다라", null, 2, 0, true, true); 처럼 하시면 됩니다.
 	=> 결과 : "가"
	"가나다라" 에서 "다"라는 키워드 기준에서 2바이트까지 자르고싶을경우 strCut("가나다라", "다", 2, 0, true, true); 처럼 하시면 됩니다.
 	=> 결과 : "다"
	"가나다라" 에서 "라"라는 키워드 기준으로 그 이전의 4바이트까지 포함하여 6바이트까지 자르고 싶을 경우 strCut("가나다라", "라", 6, 4, true, true); 처럼 하시면 됩니다.
 	=> 결과 : "나다라"
	"가나다라" 에서 3바이트를 자를 경우
 	=> 결과 : "가"
	"가a나다라" 에서 3바이트를 자를 경우
 	=> 결과 : "가a"
	"가나다라" 에서 "나" 키워드 기준으로 이전 1바이트 포함하여 4바이트까지 자를 경우
 	=> 결과 : "나"
	 */
	public static String getSubStr(String szText, int nLength){  
		return getSubStr(szText, null, nLength, 0, true, true); 
	}
	public static String getSubStr(String szText, String szKey, int nLength, int nPrev, boolean isNotag, boolean isAdddot){  // 문자열 자르기
	    
		// 사이즈가 0 이면 원래 텍스트를 리턴
		if(nLength == 0) {
			return szText;
		}
		
	    String r_val = Util.toStr(szText);
	    int oF = 0, oL = 0, rF = 0, rL = 0; 
	    int nLengthPrev = 0;
	    //Pattern p = Pattern.compile("<(/?)([^<>]*)?>", Pattern.CASE_INSENSITIVE);  // 태그제거 패턴
	   
	    //if(isNotag) {r_val = p.matcher(r_val).replaceAll("");}  // 태그 제거
	    //r_val = StringUtils.replace(r_val, "&amp;", "&");
	    //r_val = r_val.replaceAll("(!/|\r|\n|&nbsp;)", "");  // 공백제거
	 
	    try {
	      byte[] bytes = r_val.getBytes("UTF-8");     // 바이트로 보관 
	      if(szKey != null && !szKey.equals("")) {
	        nLengthPrev = (r_val.indexOf(szKey) == -1)? 0: r_val.indexOf(szKey);  // 일단 위치찾고
	        nLengthPrev = r_val.substring(0, nLengthPrev).getBytes("MS949").length;  // 위치까지길이를 byte로 다시 구한다
	        nLengthPrev = (nLengthPrev-nPrev >= 0)? nLengthPrev-nPrev:0;    // 좀 앞부분부터 가져오도록한다.
	      }
	    
	      // x부터 y길이만큼 잘라낸다. 한글안깨지게.
	      int j = 0;

	      if(nLengthPrev > 0) while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          oF+=2; rF+=3; if(oF+2 > nLengthPrev) {break;} j+=3;
	        } else {if(oF+1 > nLengthPrev) {break;} ++oF; ++rF; ++j;}
	      }
	      
	      j = rF;

	      while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          if(oL+2 > nLength) {break;} oL+=2; rL+=3; j+=3;
	        } else {if(oL+1 > nLength) {break;} ++oL; ++rL; ++j;}
	      }
	      try {
	      r_val = new String(bytes, rF, rL, "UTF-8");  // charset 옵션
	      } catch( Exception ee) {
		    	System.out.println("getSubStr szText error"+szText);
	      }

	      if(isAdddot && rF+rL+3 <= bytes.length) {r_val+="..";}  // ...을 붙일지말지 옵션 
	    } catch(UnsupportedEncodingException e){ 
	    	e.printStackTrace(); 
	    }   
	    
	    return r_val;
	  }

	// 숫자를 요일로
	public static String intToYoil( int dayOfWeek) {		


		switch (dayOfWeek) {
			case 1: return "일";
			case 2: return "월";
			case 3: return "화";
			case 4: return "수";
			case 5: return "목";
			case 6: return "금";
			case 7: return "토";
		}
		
		return "";
	}
	
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		Cookie returnCookie = null;

		if(cookies != null) {
			for( int i=0; i < cookies.length; i ++ ) {
				if(cookies[i].getName().equals(name)) {
					returnCookie = cookies[i];
					break;
				}
			}
		}
		return returnCookie;
	}

	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		String retv = null;

		if(cookies != null) {
			for( int i=0; i < cookies.length; i ++ ) {
				if(cookies[i].getName().equals(name)) {
					retv = cookies[i].getValue();
					break;
				}
			}
		}
		return retv;
	}
	
    public static void createCookie(HttpServletResponse response, String name, String value)  {
        Cookie cookie = new Cookie(name,value);
		response.addCookie(cookie);	
    }

    public static void createCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
        Cookie cookie = new Cookie(name,value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
		response.addCookie(cookie);	
    }

    public static void createCookie(HttpServletResponse response, String name, String value, String domain, String path,int maxAge)  {
        Cookie cookie = new Cookie(name,value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
		cookie.setDomain(domain);
		response.addCookie(cookie);	
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value)  {
		
		createCookie( response, name, value );
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String domain, String path, int maxAge)  {
		
		createCookie( response, name, value, domain, path, maxAge );
    }

    
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String path, int maxAge)  {
		
		createCookie( response, name, value, path, maxAge );
    }
	 
    public static String leftStr(String value, int len) {
    	
    	if(value == null) return null;
    	
    	if(value.length() > len) {
    		return value.substring(0,len);
    	}
    	
    	return value;
    	
    }
    
    /*
     * 맵을 카피한다.
     */

    public static void copyToMap(HashMap src, HashMap dest) {
    	
    	Iterator it = src.keySet().iterator();
    	while(it.hasNext()) {
    		String key = (String)it.next();
    		dest.put(key, src.get(key));
    	}
    	
    }
    
    /*
     * 날짜 유효성 체크
     */
    public static boolean isValidDate(Object year, Object month, Object date) {
    	if(date ==null || month == null || date == null) return false;
    	String format = null;
		String dt = null;
    	try {
    		dt = String.valueOf(year)+zerofill( Integer.parseInt(String.valueOf(month)), 2)+zerofill( Integer.parseInt(String.valueOf(date)), 2);
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    		format = sdf.format(sdf.parse(dt));
    	} catch (Exception e) {
    	}
    	return dt.equals(format);
    }
	
    
    public static String getDateFormat(Object year, Object month, Object date, String format) {
    	
		SimpleDateFormat dFormat = new SimpleDateFormat ( format );

		Calendar calendar = Calendar.getInstance();
		calendar.set( Integer.parseInt( String.valueOf(year) ),
					  Integer.parseInt( String.valueOf(month) ) -1,  Integer.parseInt(String.valueOf(date))  );
		
		return	dFormat.format(calendar.getTime()) ;
    	
    	
    }
    
    /*
     *  ie6 이하인지 판단
     */
    public static boolean isIE6(HttpServletRequest req) {
    	
		String userAgent = Util.toStr(req.getHeader("User-Agent").toLowerCase());
		
		Pattern p = Pattern.compile("msie 5\\.5|msie5\\.5|msie 6");
		Matcher m = p.matcher(userAgent);
		if(m.find()) {
			return true;
		}
		return false;
    	
    }
    
    /**
     * 지정한 길이만큼 문자의 오른쪽에 공백을 더한다
     * @param val 문자
     * @param len 길이
     * @return
     */
    public static String rightSpace(String val, int len) {

    	if(val == null) return "";
    	
    	if(val.length() > len) {
    		return val.substring(0,len);
    	} else {
    		StringBuffer retv = new StringBuffer(val);
    		for(int i=0; i<len-val.length(); i++) {
    			retv.append(' ');
    		}
    		return retv.toString();
    	}
    	

    }
    
    /**
     * 지정한 길이만큼 문자의 왼쪽에 공백을 더한다.
     * @param val 문자
     * @param len 길이
     * @return 
     */
    public static String leftSpace(String val, int len) {

    	if(val == null) return "";
    	
    	if(val.length() > len) {
    		return val.substring(0,len);
    	} else {
    		StringBuffer retv = new StringBuffer(val);
    		for(int i=0; i<len-val.length(); i++) {
    			retv.insert(0,' ');
    		}
    		return retv.toString();
    	}

    }
    
    /**
     * 숫자에 컴마를 넣는다
     * @param value
     * @return
     */
    public static String addComma(Object value, int decimalSize) {
    	
    	if(Util.nullOrEmpty(value)) {
    		return "";
    	}
    	
    	Double num = Double.valueOf(value.toString());
    	DecimalFormat df = null;
    	if(decimalSize > 0) {
    		try {
				df = new DecimalFormat("#,##0."+Util.zerofill(0, decimalSize));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		df = new DecimalFormat("#,##0");
    	}
    	
    	return df.format(num);
    }
    
    
	public static String toFileSize(double size) {
		
		String retv = "";
		DecimalFormat df = new DecimalFormat("#,###,###.0");

	  if (size > 1024*1024*1024) {
	          return df.format(size / (1024*1024*1024)) + " GB";
	  } else if (size > 1024*1024 ) {
	          return df.format(size / (1024*1024)) + " MB";
	  } else if (size > 1024) {
	          return df.format(size / 1024) + " KB";
	  } else {
	          return size + " bytes";
	  }
		
	}

	/**
	 * HashSet 을 콤마(,) 로구분하여 스트링으로 리터 1,2,3,4
	 * @param set
	 * @return
	 */
	public static String setToCommaString(HashSet set) {
		
		StringBuffer retv = new StringBuffer(); 
		boolean first = true;
		if(set != null) {
			Iterator it = set.iterator();
			while( it.hasNext() ) {
				if(!first) {
					retv.append(",");
				}
				retv.append(it.next());
				first = false;
			}
		}
		return retv.toString();
	}
	
	public static String decimalToStr(String format, double value) {
		
		DecimalFormat frm = new DecimalFormat(format);
		
		return frm.format(value);
		
	}
	
	/**
	 * 쿼리 스트링을 히든 테그로 만든다.
	 * @param param
	 * @return
	 */
	public static String paramToInputTag(String param) {
		
		StringBuffer sb = new StringBuffer();
		String el = null;
		int index = 0;
		
		param = Util.toStr(param);
		if(param.equals("")) return sb.toString();
		
		String[] params = Util.ReplaceAll(param,"&amp;","&" ).split("&");
		if(params != null && param.length() > 0) {
			
			for(int i=0; i<params.length; i++) {
				
				el = params[i];
				index = el.indexOf("=");
				
				if(index > -1 && index+1 < el.length()) {
					
					sb.append("<input type=\"hidden\" name=\"").append(el.substring(0,index)).append("\" value=\"")
					.append( el.substring(index+1) ).append("\"/>");
					
				}
				
				
			}
		}
		
		return sb.toString();
		
		
	}
	
	public static String unescapeHTML(String s){
		
		String retv = StringEscapeUtils.unescapeHtml(s); 
		String str = null;
		
		if(retv.indexOf("&#") > -1) {
		
			Pattern pScript = Pattern.compile("&#[0-9]{1,}");
			Matcher matcher=pScript.matcher(retv);
			StringBuffer sb = new StringBuffer();
			int esc = -1;
			
			while(matcher.find()) {
				str = matcher.group();
			  esc = Integer.parseInt(str.substring(2), 10);
				matcher.appendReplacement(sb, String.valueOf((char)esc) );
			}
			
			matcher.appendTail(sb);
			
			retv =  sb.toString();
		}
		
		if(retv.indexOf("&") > -1) {
			
			Pattern pScript = Pattern.compile("&[a-zA-Z0-9]{2,8}");
			Matcher matcher = pScript.matcher(retv);
			StringBuffer sb2 = new StringBuffer();
			String n = null;
			int i = 0;
			
			while(matcher.find()) {
				str = matcher.group();
				for(i=str.length(); i>=3; i--) {
					n = StringEscapeUtils.unescapeHtml(str.substring(0,i) +";");
					if(n.length() == 1) { 
						//System.out.println(n + str.substring(i));
						matcher.appendReplacement(sb2, n + str.substring(i) );
						break;
					}
				}
				
			}
			
			matcher.appendTail(sb2);
			
			retv = sb2.toString();
		}
		
		
		return retv;
		
	}
	
	public static void testEncoding(String str, int depth) throws UnsupportedEncodingException {
		
		depth --;
		String[] set = {"UTF-8","KSC5601","8859_1","EUC-KR"};
		System.out.println("origin:"+str);
		for(int i=0; i<set.length; i++) {
			for(int j=0; j<set.length; j++) {
				String tmp = new String(str.getBytes(set[i]),set[j]);
				System.out.println(depth+":"+set[i]+"->"+set[j]+":"+tmp);
				if(depth >= 0) {
					testEncoding(tmp,depth);
				}
			}
		}
		
		
		
	}
	
	public static String unescapeJwxe(String text) {
		
		String rslt = "";
		if(text == null) return rslt;
		
		BASE64Decoder decoder = new BASE64Decoder();
		
		try {
			rslt = new String(decoder.decodeBuffer(text),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rslt;
		
		/*
		text = StringUtils.replace(text, "jWxlTcHR", "<");
		text = StringUtils.replace(text, "jWxgTcHR", ">");

		text = StringUtils.replace(text, "jWxLcHR", "=");
		
		text = StringUtils.replace(text, "jWxacHR", "s");
		text = StringUtils.replace(text, "jWxbcHR", "S");
		text = StringUtils.replace(text, "jWxccHR", "i");
		text = StringUtils.replace(text, "jWxdcHR", "I");
		text = StringUtils.replace(text, "jWxecHR", "f");
		text = StringUtils.replace(text, "jWxgcHR", "F");
		text = StringUtils.replace(text, "jWxhcHR", "u");
		text = StringUtils.replace(text, "jWxjcHR", "U");
		text = StringUtils.replace(text, "jWxkcHR", "1");
		*/
		
		
		//return text;
	}
	
	public static void alertScript(JspWriter out, String msg) throws IOException {
		alertScript(out, msg, null);
	}	
	
	public static void alertScript(JspWriter out, String msg, String returnURL) throws IOException {
		
		try {
			try {
				out.clearBuffer();
			} catch (Exception e) {}
			
			if(returnURL != null) {
				out.print("<html><head><meta http-equiv=\"Refresh\" content=\"0; URL="+returnURL.replaceAll("\"", "%22")+"\">");
			}
			out.println("<script type=\"text/javascript\">");
			out.println("//<![CDATA[");
			out.println("alert('"+ StringEscapeUtils.escapeJavaScript(msg)+"');");
			if(returnURL == null) {
				out.println("document.location.href = document.referrer;");
			} else {
				out.println("document.location.href = '"+StringEscapeUtils.escapeJavaScript(returnURL)+"';");
			}
			out.println("document.close();");
			out.println("//]]>");
			out.println("</script></head><body>");
			out.println("<noscript>");
			out.println(Util.HTMLEncoding(msg));
			if(Util.toStr(returnURL).startsWith("http")) {
				out.println("<a href=\""+returnURL.replaceAll("\"", "%22")+"\" title=\"이동하기\">이전 페이지로 이동</a>");
			}
			out.println("</noscript></body></html>");
			out.flush();
			//out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}	
	
	public static void alertScript(HttpServletResponse res, String msg) throws IOException {
		alertScript(res, msg, null);
	}	
	
	public static void alertScript(HttpServletResponse res, String msg, String returnURL) throws IOException {
		
		PrintWriter out = null;
		try {
			try {
				res.resetBuffer();
			} catch (Exception e) {}
			out = res.getWriter();
			if(returnURL != null) {
				out.print("<html><head><meta http-equiv=\"Refresh\" content=\"0; URL="+returnURL.replaceAll("\"", "%22")+"\">");
			}
			out.println("<script type=\"text/javascript\">");
			out.println("//<![CDATA[");
			out.println("alert('"+ StringEscapeUtils.escapeJavaScript(msg)+"');");
			if(returnURL == null) {
				out.println("document.location.href = document.referrer;");
			} else {
				out.println("document.location.href = '"+StringEscapeUtils.escapeJavaScript(returnURL)+"';");
			}
			out.println("document.close();");
			out.println("//]]>");
			out.println("</script></head><body>");
			out.println("<noscript>");
			out.println(Util.HTMLEncoding(msg));
			if(Util.toStr(returnURL).startsWith("http")) {
				out.println("<a href=\""+returnURL.replaceAll("\"", "%22")+"\" title=\"이동하기\">이전 페이지로 이동</a>");
			}
			out.println("</noscript></body></html>");
			out.flush();
			//out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	
	public static String getCurrentURI(HttpServletRequest req) {
		String query= "";
		if(req.getQueryString() != null){
			query = req.getRequestURI()+"?"+req.getQueryString();
		}else {
			query = req.getRequestURI();
		}
		return query;		
	}
	
	public static String getCurrentPageURL(HttpServletRequest req) {
		return getCurrentHost(req) + getCurrentURI(req);
	}
	
	
	public static String getCurrentHost(HttpServletRequest req) {
		return req.getScheme()+"://"+req.getHeader("host");		
	}
	
	
	/**
	 * 마이크로 타임 날짜를 초단위 날짜까지 자른다.
	 * @param datetime
	 * @return
	 */
	public static String toDateTimeString(String datetime) {
		
		if(datetime == null) return "";
		
		if(datetime.length() >= 19 ) {
			return datetime.substring(0,19); 
		} else {
			return datetime;
		}
			
	}
	
	
	public static String logDateStrToShort(String dateStr ) {
		return logDateStrToShort(dateStr, 10);
	}
	
	public static String logDateStrToShort(String dateStr, int length ) {
		
		if(dateStr == null) return "";
		
		if(dateStr.length() > length ) {
			return dateStr.substring(0,length); 
		} else {
			return dateStr;
		}
		
	}

	
	/**
	 * jsp:include 일때 sendredirect가 제대로 작동하지 않으므로 meta 를 이용하여 redirect 한다 
	 * @param res
	 * @param url
	 */
	public static void redirect(HttpServletResponse res, String url) {

		PrintWriter out = null;
		try {
			url = url.replaceAll("\"", "%22");
			try {
				res.resetBuffer();
			} catch (Exception e) {}
			
			res.setStatus(302);
			res.setHeader("Location", url);
			res.setHeader("Connection","close");

			try {
				out = res.getWriter();
				out.print("<html><head><meta http-equiv=\"Refresh\" content=\"0; URL="+url.replaceAll("\"", "%22")+"\">");
				out.println("<script type=\"text/javascript\">");
				out.println("//<![CDATA[");
				out.println("document.location.href = '"+StringEscapeUtils.escapeJavaScript(url)+"';");
				out.println("document.close();");
				out.println("//]]>");
				out.println("</script></head></html>");
				out.flush();
				//out.close();
			} catch (Exception e) {}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
			
		
	}
	
	/**
	 * xml 을 json 으로
	 * @param xmlStr
	 * @return
	 */
	public static org.json.simple.JSONObject xmlStrToJson( String xmlStr  ) {
		
		net.sf.json.xml.XMLSerializer xmlSerializer = new net.sf.json.xml.XMLSerializer(); 
		net.sf.json.JSON xjson = xmlSerializer.read( xmlStr );
		
		org.json.simple.JSONObject json = (org.json.simple.JSONObject)JSONValue.parse(xjson.toString());
		
		return json;
		
	}

	public static String cutAndaddDot(String text) {
		
		return cutAndaddDot(text, 2);
		
	}
	public static String cutAndaddDot(String text, int length ) {
	
		if(text == null) return "";
		
		if(text.length() > length) {
			StringBuffer dot = new StringBuffer(text.substring(0,text.length()-length));
			for(int i=0; i<length; i++) {
				dot.append(".");
			}
			return dot.toString();
		} else {
			return text;
		}
		
	}

	private static String[] regSearch = {".","?","^","+","$","*","[","]","|","(",")","{","}","-","\\"}; 
	private static String[] regReplace = {"\\.","\\?","\\^","\\+","\\$","\\*","\\[","\\]","\\|","\\(","\\)","\\{","\\}","\\-","\\\\"};
	public static String escapeRegex(String reg) {
		
		for(int i=0; i<regSearch.length; i ++) {
			reg = StringUtils.replace(reg, regSearch[i], regReplace[i]);	
		}
		
		return reg;
		
		//return StringUtils.replaceEach(reg, search, replace);
		
	}
	

    public static void testEncode(String fn) throws Exception
    {
            final String encs[] = { "UTF-8", "ASCII","EUC-KR","ISO-2022-KR", "x-windows-949", "ISO-8859-1", "ksc5601", "Cp949", "Cp437" };

            for(int a=0; a<encs.length; a++)
            {
                    for(int b=0; b<encs.length; b++)
                    {
                            String s = new String(fn.getBytes(encs[a]), encs[b]);
                            System.out.println(fn + "(" + encs[a] + "->" + encs[b] + "):" + s);
                    }
            }
    }
    
    /**
     * 쿼리 스트링을 맵으로 변환
     * @param query
     * @return
     */
    public static Map getQueryMap(String query)  
    {  

        Map map = new HashMap();
        if(query == null) {
        	return map;
        }
    	
    	String[] params = query.split("&");
        String[] split = null;
        String name, value, param;
        for( int i=0; params != null && i< params.length; i++)  
        {  
        	param = params[i].trim();
        	if(param.indexOf("=") > -1) {
        		split = param.split("=");
        		if(split != null && split.length == 2) {
	        		name = split[0];  
	        		value = split[1];
	                map.put(name, value);  
        		}
        	}
        }  
        return map;  
    }
    
    /**
     * 파일명 중 확장자를 구한다.
     * @param fileNm
     * @param defaultExt
     * @return
     */
    public static String getFileExt(String fileNm) {
    	
    	return getFileExt(fileNm,"");
    	
    }
    public static String getFileExt(String fileNm, String defaultExt) {
    	
    	fileNm = toStr(fileNm);
    	int index = fileNm.lastIndexOf(".");
    	if(index == -1) {
    		return defaultExt;
    	}
    	
    	return fileNm.substring(index+1).toLowerCase();
    	
    }
	
}
