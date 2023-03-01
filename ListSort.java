/**
 * @author CaptainNemo force@andwise.com
 */
package md.util;

import java.util.*;

import md.cms.JWXException;


public class ListSort implements Comparator 
{
	
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
	
	public ListSort() throws Exception {
		
	}
	public ListSort(List list, String key, byte sortKind) throws Exception {
		
		this.key = key;
		this.sortKind = sortKind;
		chkKey(list);
		
	}
	
	public ListSort(List list, String key) throws Exception {
		
		this.key = key;
		chkKey(list);
		
	}
	
	private void chkKey(List list)throws Exception {
		
		if(list.size() > 0) {
			boolean posible = ((Map)list.get(0)).containsKey(key);
			if(!posible) {
				throw new JWXException("md.util.ListSort : sort key ("+key+") not found");
			}
		}
		
	}
	
	/**
	 * @override
	 */
	public final int compare(Object a, Object b) {
	
		int retv = 0;
		try {
			
			retv = ((Comparable)((Map)a).get(key)).compareTo(((Map)b).get(key));
			
			if(this.sortKind == SORT_DESC) {
				retv = retv * -1;
			}
			
		} catch (Exception e) {
			
		}
	
		return retv;
	}
	
	public static void asc(List list, String key ) throws Exception {
		
		Collections.sort(list,new ListSort(list,key,ListSort.SORT_ASC));
		
	}
	
	public static void desc(List list, String key ) throws Exception {
		
		Collections.sort(list,new ListSort(list,key,ListSort.SORT_DESC));
		
	}
	
	
	public static void main(String args[]) {
		
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		map.put("sort","2");
		list.add(map);
		map = new HashMap();
		map.put("sort","3");
		list.add(map);
		map = new HashMap();
		map.put("sort","1");
		list.add(map);
		
		try {
			ListSort.asc(list,"sort");
			System.out.print(list);
			ListSort.desc(list,"sort");
			System.out.print(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}