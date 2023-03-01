package md.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;

import com.sun.management.OperatingSystemMXBean;

public class CheckServer {
	public static void main(String[] args){

			showMemory();
			showCPU();
		    showOSBean();
	} 

	public static OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

	public static long getHardTotalSpace(){
		File root = null;
		long hardspace = 0;
		try {
			root = new File("/");
			hardspace = root.getTotalSpace();		  
		}catch(Exception e){
			   	
		}
		// System.exit(0);
		return hardspace;
	}
		
	public static long getHardFreeSpace(){
		File root = null;
		long hardspace = 0;
		try {
				root = new File("/");
			    hardspace = root.getFreeSpace();
		}catch(Exception e){
			   	
		}
		return hardspace;
	}
		
	public static long getHardUsableSpace(){
		File root = null;
		long hardspace = 0;
		try {
			root = new File("/");
			hardspace = root.getUsableSpace();
		}catch(Exception e){
			   	
		}
		return hardspace;
	}
	 
	public static long getTotalSwapSpaceSize(){
		return osbean.getTotalSwapSpaceSize();
	}
	
	public static long getFreeSwapSpaceSize(){
		return osbean.getFreeSwapSpaceSize();
	}
	

	public static String toGB(long size)
	{
	    return (int)(size/(1024*1024*1024))+"(GB)";
	}
	
	public static float floatToGB(long size)
	{
	    return (float)( (float) size/(1024*1024*1024));
	}

	public static String toMB(long size)
	{
	    return (int)(size/(1024*1024))+"(MB)";
	}
	public static float floatToMB(long size)
	{
	    return (float)((float) size/(1024*1024));
	}
	
	public static String toKB(long size)
	{
	    return (int)(size/1024)+"(KB)";
	}
	
	
	
	
	
	public static void showOSBean() { 
	    System.out.println("OS Name: " + osbean.getName()); 
	    System.out.println("OS Arch: " + osbean.getArch()); 
	    System.out.println("Available Processors: " 
	        + osbean.getAvailableProcessors()); 
	    System.out.println("TotalPhysicalMemorySize: " 
	        + toKB(osbean.getTotalPhysicalMemorySize())); 
	    System.out.println("FreePhysicalMemorySize: " 
	        + toKB(osbean.getFreePhysicalMemorySize())); 
	    System.out.println("TotalSwapSpaceSize: " 
	        + toMB(osbean.getTotalSwapSpaceSize())); 
	    System.out.println("FreeSwapSpaceSize: " 
	        + toMB(osbean.getFreeSwapSpaceSize())); 
	    System.out.println("CommittedVirtualMemorySize: " 
	        + toMB(osbean.getCommittedVirtualMemorySize())); 
	    System.out.println("SystemLoadAverage: " 
	        + osbean.getSystemLoadAverage()); 
	    
	  } 
	
	
	public static void showCPU() { 
	    OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory 
	      .getOperatingSystemMXBean(); 
	    RuntimeMXBean runbean = (RuntimeMXBean) ManagementFactory 
	      .getRuntimeMXBean(); 

	    long bfprocesstime = osbean.getProcessCpuTime(); 
	    long bfuptime = runbean.getUptime(); 
	    
		long ncpus = osbean.getAvailableProcessors(); 

	    for (int i = 0; i < 1000000; ++i) { 
	      ncpus = osbean.getAvailableProcessors(); 
	    } 

	    long afprocesstime = osbean.getProcessCpuTime(); 
	    long afuptime = runbean.getUptime(); 

	    float cal = (afprocesstime - bfprocesstime) 
	      / ((afuptime - bfuptime) * 10000f); 

	    float usage = Math.min(99f, cal); 

	    System.out.println("Calculation: " + cal); 
	    System.out.println("CPU Usage: " + usage); 

	  } 
	  
	  public static void showMemory() {
	        MemoryMXBean membean = (MemoryMXBean) ManagementFactory
	                .getMemoryMXBean();

	        MemoryUsage heap = membean.getHeapMemoryUsage();
	        System.out.println("Heap Memory: " + heap.toString());
	        MemoryUsage nonheap = membean.getNonHeapMemoryUsage();
	        System.out.println("NonHeap Memory: " + nonheap.toString());

	  }
	  
	  public static long getHardTotalSpace(String disk){
			File root = null;
			long hardspace = 0;
			try {
				root = new File(disk);
				hardspace = root.getTotalSpace();		  
			}catch(Exception e){
				   	
			}
			// System.exit(0);
			return hardspace;
		}
			
		public static long getHardFreeSpace(String disk){
			File root = null;
			long hardspace = 0;
			try {
					root = new File(disk);
				    hardspace = root.getFreeSpace();
			}catch(Exception e){
				   	
			}
			return hardspace;
		}
			
		public static long getHardUsableSpace(String disk){
			File root = null;
			long hardspace = 0;
			try {
				root = new File(disk);
				hardspace = root.getUsableSpace();
			}catch(Exception e){
				   	
			}
			return hardspace;
		}
}
