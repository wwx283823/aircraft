package com.dis.common;

import com.dis.entity.HeavyLoad;
import com.dis.entity.Sva;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.qpid.QpidException;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.url.URLSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AmqpThread extends Thread {

    
    @Autowired
    private Sva sva;
//    /**
//     * @Fields dao : 数据库处理句柄
//     */
//    private MongodbUtils dao;
    /** 
     * @Fields queueId : 队列id
     */ 
    private String queueId;
    /**
     * @Fields isStop : 是否停止线程标志
     */ 
    private boolean isStop = false;

    private AMQConnection conn;

    @Autowired
    private MongodbUtils mongodbUtils;
    
    /** 
     * <p>Title: </p> 
     * <p>Description: 构造函数</p>
     * @param queue 
     */
    public AmqpThread(Sva sva, String queue){
        this.sva = sva;
//        this.dao = dao;
        this.queueId = queue;
    }
    
    /** 
     * @Title: stopThread 
     * @Description: 通过改变标志位，停止线程
     */
    public void stopThread()
    {
        this.isStop = true;
    }
    
    /* (非 Javadoc) 
     * <p>Title: run</p> 
     * <p>Description: 实现sva数据订阅</p>  
     * @see java.lang.Thread#run() 
     */
    public void run()
    {        
        String ip = sva.getIp();
        String id = sva.getId();
        String userName = sva.getUsername();
        String port = sva.getBrokerPort();
        log.info("amqp started:"
                + "userName:" + userName
                + ",queueId:" + queueId
                + ",ip:" + ip
                + ",port:" + port
                + ",id:" + id);

        String classPath = AmqpThread.class.getClassLoader().getResource("/").getPath();  
        String rootPath = "";  
        //windows下  
        if("\\".equals(File.separator)){  
            System.out.println("windows");  
        rootPath = classPath.substring(1,classPath.indexOf("/classes"));  
        rootPath = rootPath.replace("/", "\\");  
        }  
        //linux下  
        if("/".equals(File.separator)){  
            System.out.println("linux");  
        rootPath = classPath.substring(0,classPath.indexOf("/classes"));  
        rootPath = rootPath.replace("\\", "/");  
        }  
        rootPath = rootPath + File.separator + "java_keystore" + File.separator;
        log.info("get keystore path:"+rootPath + "keystore.jks");
        log.info("get keystore path:"+rootPath + "mykeystore.jks");
        // 设置系统环境jvm参数
        System.setProperty("javax.net.ssl.keyStore", rootPath + "keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "importkey");
        System.setProperty("javax.net.ssl.trustStore", rootPath + "mykeystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "importkey");


        
        // 地址变量  
        String brokerOpts = "?brokerlist='tcp://"+ip+":"+port+"?ssl='true'&ssl_verify_hostname='false''";
        String connectionString = "amqp://"+userName+":"+"xxxx@xxxx/"+brokerOpts;
        log.info("connection string:" + connectionString);
        // 建立连接
        try {
            conn = new AMQConnection(connectionString);
            conn.start();
            log.info("connection started!");
            // 获取session
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            log.info("session created!");
            // 获取队列
            Destination queue = new AMQAnyDestination("ADDR:"+queueId+";{create:sender}");
            MessageConsumer consumer = session.createConsumer(queue);
            log.info("consumer created!");
            
            while(!isStop  && !this.isInterrupted())
            {
                Message m = consumer.receive(60000);
                // message为空的情况,
                if(m == null){
                    log.info("Get NULL message, pause for 1 miniute!");
//                    sleep(60000);
                    continue;
                }
                // message格式是否正确
                if(m instanceof BytesMessage){
                    BytesMessage tm = (BytesMessage)m;
                    int length = new Long(tm.getBodyLength()).intValue();
                    if(length > 0){
                        byte[] keyBytes = new byte[length];
                        tm.readBytes(keyBytes);
                        String messages = new String(keyBytes);
                        log.info("sva data:"+messages);
                        saveSvaData(messages, sva.getType(), sva.getIp(), sva.getId());
                    }else{
                        log.error("Get zero length message");
                    }
                }else{
                    log.error("Message is not in Byte format!");
                }
            }
        } catch (URLSyntaxException e) {
            log.error(e.getMessage());
        } catch (QpidException e) {
            log.error(e.getMessage());
        } catch (JMSException e) {
            log.error(e.getMessage());
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        } catch (Exception e){
            log.error(e.getMessage());
        } finally{
            try {
                if(conn != null)
                {
                    conn.close();
                }
            } catch (JMSException e) {
                log.error(e.getMessage());
            }
            log.error("[AMQP]No data from SVA,connection closed!");
        }
    }
    
    /**   
     * @Title: saveSvaData   
     * @Description: 将从sva获取的数据解析并保存到数据库   
     * @param jsonStr：待解析的字符串
     * @param type: 订阅类型
     * @throws   
     */ 
    private void saveSvaData(String jsonStr, int type, String ip, String svaId){
        log.info("SVA start!");
        if(StringUtils.isEmpty(jsonStr.trim())){
            log.info("AMQPThread No data from SVA!");
        }else{
            JSONObject result = JSONObject.fromObject(jsonStr);
            if(result.containsKey("hperfstream")){
                saveHperfstream(result);
            }
//            // 非匿名化订阅
//            if(result.containsKey("locationstream")){
//                saveLocationstream(result, type, svaId);
//            }
//            // 匿名化订阅
//            else if(result.containsKey("locationstreamanonymous")){
//                saveLocationstreamAnonymous(result, svaId);
//            }
        }
        log.info("SVA end!");
    }

    private void saveHperfstream(JSONObject result)
    {
        JSONArray list = result.getJSONArray("hperfstream");
        List<HeavyLoad> heavyLoadList = new ArrayList<HeavyLoad>();
        for(int i = 0; i<list.size();i++){
            HeavyLoad lm = new HeavyLoad();
            JSONObject loc = list.getJSONObject(i);
            if(!parseHperfData(loc, lm)){
                continue;
            }
            if(lm!=null){
                heavyLoadList.add(lm);
            }
        }
        if (heavyLoadList!=null&&heavyLoadList.size()>0) {
            log.info("hperfstream insert start!");
            MongodbUtils.saveList(heavyLoadList);
            log.debug("hperfstream insert end!");
        }
    }

    private boolean parseHperfData(JSONObject loc, HeavyLoad lm){
        // 设置LocationModel
        if(loc.containsKey("Timestamp")){
            long timestamp = loc.getLong("Timestamp");
            lm.setTimestamp(Util.dateStringFormat(Util.dateFormat(timestamp,Params.YYYYMMDDHHMMSS),Params.YYYYMMDDHHMMSS));
        }
        if(loc.containsKey("svcCellId")){
            long svcCellId = loc.getLong("svcCellId");
            lm.setSvcCellId(svcCellId);
        }
        if(loc.containsKey("userCnt")){
            int userCnt = loc.getInt("userCnt");
            lm.setUserCnt(userCnt);
        }
        if(loc.containsKey("ULCellInterference")){
            String ULCellInterference = loc.getString("ULCellInterference");
            lm.setULCellInterference(ULCellInterference);
        }
        if(loc.containsKey("ucULRbRate")){
            String ucULRbRate = loc.getString("ucULRbRate");
            lm.setUcULRbRate(ucULRbRate);
        }
        if(loc.containsKey("ucDLRbRate")){
            String ucDLRbRate = loc.getString("ucDLRbRate");
            lm.setUcDLRbRate(ucDLRbRate);
        }
        return true;
    }

//    /**
//     * @Title: svaLocationstream
//     * @Description: 非匿名化位置信息入库逻辑
//     * @param result
//     * @param type
//     * @param svaId
//     */
//    private void saveLocationstream(JSONObject result, int type, String svaId)
//    {
//        JSONArray list = result.getJSONArray("locationstream");
//        String tableName = null;
//        if(type == 0)
//        {
//            tableName = "bi_location_" + Util.dateFormat(System.currentTimeMillis(), "yyyyMMdd");
//            String insertSql = "insert into "+tableName+"(userId,mapId,x,y,timestamp) values";
//            int sqlLength = insertSql.length();
//            for(int i = 0; i<list.size();i++){
//                LocationModel lm = new LocationModel();
//                JSONObject loc = list.getJSONObject(i);
//                if(!parseLocation(loc, lm, svaId)){
//                    continue;
//                }
//                insertSql = insertSql+"('" + lm.getUserID()+ "','" + lm.getMapId() + "','" + lm.getX() + "','" + lm.getY() + "','" + lm.getTimestamp() + "'),";
//            }
//            if (list.size() > 0&&sqlLength<insertSql.length()) {
//                insertSql = insertSql.substring(0, insertSql.length() - 1);
//                LOG.debug("saveLocationstream insert:" + insertSql);
//                int areaResult = dao.insertSql(insertSql);
//                LOG.debug("saveLocationstream insert result:" + areaResult);
//            }
//        }else
//        {
//            for(int i = 0; i<list.size();i++){
//                LocationModel lm = new LocationModel();
//                JSONObject loc = list.getJSONObject(i);
//                if(!parseLocation(loc, lm, svaId)){
//                    continue;
//                }
//                //检查该用户是否已经存在
//                int count = dao.checkPhoneIsExisted(lm.getUserID());
//                if(count > 0){
//                    dao.updatePhoneLocation(lm);
//                }else{
//                    dao.saveAmqpData(lm, "bi_locationphone");
//                }
//            }
//
//        }
//    }
//
//    /**
//     * @Title: saveLocationstreamAnonymous
//     * @Description: 匿名化订阅位置信息入库
//     * @param result
//     * @param svaId
//     */
//    private void saveLocationstreamAnonymous(JSONObject result, String svaId)
//    {
//        JSONArray list = result.getJSONArray("locationstreamanonymous");
//        String tableName = "bi_location_" + Util.dateFormat(System.currentTimeMillis(), "yyyyMMdd");
//        String insertSql = "insert into "+tableName+"(userId,mapId,x,y,timestamp) values";
//        for(int i = 0; i<list.size();i++){
//            LocationModel lm = new LocationModel();
//            JSONObject loc = list.getJSONObject(i);
//            if(!parseLocation(loc, lm, svaId)){
//                continue;
//            }
//            insertSql = insertSql+"('" + lm.getUserID()+ "','" + lm.getMapId() + "','" + lm.getX() + "','" + lm.getY() + "','" + lm.getTimestamp() + "'),";
//        }
//        if (list.size() > 0) {
//            insertSql = insertSql.substring(0, insertSql.length() - 1);
//            LOG.debug("locationstreamanonymous insert:" + insertSql);
//            int areaResult = dao.insertSql(insertSql);
//            LOG.debug("locationstreamanonymous insert result:" + areaResult);
//        }
////        for(int i = 0; i<list.size();i++){
////            LocationModel lm = new LocationModel();
////            JSONObject loc = list.getJSONObject(i);
////            if(!parseLocation(loc, lm, svaId)){
////                continue;
////            }
////
////            // 获取当天的日期字符串
////            String tableName = "bi_location_" + Util.dateFormat(lm.getTimestamp(), "yyyyMMdd");
////            // 执行数据库保存逻辑
////            dao.saveAmqpData(lm,tableName);
////
////        }
//    }


    
    /**   
     * @Title: parseLocation   
     * @Description: 将json数据转换为LocationModel  
     * @param loc
     * @param lm
     * @param svaId
     * @return：boolean       
     * @throws   
     */ 
//    private boolean parseLocation(JSONObject loc, LocationModel lm, String svaId){
//        // 当前时间戳
//        long timeLocal = System.currentTimeMillis();
//        lm.setTimestamp(timeLocal);
//        // 设置LocationModel
//        if(loc.containsKey("location"))
//        {
//            JSONObject location = loc.getJSONObject("location");
//            lm.setX(Double.valueOf(location.getInt("x")));
//            lm.setY(Double.valueOf(location.getInt("y")));
//            JSONArray useridList = loc.getJSONArray("userid");
//            // 用户存在多个的情况，目前只取第一个；若用户为空则不作处理
//            if(useridList.size()>0){
//                lm.setUserID(useridList.getString(0));
//            }else{
//                return false;
//            }
//            if(loc.containsKey("map"))
//            {
//                JSONObject mapList = loc.getJSONObject("map");
//                String mapId = mapList.getString("mapid");
//                lm.setMapId((mapId));
//            }
//        }else
//        {
//            return false;
//        }
//
//        // 楼层号转换
//
//        return true;
//    }
}
