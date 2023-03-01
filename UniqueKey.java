package md.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.LinkedList;

public final class UniqueKey 
{
    private static LinkedList dataTable = null;
	
	public static String getKeyByDateFormat() 
	{       
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				
        Random random = new Random();       
        String keyString = null;
		
        synchronized(UniqueKey.class) 
        {		
			Date currentTime = null;
			String r_num = null;
			String str = "";
	        boolean isFlag = true;

			if (dataTable == null)
			{
				dataTable = new LinkedList();
			}
			
			while (isFlag)
			{
				currentTime = new Date();
				r_num = Integer.toString(random.nextInt(100));
								
				str +=  r_num;
											
				if (str.length() > 3) {
					str = str.substring(str.length()-3, str.length());
				}
				if (str.length() == 3)
				{
					keyString = formatter.format(currentTime) + str;
					isFlag  = findKey(keyString);
				}			
			}	
            if(dataTable.size() > 50) {
				dataTable.removeFirst();
            }
			
			dataTable.add(keyString);
        }
		
        return keyString;
    }
	
	private static boolean findKey(String key)
	{
		int len = dataTable.size();
		boolean flag = false;

		for (int i=0;i<len;i++)
		{
			if (key.equals(dataTable.get(i))) {
				flag = true;
				break;
			}
			else
			{
				flag = false;
			}
		}
		return flag;
	}
} 