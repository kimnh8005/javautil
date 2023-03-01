package md.util;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import md.cms.util.TextReader;
import md.cms.util.TextWriter;

public class FileJSONObject extends JSONObject {
	
	/**
	 * @uml.property  name="file_path"
	 */
	private String file_path = null;
	/**
	 * @uml.property  name="file_name"
	 */
	private String file_name = null;
	/**
	 * @uml.property  name="file_full_name"
	 */
	private String file_full_name = null;
	
	public FileJSONObject( File f ) throws Exception {

		this.file_path = f.getParent();
		this.file_name = f.getName();
		this.file_full_name = this.file_path + File.separator + file_name;
		
		if(f.exists()) {
			TextReader tr = new TextReader(file_full_name);
			try {
				this.putAll((JSONObject)JSONValue.parse( tr.toString() ));
			} catch (Exception e) {}
			tr.close();
		}
		f = null;
		
	}
	public FileJSONObject( String file_path, String file_name ) throws Exception {
		
		this.file_path = file_path;
		if(this.file_path.endsWith("/") || this.file_path.endsWith("\\")) {
			this.file_path = file_path.substring(0,file_path.length()-1);
		}
		this.file_name = file_name;
		this.file_full_name = this.file_path + File.separator + file_name;
		
		File f = new File(file_path, file_name);
		
		if(f.exists()) {
			TextReader tr = new TextReader(file_full_name);
			try {
				this.putAll((JSONObject)JSONValue.parse( tr.toString() ));
			} catch (Exception e) {}
			tr.close();
		}
		f = null;
	}
	
	public void saveToFile() {
		
		File f = new File(this.file_path);
		if(!f.exists()) {
			f.mkdirs();
		}
		f = null;
		
		
		TextWriter tw = new TextWriter(this.file_full_name);
		tw.print(this.toString());
		tw.flush();
		tw.close();
		tw = null;
	}
	
	
}
