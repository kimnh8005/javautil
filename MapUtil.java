package com.zesware.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class MapUtils {
	public static <E, T> Map<E,T> sortByComparator(Map<E,T> unsortMap, Comparator<Map.Entry<E, T>> comparator) {
		 
		List<Map.Entry<E, T>> list = new LinkedList<Map.Entry<E, T>>( unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, comparator);
		
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<E,T> sortedMap = new LinkedHashMap<E,T>();
		for (Iterator<Entry<E, T>> it = list.iterator(); it.hasNext();) {
			Map.Entry<E, T> entry =  it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static void main(String[] args){
		
		List<EgovMap> list = new ArrayList<EgovMap>();
		
		
		EgovMap egov = null;
		egov = new EgovMap(); egov.put("menuNum", 100L); egov.put("pMenuNum", 0L); egov.put("sortNum", 1); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 110L); egov.put("pMenuNum", 100L); egov.put("sortNum", 2); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 120L); egov.put("pMenuNum", 100L); egov.put("sortNum", 1); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 200L); egov.put("pMenuNum", 0L); egov.put("sortNum", 3); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 210L); egov.put("pMenuNum", 200L); egov.put("sortNum", 1); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 220L); egov.put("pMenuNum", 200L); egov.put("sortNum", 2); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 300L); egov.put("pMenuNum", 0L); egov.put("sortNum", 2); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 310L); egov.put("pMenuNum", 300L); egov.put("sortNum", 1); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 320L); egov.put("pMenuNum", 200L); egov.put("sortNum", 2); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 311L); egov.put("pMenuNum", 310L); egov.put("sortNum", 3); list.add(egov);
		egov = new EgovMap(); egov.put("menuNum", 321L); egov.put("pMenuNum", 320L); egov.put("sortNum", 3); list.add(egov);
		
		Map<Long, EgovMap> map = new LinkedHashMap<Long, EgovMap>();
		map = makeMenuTree(list, map);
		logByMap(map);
		map = sortByComparator(map,  new EgovLeveComparator());
		logByMap(map);
	}
	
	private static <E,T> void logByMap(Map<E,T> map){
		System.out.println(map.toString());
	}
	
	private static Map<Long, EgovMap> makeMenuTree( List<EgovMap> menulist, Map<Long, EgovMap> menulist_map ){
        
        List<EgovMap> tempList = new ArrayList<EgovMap>();
        int cnt = menulist.size();
        EgovMap parent = null;
        
        for(EgovMap temp : menulist){
               parent = null;
               // pMenuNum 이 0 이면 level = 1
               if(((Long)temp.get("pMenuNum")) == 0){
                       temp.put("lev", 1);
                       // level 이 1 보다 크다
               } else {
                       // menulist_map 에 parent 가 존재 하면 parent level 을 가져와서 +1 한값을 level로 설정 한다.
                       
                       if(menulist_map.containsKey(temp.get("pMenuNum"))){
                              parent = menulist_map.get(temp.get("pMenuNum"));
                              int lev= (Integer)parent.get("lev");
                              temp.put("lev", lev+1);
                              //sort비교 위해서 parent 속성 값 추가
                              temp.put("parent", parent);
                              // parent 가 존재 하지 않으면 templist_map 에 담고 넘김
                       } else {
                              tempList.add( temp);
                              continue;
                      }
               }                                    
               menulist_map.put((Long)temp.get("menuNum"), temp);                 
        }
        
        int tempCnt = tempList.size();
        
        if(tempCnt > 0 && tempCnt < cnt){
               return makeMenuTree(tempList, menulist_map);
        }

        return menulist_map;
	}
}
class EgovLeveComparator implements Comparator<Map.Entry<Long, EgovMap>>{

	@Override
	public int compare(Entry<Long, EgovMap> o1, Entry<Long, EgovMap> o2) {
		
		return compare(o1.getValue(), o2.getValue());
	}
	
	private int compare(EgovMap o1, EgovMap o2){
		int lvl1 = (Integer)o1.get("lev");
		int lvl2 = (Integer)o2.get("lev");
		
		if(lvl1 > lvl2){
			return compare((EgovMap)o1.get("parent"), o2);
		} else if(lvl1 < lvl2){
			return compare(o1,(EgovMap)o2.get("parent"));
		}
		
		long pNum1 = (Long)o1.get("pMenuNum");
		long pNum2 = (Long)o2.get("pMenuNum");
		
		if(pNum1 != pNum2) {
			return compare((EgovMap)o1.get("parent"), (EgovMap)o2.get("parent"));
		}
		
		int sortNum1 = (Integer)o1.get("sortNum");
		int sortNum2 = (Integer)o2.get("sortNum");
		
		if(sortNum1 != sortNum2) {
			return sortNum1 - sortNum2;
		}
		
		long menuNum1 = (Long)o1.get("menuNum");
		long menuNum2 = (Long)o2.get("menuNum");
		
		return (int)(menuNum1 - menuNum2);
	}
	
}
