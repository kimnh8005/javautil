package md.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.oreilly.servlet.MultipartRequest;
import md.cms.CMS;
import md.database.RDBMS;

public class FilterInjectionAttack {
	
	
	private static Set badWords = new HashSet();
	private static String sp = "\\s*";
	private static String sPattern = "((?i).*<\\s*(script|javascript|vbscript).*>.+<\\s*/\\s*(script|javascript|vbscript)?\\s*>.*)"
		+ "|((?i).*<.+(=)\\s*(script|javascript|vbscript)\\s*:\\s*.*)" 
		+ "|((?i).*\\s*[^\\w]on[a-z]+\\s*=.+)";
	private static String iPattern = sPattern + "|((?i).*<\\s*iframe(.+)src\\s*=.*)" + "|((?i).*<\\s*embed(.+)src\\s*=.*)";

	
	private static Pattern pScript = Pattern.compile(sPattern,Pattern.DOTALL | Pattern.MULTILINE);
	private static Pattern iScript = Pattern.compile(iPattern,Pattern.DOTALL | Pattern.MULTILINE);

	private static Log logger = LogFactory.getLog(FilterInjectionAttack.class);

	
	static {
		initBadWord();
	}

	/**
	 * 금지 단어 테이블을 초기화한다.
	 */
	private static void initBadWord() {
		// 
		BufferedReader brIn = null;
		String line = null;
		try {
			brIn = new BufferedReader(new InputStreamReader(new FileInputStream( CMS.DIR_ROOT  + "_sys/dibChecker/ref/word.txt")));
			while((line=brIn.readLine()) != null) {
				line = line.trim();
				if(line.length() == 0)
					continue;
				badWords.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(brIn != null)
				try {
					brIn.close();
				} catch (IOException e) {
					logger.error(e);
				}
		}
		
	}
	
	/**
	 * 금칙어를 찾아서 xxxx 로 치환한다
	 * @param text
	 * @return
	 */
	public static String filterBadWord(String text) {
		
		String word = null;
		Iterator it = badWords.iterator();
		while(it.hasNext()) {
			word = (String)it.next();
			if(text.indexOf(word)>-1) {
				text = StringUtils.replace(text, word, "XXXX");
			}
		}
		return text;
	}
	
	/**
	 * request 를 검사하여 스크립트를 찾는다.
	 * @param req
	 * @return
	 */
	public static boolean checkScript(HttpServletRequest req) {
		return checkScript(req, null);
	}
	
	// parameter 와 multipart 를 모두 체크한다.
	public static boolean checkScriptAll(HttpServletRequest requestWrapper) {

		// request parameter 부터 체크
		if(!checkScript(requestWrapper)) {
			return false;
		}

		
		boolean isMultipart = ServletFileUpload.isMultipartContent(requestWrapper);
		// multipart 검사
		if(isMultipart) {
			Pattern pattern = null;
			if(CMS.getProp("board.secure.iframe", "Y").equals("Y")) { // iframe 도 필터링 할지
				pattern = iScript;
			} else {
				pattern = pScript;
			}
			boolean retv = false;
			String key, value = null;
			
			try {
				
				
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(requestWrapper);
				while (iter.hasNext()) {
					 FileItemStream item = iter.next();
					 
					 if (item.isFormField()) {
						key = item.getFieldName();
					 	InputStream stream = item.openStream();
					 	value = Streams.asString(stream);
						retv = pattern.matcher(value).matches();
						if(retv) {
							injectionLog(key,value,requestWrapper);
							requestWrapper.setAttribute("injection", "Y");
							return false;
						}

						// 인코딩된 스크립트를 막는다.
						if(value.indexOf("&") > -1) {
							value = Util.unescapeHTML(value);
							//System.out.println("key:"+key+",value:"+value);
							retv = pattern.matcher(value).matches();
							if(retv) {
								injectionLog(key,value,requestWrapper);
								requestWrapper.setAttribute("injection", "Y");
								return false;
							}
						}
					 	
					 }
				}
			} catch (Exception e ) {
				logger.error(e);
			}
			
		}
		
		return true;
		
	}
	
	
	public static boolean checkScript(HttpServletRequest req, MultipartRequest multi) {
		
		if(req.getAttribute("jwxe_chk_injection") != null) // 중복 체크를 막는다
			return true;
		
		req.setAttribute("jwxe_chk_injection","Y"); // 중복 체크를 막기 위해서
		
		boolean retv = false;
		String key = null, decode = null, value = null;;
		Pattern pattern = null;
		
		if(CMS.getProp("board.secure.iframe", "Y").equals("Y")) { // iframe 도 필터링 할지
			pattern = iScript;
		} else {
			pattern = pScript;
		}
		
		Enumeration en = req.getParameterNames();
		while(en.hasMoreElements()) {
			// 스크립트를 막는다.
			key = (String)en.nextElement();
			value = req.getParameter(key);
			//System.out.println("key:"+key+",value:"+value);
			retv = pattern.matcher(value).matches();
			if(retv) {
				injectionLog(key,value,req);
				req.setAttribute("injection", "Y");
				return false;
			}

			// 인코딩된 스크립트를 막는다.
			if(value.indexOf("&") > -1) {
				value = Util.unescapeHTML(value);
				//System.out.println("key:"+key+",value:"+value);
				retv = pattern.matcher(value).matches();
				if(retv) {
					injectionLog(key,value,req);
					req.setAttribute("injection", "Y");
					return false;
				}
			}
		}
		
		
		if(multi != null) { // multipart
			
	        Enumeration enm = multi.getParameterNames();
	        while (enm.hasMoreElements()) {
	          key = (String) enm.nextElement();
	          value = multi.getParameter(key);
				retv = pattern.matcher(value).matches();
				if(retv) {
					injectionLog(key,value,req);
					req.setAttribute("injection", "Y");
					return false;
				}
	
				// 인코딩된 스크립트를 막는다.
				if(value.indexOf("&") > -1) {
					value = Util.unescapeHTML(value);
					//System.out.println("key:"+key+",value:"+value);
					retv = pattern.matcher(value).matches();
					if(retv) {
						injectionLog(key,value,req);
						req.setAttribute("injection", "Y");
						return false;
					}
				}
	        }
			
			
		}
		
		return true;
	}
	
	private static void injectionLog(String key, String value, HttpServletRequest req) {
		StringBuffer sb = new StringBuffer();
		sb.append("=============================================================================\n")
		.append("INJECTION ATTACK : ").append(Util.getCurDate("yyyy-MM-dd hh:mm:ss")).append("\n")
		.append("page: ").append(req.getRequestURI()).append("\n")
		.append("referer: ").append(req.getHeader("referer")).append("\n")
		.append("locale: ").append(req.getLocale().toString()).append("\n")
		.append("agent : ").append(req.getHeader("User-Agent")).append("\n")
		.append("ip : ").append(getRemoteAddr(req)).append("\n")
		.append("key : ").append(key).append("\n")
		.append("value : ").append(value).append("\n")
		.append("=============================================================================");
		
		logger.error(sb);
		sb.setLength(0);
	}
	
	private static String getRemoteAddr(HttpServletRequest req) {
		String ip = CMS.getProp("jwxe.remote.forward","N").equals("N")? req.getRemoteAddr(): req.getHeader("X-Forwarded-For");
		if(ip == null) {
			ip = req.getRemoteAddr();
		}
		return ip;
	}
}