/**
 * @author CaptainNemo force@andwise.com
 **/
package md.util;

import java.util.*;
import md.cms.CMS;
import md.cms.JWXException;
import md.cms.component.ABComponent;
import md.cms.component.Site;


public class ComponentSort implements Comparator 
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
	
	public ComponentSort(List list, String key, byte sortKind) throws Exception {
		
		this.key = key;
		this.sortKind = sortKind;
		chkKey(list);
		
	}
	
	public ComponentSort(List list, String key) throws Exception {
		
		this.key = key;
		chkKey(list);
		
	}
	
	private void chkKey(List list)throws Exception {
		
		if(list.size() > 0) {
			ABComponent cmp = (ABComponent)list.get(0);
			if(cmp != null) {
			boolean posible = cmp.containsKey(key);
				if(!posible) {
					throw new JWXException("md.util.ComponentSort : sort key ("+key+") not found");
				}
			}
		}
		
	}
	
	/**
	 * @override
	 */
	public final int compare(Object a, Object b) {
	
		int retv = 0;
		
		try {
			retv = ((String)((ABComponent)a).getProperty(key, "")).compareTo(((ABComponent)b).getProperty(key,""));
			
			if(this.sortKind == SORT_DESC) {
				retv = retv * -1;
			}
		} catch (Exception e) {
			
		}
	
		return retv;
	}
	
	public static void asc(List list, String key ) throws Exception {
		
		Collections.sort(list,new ComponentSort(list,key,ComponentSort.SORT_ASC));
		
	}
	
	public static void desc(List list, String key ) throws Exception {
		
		Collections.sort(list,new ComponentSort(list,key,ComponentSort.SORT_DESC));
		
	}
	
	
	public static void main(String args[]) {
		
		Site site = CMS.getSite("gongdan");
		try {
			Vector list = site.getItems();
			ComponentSort.asc(list,"sort");
			System.out.print(list);
			ComponentSort.desc(list,"sort");
			System.out.print(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}