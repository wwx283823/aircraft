package com.dis.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GlobalConf
{
    public static String ip;

    public static String user;

    public static String password;

    /**
     * prru采集特征值线程池
     */
    private static Map<String,Thread> prruTaskPool = new HashMap<String,Thread>(10);
    
    /**
     * 对接SVA数据线程池
     */
    private static ArrayList<Thread> threadPool = new ArrayList<Thread>(10);
    
    /**
     * 对接SVA数据对接管理表(java)
     */
    private static Map<String,AmqpThread> subscriptionMap = new HashMap<String,AmqpThread>();

    /**
     * 对接IBMBluemixClient线程池
     */
    private static ArrayList<Thread> bluemixClientThreadPool = new ArrayList<Thread>(
            10);
    
    private static Map<String,Map<String,Object>> navigateData = new HashMap<String,Map<String,Object>>();
    
    /**
     * 加入prru采集特征值线程池
     * 
     * @param e
     */
    public static synchronized void addPrruThreadPool(String id, Thread e)
    {
        prruTaskPool.put(id, e);
    }

    /**
     * 移除指定id对应的prru采集特征值线程
     * 
     * @param e
     */
    public static synchronized void removePrruThreadPool(String id)
    {
        prruTaskPool.remove(id);
    }
    
    /**
     * 根据id获取对于线程
     * 
     * @param i
     * @return
     */
    public static synchronized Thread getPrruThread(String id)
    {
        return prruTaskPool.get(id);
    }
    
    /**
     * 加入SVA数据线程池
     * 
     * @param e
     */
    public static synchronized void addThreadPool(Thread e)
    {
        threadPool.add(e);
    }

    /**
     * 移除SVA数据线程
     * 
     * @param e
     */
    public static synchronized void removeThreadPool(Thread e)
    {
        threadPool.remove(e);
    }

    /**
     * 获取SVA数据线程池长度
     * 
     * @return
     */
    public static synchronized int getThreadPoolSize()
    {
        return threadPool.size();
    }

    /**
     * 根据索引获取对于线程
     * 
     * @param i
     * @return
     */
    public static synchronized Thread getThreadPool(int i)
    {
        return threadPool.get(i);
    }

    /**
     * 添加对接IBMBluemixClient线程
     */
    public static synchronized void addBluemixClientThreadPool(Thread e)
    {
        bluemixClientThreadPool.add(e);
    }

    /**
     * 移除IBMBluemixClient线程
     */
    public static synchronized void removeBluemixClientThreadPool(Thread e)
    {
        bluemixClientThreadPool.remove(e);
    }

    /**
     * 获取IBMBluemixClient线程池长度
     */
    public static synchronized int getBluemixClientThreadPoolSize()
    {
        return bluemixClientThreadPool.size();
    }

    /**
     * 根据索引获取IBMBluemixClient线程
     */
    public static synchronized Thread getBluemixClientThreadPool(int i)
    {
        return bluemixClientThreadPool.get(i);
    }
    
    /**   
     * @Title: addService   
     * @Description: 加入SVA数据对接管理表   
     * @param svaId
     * @param s   
     * @return: void      
     * @throws   
     */ 
    public static synchronized void addAmqpThread(String svaId, AmqpThread thread)
    {
    	subscriptionMap.put(svaId, thread);
    }

    /**   
     * @Title: removeService   
     * @Description: 移除SVA数据对接管理表  
     * @param svaId      
     * @return: void      
     * @throws   
     */ 
    public static synchronized void removeAmqpThread(String svaId)
    {
    	AmqpThread at = subscriptionMap.get(svaId);
    	if(at != null){
    		at.stopThread();
    	}
        subscriptionMap.remove(svaId);
    }

    /**   
     * @Title: getServiceMapSize   
     * @Description: 获取SVA数据对接管理表的大小  
     * @return      
     * @return: int      
     * @throws   
     */ 
    public static synchronized int getAmqpMapSize()
    {
        return subscriptionMap.size();
    }

    /**   
     * @Title: getService   
     * @Description: 根据键，获取对应的sva数据对接服务  
     * @param svaId
     * @return：SubscriptionService       
     * @throws   
     */ 
    public static synchronized AmqpThread getAmqpThread(String svaId)
    {
        return subscriptionMap.get(svaId);
    }
    
    /** 
     * @Title: addNavigateData 
     * @Description: 添加移动端html5导航数据
     * @param fileName
     * @param data 
     */
    public static synchronized void addNavigateData(String fileName, Map<Integer,Map<Integer,List<Integer>>> data, List<Map<String,Object>> pointArray)
    {
        // 将点的数组转换为map
        Map<Integer, Object> poingMap = new HashMap<Integer, Object>();
        // 遍历list，转换成map
        for(Map<String,Object> m : pointArray){
            int id = Integer.parseInt(m.get("id").toString());
            poingMap.put(id, m);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("point", poingMap);
        result.put("navigate", data);
        navigateData.put(fileName, result);
    }
    
    /** 
     * @Title: getNavigateDataByFileName 
     * @Description: 获取指定路径规划文件对应的导航信息
     * @param fileName
     * @return 
     */
    public static synchronized Map<String, Object> getNavigateDataByFileName(String fileName){
        return navigateData.get(fileName);
    }
}
