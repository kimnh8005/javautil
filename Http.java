package md.util;

import java.io.*;
import java.net.*;

public class Http {
	
	public static String getHttpXMLStr( String url ) throws IOException {
		
		return getHttpStr(url, "utf-8");
	}
	
	public static String getHttpStr( String url, String charset ) throws IOException {
		
		HttpURLConnection con = null;
		StringBuffer resultString = new StringBuffer();

		try {
			URL urlObj = new URL(url);
			con = (HttpURLConnection)urlObj.openConnection();
			con.setAllowUserInteraction(false);
	    
			// 전송 후 서버에서 받아 온 결과값 저장
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(),charset));
				String str = null;
				while((str = br.readLine()) != null) {
					resultString.append(str).append("\n");
				}
			} finally {
				if(br != null) br.close();
			}
			
			//net.sf.json.xml.XMLSerializer xmlSerializer = new net.sf.json.xml.XMLSerializer(); 
			//net.sf.json.JSON xjson = xmlSerializer.read( resultString.toString() );
			
		} finally {
			con.disconnect();
		}
		return resultString.toString();
		
	}
	
}
