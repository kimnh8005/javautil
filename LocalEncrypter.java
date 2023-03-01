package md.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import com.oreilly.servlet.Base64Decoder;
import com.oreilly.servlet.Base64Encoder;
import md.cms.CMS;


public class LocalEncrypter{
    //DESede 는 암호화시 사용되는 최대 키 사이즈를 지정하는 키워드임 : 관련페이지 내용 링크
    private static String algorithm = "DESede";
    private static Key    key       = null;
    private static Cipher cipher    = null;
 
    public static String returnEncryptCode(String str) throws Exception {
        byte [] encryptionBytes = null;
       
        setUp();
              
        // 입력받은 문자열을 암호화 하는 부분
        encryptionBytes = encrypt( str );
        BASE64Encoder encoder = new BASE64Encoder();
        String encodeString = encoder.encode(encryptionBytes);
         //encoder.encode(encryptionBytes) 으로 encrypt 된 값 출력
        return encodeString;
    }

   private static void setUp() throws Exception {
        key = KeyGenerator.getInstance( algorithm ).generateKey();
        cipher = Cipher.getInstance( algorithm );
    }

    public static String returnDecryptCode(String str) throws Exception {
    	
        BASE64Decoder decoder = new BASE64Decoder();
        String decode = decrypt( decoder.decodeBuffer(str) );
        return decode;
        
    }
 
    // encryptionBytes = encrypt( input ), input을 변조하여 encryptionBytes에 대입함.
    private static byte [] encrypt(String input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException  {
        cipher.init( Cipher.ENCRYPT_MODE, key );
        byte [] inputBytes = input.getBytes();
        return cipher.doFinal( inputBytes );
    }
 
    //decrypt( decoder.decodeBuffer(encodeString) ) 처리부분.
    private static String decrypt(byte [] encryptionBytes) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init( Cipher.DECRYPT_MODE, key );
        byte [] recoveredBytes = cipher.doFinal( encryptionBytes );
        String recovered = new String( recoveredBytes );
        return recovered;
    }
 
	public static void main(String[] args) throws Exception { 
	
		String encryptcode = LocalEncrypter.returnEncryptCode("680215-1004713");
		
		System.out.println(encryptcode);
		
		System.out.println(returnDecryptCode(encryptcode));
		
	}
	
	public static String CookEncrypto(String plaintext) {
		
		String encrypt = CMS.getProp("encrypt","Y");
		
		if(encrypt.equals("N")) {
			return plaintext;
		} else if(encrypt.equals("MD5")) {
			
			return EncMd5(plaintext);
			
		} else if(encrypt.equals("SHA256")) {
			
			return encodeSHA256(CMS.getProp("sea","1234567890123456"),plaintext);
			
		} else if(encrypt.equals("SHA256B64")) {
			
			return encodeSHA256BE(CMS.getProp("sea","1234567890123456"),plaintext);
			
		} else if(encrypt.equals("SHA512")) {
			
			return encodeSHA512(CMS.getProp("sea","1234567890123456"),plaintext);
			
		} else if(encrypt.equals("SHA512B64")) {
			
			return encodeSHA512BE(CMS.getProp("sea","1234567890123456"),plaintext);
			
		} else if(encrypt.equals("AESHEX")) {
			
			return CookEncryptoHex(CMS.getProp("sea","1234567890123456"),plaintext);
			
		} else if(CMS.getProp("encrypt","").equals("AES_SHA256")) {
			
			return encodeSHA256BE("",CookEncryptoKey(CMS.getProp("sea","1234567890123456"), plaintext));
		}
		
		return CookEncryptoKey(CMS.getProp("sea","1234567890123456"), plaintext);
	}
	
	public static String CookEncryptoHex(String sKey, String plaintext) {
		
	    String message = plaintext;
	    String sResult = "";

	    byte[] raw = sKey.getBytes();

	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    try
	    {
	      Cipher cipher = Cipher.getInstance("AES");
	      cipher.init(1, skeySpec);
	      byte[] encrypted = cipher.doFinal(message.getBytes());

	      // base64 사용
	      BASE64Encoder base64encoder = new BASE64Encoder();
	      sResult = byteArrayToHex(encrypted);
	      
	      
	      
	    }
	    catch (Exception localException)
	    {
	    	return Base64Encoder.encode(plaintext);
	    }
	    
	    return sResult.toString();		
	}
	
	public static String CookEncryptoKey(String sKey, String plaintext)
	  {
		
		
	    String message = plaintext;
	    String sResult = "";

	    byte[] raw = sKey.getBytes();

	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    try
	    {
	      Cipher cipher = Cipher.getInstance("AES");
	      cipher.init(1, skeySpec);
	      byte[] encrypted = cipher.doFinal(message.getBytes());

	      // base64 사용
	      BASE64Encoder base64encoder = new BASE64Encoder();
	      sResult = base64encoder.encode(encrypted);
	      
	      // 그냥
	      //sResult = byteArrayToHex(encrypted);
	      
	      
	      
	    }
	    catch (Exception localException)
	    {
	    	return Base64Encoder.encode(plaintext);
	    }
	    
	    return sResult.toString();
	    
	  }
	
	  public static String CookDecrypto(String ciphertext) {
		  
		if(CMS.getProp("encrypt","AES").equals("AES")) {
			return CookDecryptoKey(CMS.getProp("sea","1234567890123456"), ciphertext);
		}
		if(CMS.getProp("encrypt","AES").equals("AESHEX")) {
			return CookDecryptoHex(CMS.getProp("sea","1234567890123456"), ciphertext);
		}
		
		System.out.println("ERROR : CookDecrypto =======> AES 만 복호화가 가능합니다.");
		return "";
		  
		  
	  }

	  public static String CookDecryptoHex(String sKey, String ciphertext)
	  {
		  
		  
	    String sResult = "";
	    
	    byte[] raw = sKey.getBytes();

	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    try
	    {
	    	
	      
	      byte[] bResult = hexToByteArray(ciphertext);

	      Cipher cipher = Cipher.getInstance("AES");
	      cipher.init(2, skeySpec);
	      byte[] original = cipher.doFinal(bResult);

	      sResult = new String(original);
	    }
	    catch (Exception localException) {
	    	return Base64Decoder.decode(ciphertext);
	    }
	    
	    return sResult;
	  }
	  
	  
	  public static String CookDecryptoKey(String sKey, String ciphertext)
	  {
		  
		  
	    String sResult = "";
	    
	    byte[] raw = sKey.getBytes();

	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    try
	    {
	    	
	      // base64
	      BASE64Decoder base64decoder = new BASE64Decoder();
	      byte[] bResult = base64decoder.decodeBuffer(ciphertext);
	      
	      // 그냥
	      //byte[] bResult = hexToByteArray(ciphertext);

	      Cipher cipher = Cipher.getInstance("AES");
	      cipher.init(2, skeySpec);
	      byte[] original = cipher.doFinal(bResult);

	      sResult = new String(original);
	    }
	    catch (Exception localException) {
	    	return Base64Decoder.decode(ciphertext);
	    }
	    
	    return sResult;
	  }


		public static String EncMd5( String source ){ 
			String temp = null;
			MessageDigest mdAlgorithm = null;
			StringBuffer sb = new StringBuffer();
			try{
				mdAlgorithm = MessageDigest.getInstance("MD5");
				byte[] originalBytes = source.getBytes();
				mdAlgorithm.update(originalBytes);
				byte[] digest = mdAlgorithm.digest();
				
				for(int i = 0; i < digest.length; i++){
					temp = Integer.toHexString(0xFF & digest[i]);
					if(temp.length() < 2)
						temp = "0" + temp;
						sb.append(temp);
					}   
			}catch(Exception e){
				e.printStackTrace(); 
			}
			return sb.toString();
			//return temp;
		}
		
	public static String encodeSHA256( String key, String text ) {
		
		MessageDigest md = null;
        String out = "";
        
        try {
            md= MessageDigest.getInstance("SHA-256");
            md.update(key.getBytes());
            md.update(text.getBytes());
            byte[] mb = md.digest();
            
            out = byteArrayToHex(mb);
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return out;

		
	}
	
	public static String encodeSHA512( String key, String text ) {
		
		MessageDigest md = null;
        String out = "";
        
        try {
            md= MessageDigest.getInstance("SHA-512");
            md.update(key.getBytes());
            md.update(text.getBytes());
            byte[] mb = md.digest();
            
            out = byteArrayToHex(mb);
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return out;

		
	}
	
	/**
	 * sha512 base64 endcode
	 * @param key
	 * @param text
	 * @return
	 */
	public static String encodeSHA256BE( String key, String text ) {
		
		MessageDigest md = null;
        String out = "";
        
        try {
            md= MessageDigest.getInstance("SHA-256");
            md.update(key.getBytes());
            md.update(text.getBytes());
            byte[] mb = md.digest();
            
			// base64 사용
			BASE64Encoder base64encoder = new BASE64Encoder();
			out = base64encoder.encode(mb);
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return out;
		
	}
	
	/**
	 * sha512 base64 endcode
	 * @param key
	 * @param text
	 * @return
	 */
	public static String encodeSHA512BE( String key, String text ) {
		
		MessageDigest md = null;
        String out = "";
        
        try {
            md= MessageDigest.getInstance("SHA-512");
            md.update(key.getBytes());
            md.update(text.getBytes());
            byte[] mb = md.digest();
            
			// base64 사용
			BASE64Encoder base64encoder = new BASE64Encoder();
			out = base64encoder.encode(mb);
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return out;
		
	}
	public static byte[] hexToByteArray(String hex) {    
		if (hex == null || hex.length() == 0) {        
			return null;    
		}     
		byte[] ba = new byte[hex.length() / 2];    
		for (int i = 0; i < ba.length; i++) {        
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);    
		}    
		return ba;
	}

	public static String byteArrayToHex(byte[] ba) {    
		if (ba == null || ba.length == 0) {        
			return null;    
		}     
		StringBuffer sb = new StringBuffer(ba.length * 2);    
		String hexNumber;    
		for (int x = 0; x < ba.length; x++) {        
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);         
			sb.append(hexNumber.substring(hexNumber.length() - 2));    
		}    
		return sb.toString();
	}
	
	public static String createSHAEncode(String plaintext) {
		
		return SHA256(plaintext);

	}	
	
	private static String SHA256(String plaintext) {
		MessageDigest messageDigest;
		StringBuffer sb = new StringBuffer();
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(plaintext.getBytes());
			byte[] passwordMessageDigest = messageDigest.digest();
			for(int i=0;i<passwordMessageDigest.length;i++){
				String hex = Integer.toHexString(0xFF & passwordMessageDigest[i]);
				if(hex.length()==1) sb.append('0');
				sb.append(hex);
			}
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}	  
	
} 
