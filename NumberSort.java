package md.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import md.cms.JWXException;

public class NumberSort implements Comparator {
	
	private static final byte SORT_ASC = 0; 
	private static final byte SORT_DESC = 1; 
	/**
	 * @uml.property  name="key"
	 */
	private String key = null;
	/**
	 * @uml.property  name="sortKind"
	 */
	private byte sortKind = SORT_ASC;
	private Comparator<? super Object> comparator;
	
	public NumberSort() throws Exception {
		
	}
	public NumberSort(List list, String key, byte sortKind) throws Exception {
		
		this.key = key;
		this.sortKind = sortKind;
		chkKey(list);
		
	}
	
	public NumberSort(List list, String key) throws Exception {
		
		this.key = key;
		chkKey(list);
		
	}
	
	private void chkKey(List list)throws Exception {
		
		if(list.size() > 0) {
			boolean posible = ((Map)list.get(0)).containsKey(key);
			if(!posible) {
				throw new JWXException("md.util.NumberSort : sort key ("+key+") not found");
			}
		}
		
	}
	
	/**
	 * @override
	 */
	/* 기본오름차순*/
	public final int compare(Object a, Object b) {
	
		int retv = 0;
		int str_a = Integer.parseInt(Util.toStr(((Map)a).get(key),"00"));
		int str_b = Integer.parseInt(Util.toStr(((Map)b).get(key),"00"));
		
		try {
			retv = str_a < str_b ? -1 : str_a > str_b ? 1:0;
			if(this.sortKind == SORT_DESC) {
				retv = str_a > str_b ? -1 : str_a < str_b ? 1:0;
			}			
			
		} catch (Exception e) {
			
		}
	
		return retv;
	}
	


	
	public static void asc(List list, String key ) throws Exception {
		
		Collections.sort(list,new NumberSort(list,key,NumberSort.SORT_ASC));
		
	}
	
	public static void desc(List list, String key ) throws Exception {
		
		Collections.sort(list,new NumberSort(list,key,NumberSort.SORT_DESC));
		
	}
	
	
	
	public static void main(String args[]) {
		
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		map.put("sort","7");
		list.add(map);
		map = new HashMap();
		map.put("sort","88");
		list.add(map);
		map = new HashMap();
		map.put("sort","15");
		list.add(map);
		
		try {
			NumberSort.asc(list,"sort");
			System.out.print(list);
			NumberSort.desc(list,"sort");
			System.out.print(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}