package com.ap.beautypoint.member.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.HttpException;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ap.beautypoint.common.util.PropertyHandler;
import com.ap.beautypoint.common.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import common.SNameCheck;

@SlingServlet(
		paths = {"/bin/beautypoint/external"}, 
		methods = {"POST"}, 
		selectors = {"auth","smsreq","smsres","smsreq2"},
		metatype = true
)
public class AuthServlet extends SlingAllMethodsServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(MemberServlet.class);

	private String getToday(){
		Calendar today = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String day = sdf.format(today.getTime());
		return day;
	}

	private String createRandomNum(int length){
		Random ran = new Random();
		// 	랜덤 문자 길이
		String randomStr = "";
		for (int i = 0; i < length; i++) {
			randomStr += ran.nextInt(10);		// 	    0 ~ 9 랜덤 숫자 생성
		}
		return randomStr;
	}

	//핸드폰번호 + 생년월일 + 성별 + 성명을 이용하여 CINO생성(Hash 코드)
	public String CinoHash(String str) {
		String rtnSHA = "";

		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			rtnSHA = sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			rtnSHA = null;
		}
		return rtnSHA;
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws HttpException, IOException {

		Gson gson = new Gson();
		Map<String, Object> params = gson.fromJson(request.getReader(), new TypeToken<Map<String, Object>>(){}.getType());
		if (request.getRequestURI().equals("/bin/beautypoint/external.auth")) {
			String name = URLDecoder.decode(StringUtil.nullTrim((String)params.get("name")),"UTF-8");
			String mobileNo1 = StringUtil.nullTrim((String)params.get("mobileNo1"));
			String mobileNo2 = StringUtil.nullTrim((String)params.get("mobileNo2"));
			String mobileNo3 = StringUtil.nullTrim((String)params.get("mobileNo3"));
			String mobileNo = mobileNo1+mobileNo2+mobileNo3;
			String birthYear = StringUtil.nullTrim((String)params.get("birthYear"));
			String birthMonth = StringUtil.nullTrim((String)params.get("birthMonth"));
			String birthDay = StringUtil.nullTrim((String)params.get("birthDay"));
			String nation = StringUtil.nullTrim((String)params.get("nation"));
			String gender = StringUtil.nullTrim((String)params.get("gender"));
			
			//name = new String(name.getBytes("8859-1"),"UTF-8");
			
			PrintWriter out = response.getWriter();
			JSONObject resObj = new JSONObject();
			try
			{
				// NICE 간편인증
				String res = CheckSName(name, birthYear+birthMonth+birthDay, gender);
				resObj.put("SCIRes", res);
				
				// 점유인증 (KMC)
				if( res.equals("1") )
				{
					Map<String,Object> result = CheckKCM(name, mobileNo, birthYear, birthMonth, birthDay, nation, gender);
					if( "Y".equals(result.get("result"))  )
					{
						resObj.put("result", "OK");
						resObj.put("authNo", result.get("authNo"));
						resObj.put("name", name);
						resObj.put("mobileNo2", result.get("mobileNo2"));
						resObj.put("mobileNo3", result.get("mobileNo3"));
						resObj.put("mobileNo3", result.get("mobileNo3"));
						resObj.put("birthYear", result.get("birthYear"));
						resObj.put("birthMonth", result.get("birthMonth"));
						resObj.put("birthDay", result.get("birthDay"));
						resObj.put("nation", result.get("nation"));
						resObj.put("gender", result.get("gender"));
						resObj.put("CI", result.get("CI"));
						out.print(resObj);
						out.flush();
						out.close();
						return;
					}
				}

				resObj.put("result", "NOK");
			}catch(Exception ex){
				ex.printStackTrace();
			}
			out.print(resObj);
			out.flush();
			out.close();
		}
		else if (request.getRequestURI().equals("/bin/beautypoint/external.smsreq")) {
			String name = URLDecoder.decode(StringUtil.nullTrim((String)params.get("name")),"utf-8");
			String mobileCorp = StringUtil.nullTrim((String)params.get("mobileCorp"));
			String mobileNo1 = StringUtil.nullTrim((String)params.get("mobileNo1"));
			String mobileNo2 = StringUtil.nullTrim((String)params.get("mobileNo2"));
			String mobileNo3 = StringUtil.nullTrim((String)params.get("mobileNo3"));
			String mobileNo = mobileNo1+mobileNo2+mobileNo3;
			String birthYear = StringUtil.nullTrim((String)params.get("birthYear"));
			String birthMonth = StringUtil.nullTrim((String)params.get("birthMonth"));
			String birthDay = StringUtil.nullTrim((String)params.get("birthDay"));
			String nation = StringUtil.nullTrim((String)params.get("nation"));
			String gender = StringUtil.nullTrim((String)params.get("gender"));
			
			String localIp = request.getLocalAddr();
			String port = String.valueOf(request.getLocalPort());
			logger.debug("###################$$$$$$$$ "+(String)params.get("name"));
			logger.debug("###################aaaaaaaa "+URLDecoder.decode(StringUtil.nullTrim((String)params.get("name")),"utf-8"));
			PrintWriter out = response.getWriter();
			JSONObject resObj = new JSONObject();
			try{
				name = new String(name.getBytes("euc-kr"), "iso-8859-1");
				logger.debug("###################bbbbbbbbbb "+name);
				Map<String,Object> result = CheckKCMReq( localIp, port, name, mobileCorp, mobileNo, birthYear, birthMonth, birthDay, nation, gender );
				resObj.put("result", result.get("result"));
				resObj.put("resultCode", result.get("resultCode"));
				resObj.put("check_1", result.get("check_1"));
				resObj.put("check_2", result.get("check_2"));
				resObj.put("check_3", result.get("check_3"));
				out.print(resObj);
				out.flush();
				out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if (request.getRequestURI().equals("/bin/beautypoint/external.smsres")) {
			String check_1 = StringUtil.nullTrim((String)params.get("check_1"));
			String check_2 = StringUtil.nullTrim((String)params.get("check_2"));
			String check_3 = StringUtil.nullTrim((String)params.get("check_3"));
			String authNo = StringUtil.nullTrim(String.valueOf(params.get("authNo")));
			
			String localIp = request.getLocalAddr();
			String port = String.valueOf(request.getLocalPort());

			PrintWriter out = response.getWriter();
			JSONObject resObj = new JSONObject();
			try{
				Map<String,Object> result = CheckKCMRes(localIp, port, authNo, check_1, check_2, check_3);
				resObj.put("result", result.get("result"));
				resObj.put("resultCode", result.get("resultCode"));
				resObj.put("CI", result.get("CI"));
				resObj.put("DI", result.get("DI"));
				out.print(resObj);
				out.flush();
				out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if (request.getRequestURI().equals("/bin/beautypoint/external.smsreq2")) {
			String check_1 = StringUtil.nullTrim((String)params.get("check_1"));
			String check_2 = StringUtil.nullTrim((String)params.get("check_2"));
			String check_3 = StringUtil.nullTrim((String)params.get("check_3"));

			String localIp = request.getLocalAddr();
			String port = String.valueOf(request.getLocalPort());

			PrintWriter out = response.getWriter();
			JSONObject resObj = new JSONObject();
			try{
				Map<String,Object> result = CheckKCMReq2(localIp, port, check_1, check_2, check_3);
				resObj.put("result", result.get("result"));
				resObj.put("resultCode", result.get("resultCode"));
				resObj.put("check_1", result.get("check_1"));
				resObj.put("check_2", result.get("check_2"));
				resObj.put("check_3", result.get("check_3"));
				out.print(resObj);
				out.flush();
				out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	private String CheckSName(String name, String birthDay, String gender) throws Exception{
		try{
			String SITECODE = PropertyHandler.getConfigByProp("SNAME_SITECODE");
			String SITEPW = PropertyHandler.getConfigByProp("SNAME_SITEPW");
			SNameCheck NC = new SNameCheck();
			logger.debug("name:{}",name);
			logger.debug("birthDay:{}",birthDay.substring(2));
			logger.debug("SITECODE:{}",SITECODE);
			logger.debug("SITEPW:{}",SITEPW);
			logger.debug("gender:{}",gender);
			String Rtn = NC.fnNameCheck(SITECODE, SITEPW, birthDay.substring(2), name);  
			logger.debug("Rtn_length:"+Rtn.length());
			logger.debug("Rtn:"+Rtn.toString());
		return Rtn;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.debug(ex.getMessage());
		}
		return "";
	}
	
	private Map<String,Object> CheckKCM(String name, String mobileNo, String birthYear, String birthMonth, String birthDay, String nation, String gender){
		
		Map<String,Object> res = new HashMap<String,Object>();
		
		res.put("result", "N");

		String tr_cert   = "";

		//날짜 생성
		String day = getToday();

		String randomStr = createRandomNum(6);

		URL url = null;
		BufferedReader in = null;
		URLConnection con = null;

		String rec_cert = "";

		//01. 한국모바일인증(주) 암호화 모듈 선언
		com.icert.comm.secu.IcertSecuManager seed  = new com.icert.comm.secu.IcertSecuManager();  

		String certNo    = day + randomStr;		//인증번호
		String reqDate   = day;						//요청일시
		String phoneNo   = mobileNo;				//휴대폰번호
		String authNo    = "";							//SMS승인번호
		String extendVar = "0000000000000000";	//확장변수        
		try {
			//입력값 변수--------------------------------------------------------------------------------------------                    
			String cpId      = PropertyHandler.getConfigByProp("KMC_SMS_ID");						//점유인증 회원사ID
			String urlCode   = PropertyHandler.getConfigByProp("KMC_SMS_URLCODE_WEB");				//점유인증 URL코드
	
			//-------------------------------------------------------------------------------------------------------
			/** 요청번호(certNum) 주의사항 ****************************************************************************
			 * 1. 본인인증 결과값 복호화를 위한 키로 활용되므로 중요함.
			 * 2. 본인인증 요청시 중복되지 않게 생성해야함. (예-시퀀스번호)
			 ***********************************************************************************************************/		
			logger.debug("cpId		:"+cpId);
			logger.debug("urlCode		:"+urlCode);
			logger.debug("certNo		:"+certNo);
			logger.debug("reqDate		:"+reqDate);
			logger.debug("phoneNo		:"+phoneNo);
			logger.debug("authNo		:"+authNo);		//sms 승인번호
			logger.debug("extendVar	:"+extendVar);
	
	
			//02. 1차 암호화 (tr_cert 데이터변수 조합 후 암호화)
			String enc_tr_cert = "";
			tr_cert     = cpId +"/"+ urlCode +"/"+ certNo +"/"+ reqDate +"/"+ phoneNo +"/"+ authNo +"/"+ extendVar;
			enc_tr_cert = seed.getEnc(tr_cert, "");
	
			//03. 1차 암호화 데이터에 대한 위변조 검증값 생성 (HMAC)
			com.icert.comm.secu.hmac.IcertHmac hmac = new com.icert.comm.secu.hmac.IcertHmac(); 
			String hmacMsg = hmac.HMacEncript(enc_tr_cert);
	
			//04. 2차 암호화 (1차 암호화 데이터, HMAC 데이터, extendVar 조합 후 암호화)
			tr_cert  = seed.getEnc(enc_tr_cert +"/"+ hmacMsg +"/"+ extendVar, "");  
	
			//05. SMS사용자인증 요청 URL로 암호화 데이터 넘기기        
			String send_url = PropertyHandler.getConfigByProp("KMC_SMS_URL")+"?tr_cert="+tr_cert;	//로컬
	
			//06. URL로 암호화 값 보내고 암호화된 결과 받기
			String urlstr   = send_url;

			url = new URL(urlstr);
			con = url.openConnection();
			con.connect();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String tmp_msg = "";
			while ((tmp_msg = in.readLine()) != null) {
				if (!"".equals(tmp_msg)) {
					rec_cert = tmp_msg;
				}
			}
		} catch (MalformedURLException malformedurlexception) {
			malformedurlexception.printStackTrace();
			logger.error(malformedurlexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);

		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			logger.error(ioexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex3) {
				ex3.printStackTrace();
				logger.error(ex3.toString());
			}
		}

		try {
			//결과값 변수--------------------------------------------------------------------------------------------
			String encPara      = "";	//암호화된 통합 파라미터
			String encMsg1      = "";	//암호화된 통합 파라미터의 Hash값
			String encMsg2      = "";	//암호화된 통합 파라미터의 Hash값 비교를 위한 Hash값

			String r_certNo		= "";	//인증번호
			String r_reqDate    = "";	//요청일시
			String r_result     = "";		//인증결과값 
			String r_resultCode = "";	//인증결과코드
			String r_phoneNo    = "";	//휴대폰번호
			String r_authNo		= "";	//SMS승인번호 
			//-------------------------------------------------------------------------------------------------------			

			//07. 1차 복호화 (인증번호를 이용해 복호화)
			rec_cert = seed.getDec(rec_cert, "");  

			int inf1 = rec_cert.indexOf("/",0);
			int inf2 = rec_cert.indexOf("/",inf1+1);

			encPara = rec_cert.substring(0, inf1);      
			encMsg1 = rec_cert.substring(inf1+1, inf2); 

			//08. 1차 복호화 데이터에 대한 위변조 검증값 검증
			encMsg2 = seed.getMsg(encPara);

			if(!encMsg1.equals(encMsg2)){
				//vo.setKmcis_error("비정상적인 접근입니다.");
				return res;
			}            

			//09. 2차 복호화 (인증번호를 이용해 복호화)
			rec_cert  = seed.getDec(encPara, "");  

			logger.debug("<CheckKCM>== rec_cert ==>"+rec_cert);
			
			int info1  = rec_cert.indexOf("/",0);
			int info2  = rec_cert.indexOf("/",info1+1);
			int info3  = rec_cert.indexOf("/",info2+1);
			int info4  = rec_cert.indexOf("/",info3+1);
			int info5  = rec_cert.indexOf("/",info4+1);
			int info6  = rec_cert.indexOf("/",info5+1);

			r_certNo	 = rec_cert.substring(0,info1);            
			r_reqDate    = rec_cert.substring(info1+1,info2);      
			r_result     = rec_cert.substring(info2+1,info3);      
			r_resultCode = rec_cert.substring(info3+1,info4);      
			r_phoneNo    = rec_cert.substring(info4+1,info5);      
			r_authNo     = rec_cert.substring(info5+1,info6);            

			String cino = "";
			String cicode = "";			

			//cino 생성...
			if(r_result.equals("Y")){ 					//본인인증 성공하였습니다
				//해쉬코드 생성하기 위해 성별 변환
				if (nation.equals("0")) { //내국인
					if (birthYear.substring(0, 2).equals("19")) {
						if (gender.equals("0")) {//남자
							gender = "1";	
						} else {
							gender = "2";
						}
					} else if (birthYear.substring(0, 2).equals("20")) {
						if (gender.equals("0")) { //남자
							gender = "3";	
						} else {
							gender = "4";
						}
					}
				} else if (nation.equals("1")) { //외국인
					if (birthYear.substring(0, 2).equals("19")) {
						if (gender.equals("0")) {//남자
							gender = "5";	
						} else {
							gender = "6";
						}
					} else if (birthYear.substring(0, 2).equals("20")) {
						if (gender.equals("0")) {//남자
							gender = "7";	
						} else {
							gender = "8";
						}
					}
				}

				String rtnTest = new String(name.getBytes("euc-kr"), "iso-8859-1");
				String nameTest = new String(name.getBytes("8859-1"),"UTF-8");
				String nameTest1 = 	URLDecoder.decode(StringUtil.nullTrim(name),"UTF-8");
				name = new String(name.getBytes("euc-kr"), "iso-8859-1");
				
				
				int len = name.getBytes().length;
				

				String rtnName = name;
			
				
				if(len < 40)
				{
					for(int i=0; i< (40-len); i++)
					{
						rtnName = rtnName + " ";
					}
				}
				name = rtnName;
			
				cicode = phoneNo + birthDay + gender + name;
				
				String cihash = CinoHash(cicode);
				cino = cihash + cihash.substring(0,24);

				logger.debug("cicode==============>" + cicode);
				logger.debug("cihash==============>" + cihash);
				logger.debug("name==============>" + name);
				logger.debug("cino==============>" + cino);
				logger.debug("length==============>" + cino.length());
			}	

			logger.debug("r_certNo		:	" + r_certNo);
			logger.debug("r_reqDate		:	" + r_reqDate);
			logger.debug("r_result		:	" + r_result);
			logger.debug("r_resultCode	:	" + r_resultCode);
			logger.debug("r_phoneNo		:	" + r_phoneNo);

			logger.debug("r_authNo		:	" + r_authNo);			

			
			res.put("result", r_result);				
			res.put("resultCode", r_resultCode);	
			res.put("authNo", r_authNo);
			res.put("CI", cino);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("[ICS] Receive Error -" + ex.getMessage());
		}
		
		return res;
	}
	
	// 본인 인증 SMS 요청
	private Map<String,Object> CheckKCMReq(String localIp, String port, String name, String mobileCorp, String mobileNo, 
														String birthYear, String birthMonth, String birthDay, String nation, String gender){
		Map<String,Object> res = new HashMap<String,Object>();
		URL url = null;
		BufferedReader in = null;
		URLConnection con = null;
		
		String rec_cert = "";
		
		res.put("result", "E");

		String tr_cert   = "";

		//날짜 생성
		String day = getToday();

		String randomStr = createRandomNum(6);
		
		//01. 한국모바일인증(주) 암호화 모듈 선언
		com.icert.comm.secu.IcertSecuManager seed  = new com.icert.comm.secu.IcertSecuManager();  
		String certNo    = day + randomStr;		//인증번호

		try {
			//입력값 변수--------------------------------------------------------------------------------------------                    
			String cpId      = PropertyHandler.getConfigByProp2(localIp, port, PropertyHandler.PROP_FILE_BASE, "KMC_REQ_ID");				//점유인증 회원사ID
			String urlCode   = PropertyHandler.getConfigByProp2(localIp, port, PropertyHandler.PROP_FILE_BASE, "KMC_REQ_URLCODE_WEB");					//점유인증 URL코드
			String reqDate   = day;						//요청일시
			String phoneNo   = mobileNo;				//휴대폰번호
			String phoneCorp   = mobileCorp;				//통신사 SKT, KTF, LGT, SKM
			String birthDate = birthYear+birthMonth+birthDay;
			String extendVar = "0000000000000000";	//확장변수        

			//-------------------------------------------------------------------------------------------------------
			/** 요청번호(certNum) 주의사항 ****************************************************************************
			 * 1. 본인인증 결과값 복호화를 위한 키로 활용되므로 중요함.
			 * 2. 본인인증 요청시 중복되지 않게 생성해야함. (예-시퀀스번호)
			 ***********************************************************************************************************/		
			logger.debug("cpId			:"+cpId);
			logger.debug("urlCode		:"+urlCode);
			logger.debug("certNo		:"+certNo);
			logger.debug("reqDate		:"+reqDate);
			logger.debug("phoneNo		:"+phoneNo);
			logger.debug("phoneCorp		:"+phoneCorp);
			logger.debug("birthDate		:"+birthDate);
			logger.debug("name		:"+name);
			logger.debug("gender		:"+gender);
			logger.debug("nation		:"+nation);
			logger.debug("extendVar	:"+extendVar);
	
	
			//02. 1차 암호화 (tr_cert 데이터변수 조합 후 암호화)
			String enc_tr_cert = "";
			tr_cert     = cpId +"/"+ urlCode +"/"+ certNo +"/"+ reqDate +"/"+ phoneNo +"/"+ phoneCorp +"/"+ birthDate +"/"+ gender +"/"+ nation +"/"+ name +"/"+ extendVar;
			enc_tr_cert = seed.getEnc(tr_cert, "");

			//03. 1차 암호화 데이터에 대한 위변조 검증값 생성 (HMAC)
			com.icert.comm.secu.hmac.IcertHmac hmac = new com.icert.comm.secu.hmac.IcertHmac(); 
			String hmacMsg = hmac.HMacEncript(enc_tr_cert);
	
			//04. 2차 암호화 (1차 암호화 데이터, HMAC 데이터, extendVar 조합 후 암호화)
			tr_cert  = seed.getEnc(enc_tr_cert +"/"+ hmacMsg +"/"+ extendVar, "");  

			//05. SMS사용자인증 요청 URL로 암호화 데이터 넘기기        
			String send_url = PropertyHandler.getConfigByProp("KMC_REQ_URL")+"?tr_cert="+tr_cert;

			//06. URL로 암호화 값 보내고 암호화된 결과 받기
			String urlstr   = send_url;

			url = new URL(urlstr);
			
			con = url.openConnection();
			con.connect();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String tmp_msg = "";
			while ((tmp_msg = in.readLine()) != null) {
				if (!"".equals(tmp_msg)) {
					rec_cert = tmp_msg;
				}
			}
			
		} catch (MalformedURLException malformedurlexception) {
			malformedurlexception.printStackTrace();
			logger.error(malformedurlexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);

		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			logger.error(ioexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex3) {
				ex3.printStackTrace();
				logger.error(ex3.toString());
			}
		}

		try {
			//결과값 변수--------------------------------------------------------------------------------------------
            String encPara      = ""; //암호화된 통합 파라미터
            String encMsg1      = ""; //암호화된 통합 파라미터의 Hash값
			String encMsg2      = ""; //암호화된 통합 파라미터의 Hash값 비교를 위한 Hash값
			String msgChk       = "";

			String r_certNum    = ""; //요청번호
			String r_date       = ""; //요청일시
			String r_phoneNo	= ""; //휴대폰번호
			String r_phoneCorp	= ""; //이통사
			String r_birthDay	= ""; //생년월일
			String r_gender		= ""; //성별
			String r_nation     = ""; //내국인
			String r_name       = ""; //성명
			String r_result     = ""; //인증결과값
			String r_resultCode = ""; //인증결과코드
			String r_check_1    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 1(수정불가)
			String r_check_2    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 2(수정불가)
			String r_check_3    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 3(수정불가)
			//-------------------------------------------------------------------------------------------------------

			//07. rec_cert 정상/비정상 체크
			if(rec_cert.length() == 8){
				res.put("resultCode", rec_cert);		
				return res;
			}else{
				// 정상 결과값 처리 부분 -------------------------------------------------------------------------

				//08. 1차 복호화 (요청번호를 이용해 복호화)
				rec_cert = seed.getDec(rec_cert, certNo);

				int inf1 = rec_cert.indexOf("/",0);
				int inf2 = rec_cert.indexOf("/",inf1+1);

				encPara = rec_cert.substring(0, inf1);
				encMsg1 = rec_cert.substring(inf1+1, inf2);

				//09. 1차 복호화 데이터에 대한 위변조 검증값 검증
				encMsg2 = seed.getMsg(encPara);

				if(encMsg2.equals(encMsg1)){
					msgChk="Y";
				}

				if(msgChk.equals("N")){
					res.put("result", "Not Normal");	
					return res;
				}

				//10. 2차 복호화 (요청번호를 이용해 복호화)
				rec_cert  = seed.getDec(encPara, certNo);
				
				logger.debug("<CheckKCMReq>== rec_cert ==>"+rec_cert);
				
				int info1  = rec_cert.indexOf("/",0);
				int info2  = rec_cert.indexOf("/",info1+1);
				int info3  = rec_cert.indexOf("/",info2+1);
				int info4  = rec_cert.indexOf("/",info3+1);
				int info5  = rec_cert.indexOf("/",info4+1);
				int info6  = rec_cert.indexOf("/",info5+1);
				int info7  = rec_cert.indexOf("/",info6+1);
				int info8  = rec_cert.indexOf("/",info7+1);
				int info9  = rec_cert.indexOf("/",info8+1);
				int info10 = rec_cert.indexOf("/",info9+1);
				int info11 = rec_cert.indexOf("/",info10+1);
				int info12 = rec_cert.indexOf("/",info11+1);
				int info13 = rec_cert.indexOf("/",info12+1);

				r_certNum		= rec_cert.substring(0,info1);
				r_date			= rec_cert.substring(info1+1,info2);
				r_phoneNo		= rec_cert.substring(info2+1,info3);
				r_phoneCorp		= rec_cert.substring(info3+1,info4);
				r_birthDay		= rec_cert.substring(info4+1,info5);
				r_gender		= rec_cert.substring(info5+1,info6);
				r_nation		= rec_cert.substring(info6+1,info7);
				r_name			= rec_cert.substring(info7+1,info8);
				r_result		= rec_cert.substring(info8+1,info9);
				r_resultCode	= rec_cert.substring(info9+1,info10);
				r_check_1		= rec_cert.substring(info10+1,info11);
				r_check_2		= rec_cert.substring(info11+1,info12);
				r_check_3		= rec_cert.substring(info12+1,info13);
				// End - 정상 결과값 처리 부분 --------------------------------------------------------------------

				res.put("result", r_result);				
				res.put("resultCode", r_resultCode);	
				res.put("phoneNo", r_phoneNo);	
				res.put("phoneCorp", r_phoneCorp);	
				res.put("birthDay", r_birthDay);	
				res.put("gender", r_gender);	
				res.put("nation", r_nation);	
				res.put("name", r_name);	
				res.put("check_1", r_check_1);	
				res.put("check_2", r_check_2);	
				res.put("check_3", r_check_3);	
			}

        } catch (Exception ex) {
			ex.printStackTrace();
        	logger.error("[KMCIS] Receive Error -" + ex.getMessage());
        }

		return res;
	}

	// 본인 인증 결과
	private Map<String,Object> CheckKCMRes(String localIp, String port, String authNo, String check_1, String check_2, String check_3){
		
		Map<String,Object> res = new HashMap<String,Object>();
		URL url = null;
		BufferedReader in = null;
		URLConnection con = null;
		
		String rec_cert = "";
		
		res.put("result", "N");

		String tr_cert   = "";

		//날짜 생성
		String day = getToday();

		String randomStr = createRandomNum(6);
		
		//01. 한국모바일인증(주) 암호화 모듈 선언
		com.icert.comm.secu.IcertSecuManager seed  = new com.icert.comm.secu.IcertSecuManager();  
		String certNo    = day + randomStr;		//인증번호

		try {
			
			//입력값 변수--------------------------------------------------------------------------------------------                    
			String reqDate   = day;						//요청일시
			String extendVar = "0000000000000000";	//확장변수        
	
			//-------------------------------------------------------------------------------------------------------
			/** 요청번호(certNum) 주의사항 ****************************************************************************
			 * 1. 본인인증 결과값 복호화를 위한 키로 활용되므로 중요함.
			 * 2. 본인인증 요청시 중복되지 않게 생성해야함. (예-시퀀스번호)
			 ***********************************************************************************************************/			
			logger.debug("authNo		:"+authNo);
			logger.debug("check_1		:"+check_1);
			logger.debug("check_2		:"+check_2);
			logger.debug("check_3		:"+check_3);
			logger.debug("certNo		:"+certNo);
			logger.debug("reqDate		:"+reqDate);
			logger.debug("extendVar	:"+extendVar);
	
	
			//02. 1차 암호화 (tr_cert 데이터변수 조합 후 암호화)
			String enc_tr_cert = "";
			tr_cert     = certNo +"/"+ authNo +"/"+ check_1 +"/"+ check_2 +"/"+ check_3 +"/"+ extendVar;
			enc_tr_cert = seed.getEnc(tr_cert, "");
	
			//03. 1차 암호화 데이터에 대한 위변조 검증값 생성 (HMAC)
			com.icert.comm.secu.hmac.IcertHmac hmac = new com.icert.comm.secu.hmac.IcertHmac(); 
			String hmacMsg = hmac.HMacEncript(enc_tr_cert);
	
			//04. 2차 암호화 (1차 암호화 데이터, HMAC 데이터, extendVar 조합 후 암호화)
			tr_cert  = seed.getEnc(enc_tr_cert +"/"+ hmacMsg +"/"+ extendVar, "");  
	
			//05. SMS사용자인증 요청 URL로 암호화 데이터 넘기기        
	        String send_url = PropertyHandler.getConfigByProp("KMC_RES_URL")+"?tr_cert="+tr_cert;
	
			//06. URL로 암호화 값 보내고 암호화된 결과 받기
			String urlstr   = send_url;

			url = new URL(urlstr);
			con = url.openConnection();
			con.connect();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String tmp_msg = "";
			while ((tmp_msg = in.readLine()) != null) {
				if (!"".equals(tmp_msg)) {
					rec_cert = tmp_msg;
				}
			}
		} catch (MalformedURLException malformedurlexception) {
			malformedurlexception.printStackTrace();
			logger.error(malformedurlexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);

		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			logger.error(ioexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex3) {
				ex3.printStackTrace();
				logger.error(ex3.toString());
			}
		}

		try {
			//결과값 변수--------------------------------------------------------------------------------------------
            String encPara      = ""; //암호화된 통합 파라미터
            String encMsg1      = ""; //암호화된 통합 파라미터의 Hash값
			String encMsg2      = ""; //암호화된 통합 파라미터의 Hash값 비교를 위한 Hash값

			String msgChk       = "";
			String r_certNum    = ""; //요청번호
			String r_CI			= ""; //연계정보(CI)
			String r_DI			= ""; //중복가입확인정보(DI)
			String r_result     = ""; //인증결과값
			String r_resultCode = ""; //인증결과코드
			String r_check_1    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 1(수정불가)
			String r_check_2    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 2(수정불가)
			String r_check_3    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 3(수정불가)
			//-------------------------------------------------------------------------------------------------------
			
			//07. rec_cert 정상/비정상 체크
			if(rec_cert.length() == 8){
				res.put("resultCode", rec_cert);		
				return res;
			}else{
				// 정상 결과값 처리 부분 -------------------------------------------------------------------------

				//08. 1차 복호화 (요청번호를 이용해 복호화)
				rec_cert = seed.getDec(rec_cert, certNo);

				int inf1 = rec_cert.indexOf("/",0);
				int inf2 = rec_cert.indexOf("/",inf1+1);

				encPara = rec_cert.substring(0, inf1);
				encMsg1 = rec_cert.substring(inf1+1, inf2);

				//09. 1차 복호화 데이터에 대한 위변조 검증값 검증
				encMsg2 = seed.getMsg(encPara);

				if(encMsg2.equals(encMsg1)){
					msgChk="Y";
				}

				if(msgChk.equals("N")){
					res.put("result", "Not Normal");	
					return res;
				}

				//10. 2차 복호화 (요청번호를 이용해 복호화)
				rec_cert  = seed.getDec(encPara, certNo);
				
				logger.debug("<CheckKCMRes>== rec_cert ==>"+rec_cert);
				
				int info1  = rec_cert.indexOf("/",0);
				int info2  = rec_cert.indexOf("/",info1+1);
				int info3  = rec_cert.indexOf("/",info2+1);
				int info4  = rec_cert.indexOf("/",info3+1);
				int info5  = rec_cert.indexOf("/",info4+1);
				int info6  = rec_cert.indexOf("/",info5+1);
				int info7  = rec_cert.indexOf("/",info6+1);
				int info8  = rec_cert.indexOf("/",info7+1);

				r_certNum	= rec_cert.substring(0,info1);
				r_CI		= rec_cert.substring(info1+1,info2);
				r_DI		= rec_cert.substring(info2+1,info3);
				r_result	= rec_cert.substring(info3+1,info4);
				r_resultCode= rec_cert.substring(info4+1,info5);
				r_check_1	= rec_cert.substring(info5+1,info6);
				r_check_2	= rec_cert.substring(info6+1,info7);
				r_check_3	= rec_cert.substring(info7+1,info8);

				//11. CI, DI 복호화 (요청번호를 이용해 복호화)
				r_CI  = seed.getDec(r_CI, certNo);
				r_DI  = seed.getDec(r_DI, certNo);

				// End - 정상 결과값 처리 부분 --------------------------------------------------------------------
				
				res.put("result", r_result);				
				res.put("resultCode", r_resultCode);	
				res.put("check_1", r_check_1);	
				res.put("check_2", r_check_2);	
				res.put("check_3", r_check_3);	
				res.put("CI", r_CI);	
				res.put("DI", r_DI);	
				res.put("authNo", authNo);	
			}

        } catch (Exception ex) {
			ex.printStackTrace();
        	logger.error("[KMCIS] Receive Error -" + ex.getMessage());
        }

		return res;
	}

	// 본인 인증 SMS 재발송
	private Map<String,Object> CheckKCMReq2(String localIp, String port, String check_1, String check_2, String check_3){
		
		Map<String,Object> res = new HashMap<String,Object>();
		URL url = null;
		BufferedReader in = null;
		URLConnection con = null;
		
		String rec_cert = "";
		
		res.put("result", "N");

		String tr_cert   = "";

		//날짜 생성
		String day = getToday();

		String randomStr = createRandomNum(6);
		
		//01. 한국모바일인증(주) 암호화 모듈 선언
		com.icert.comm.secu.IcertSecuManager seed  = new com.icert.comm.secu.IcertSecuManager();  
		String certNo    = day + randomStr;		//인증번호

		try {
			
			//입력값 변수--------------------------------------------------------------------------------------------                    
			String reqDate   = day;						//요청일시
			String extendVar = "0000000000000000";	//확장변수        
	
			//-------------------------------------------------------------------------------------------------------
			/** 요청번호(certNum) 주의사항 ****************************************************************************
			 * 1. 본인인증 결과값 복호화를 위한 키로 활용되므로 중요함.
			 * 2. 본인인증 요청시 중복되지 않게 생성해야함. (예-시퀀스번호)
			 ***********************************************************************************************************/	
			logger.debug("check_1		:"+check_1);
			logger.debug("check_2		:"+check_2);
			logger.debug("check_3		:"+check_3);
			logger.debug("certNo		:"+certNo);
			logger.debug("reqDate		:"+reqDate);
			logger.debug("extendVar	:"+extendVar);
	
	
			//02. 1차 암호화 (tr_cert 데이터변수 조합 후 암호화)
			String enc_tr_cert = "";
			tr_cert     = certNo +"/"+ check_1 +"/"+ check_2 +"/"+ check_3 +"/"+ extendVar;
			enc_tr_cert = seed.getEnc(tr_cert, "");
	
			//03. 1차 암호화 데이터에 대한 위변조 검증값 생성 (HMAC)
			com.icert.comm.secu.hmac.IcertHmac hmac = new com.icert.comm.secu.hmac.IcertHmac(); 
			String hmacMsg = hmac.HMacEncript(enc_tr_cert);
	
			//04. 2차 암호화 (1차 암호화 데이터, HMAC 데이터, extendVar 조합 후 암호화)
			tr_cert  = seed.getEnc(enc_tr_cert +"/"+ hmacMsg +"/"+ extendVar, "");  
	
			//05. SMS사용자인증 요청 URL로 암호화 데이터 넘기기        
	        String send_url = PropertyHandler.getConfigByProp("KMC_REQ_URL2")+"?tr_cert="+tr_cert;
	
			//06. URL로 암호화 값 보내고 암호화된 결과 받기
			String urlstr   = send_url;

			url = new URL(urlstr);
			con = url.openConnection();
			con.connect();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String tmp_msg = "";
			while ((tmp_msg = in.readLine()) != null) {
				if (!"".equals(tmp_msg)) {
					rec_cert = tmp_msg;
				}
			}
		} catch (MalformedURLException malformedurlexception) {
			malformedurlexception.printStackTrace();
			logger.error(malformedurlexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);

		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			logger.error(ioexception.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			//vo.setKmcis_error("SMS사용자인증 서비스(웹통신방식) 에러 : \n"+rec_cert);
			//return vo;

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex3) {
				ex3.printStackTrace();
				logger.error(ex3.toString());
			}
		}

		try {
			//결과값 변수--------------------------------------------------------------------------------------------
            String encPara      = ""; //암호화된 통합 파라미터
            String encMsg1      = ""; //암호화된 통합 파라미터의 Hash값
			String encMsg2      = ""; //암호화된 통합 파라미터의 Hash값 비교를 위한 Hash값

			String msgChk       = "";
			String r_certNum    = ""; //요청번호
			String r_result     = ""; //인증결과값
			String r_resultCode = ""; //인증결과코드
			String r_check_1    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 1(수정불가)
			String r_check_2    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 2(수정불가)
			String r_check_3    = ""; //인증번호 확인결과 전송 및 SMS재전송 요청을 위한 파라미터 3(수정불가)
			//-------------------------------------------------------------------------------------------------------
			
			//07. rec_cert 정상/비정상 체크
			if(rec_cert.length() == 8){
				res.put("resultCode", rec_cert);		
				return res;
			}else{
				// 정상 결과값 처리 부분 -------------------------------------------------------------------------

				//08. 1차 복호화 (요청번호를 이용해 복호화)
				rec_cert = seed.getDec(rec_cert, certNo);

				int inf1 = rec_cert.indexOf("/",0);
				int inf2 = rec_cert.indexOf("/",inf1+1);

				encPara = rec_cert.substring(0, inf1);
				encMsg1 = rec_cert.substring(inf1+1, inf2);

				//09. 1차 복호화 데이터에 대한 위변조 검증값 검증
				encMsg2 = seed.getMsg(encPara);

				if(encMsg2.equals(encMsg1)){
					msgChk="Y";
				}

				if(msgChk.equals("N")){
					res.put("result", "Not Normal");	
					return res;
				}

				//10. 2차 복호화 (요청번호를 이용해 복호화)
				rec_cert  = seed.getDec(encPara, certNo);
				
				logger.debug("<CheckKCMRes>== rec_cert ==>"+rec_cert);


				int info1  = rec_cert.indexOf("/",0);
				int info2  = rec_cert.indexOf("/",info1+1);
				int info3  = rec_cert.indexOf("/",info2+1);
				int info4  = rec_cert.indexOf("/",info3+1);
				int info5  = rec_cert.indexOf("/",info4+1);
				int info6  = rec_cert.indexOf("/",info5+1);

				r_certNum    = rec_cert.substring(0,info1);
				r_result     = rec_cert.substring(info1+1,info2);
				r_resultCode = rec_cert.substring(info2+1,info3);
				r_check_1    = rec_cert.substring(info3+1,info4);
				r_check_2    = rec_cert.substring(info4+1,info5);
				r_check_3    = rec_cert.substring(info5+1,info6);

				// End - 정상 결과값 처리 부분 --------------------------------------------------------------------
				
				res.put("result", r_result);				
				res.put("resultCode", r_resultCode);	
				res.put("check_1", r_check_1);	
				res.put("check_2", r_check_2);	
				res.put("check_3", r_check_3);	
			}

        } catch (Exception ex) {
			ex.printStackTrace();
        	logger.error("[KMCIS] Receive Error -" + ex.getMessage());
        }

		return res;
	}

}
