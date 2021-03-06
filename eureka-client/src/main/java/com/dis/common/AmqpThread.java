package com.dis.common;

import com.dis.entity.HeavyLoad;
import com.dis.entity.HighHeavyLoadHistory;
import com.dis.entity.Sva;
import com.dis.entity.WirelessInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import netscape.javascript.JSObject;
import org.apache.commons.lang.StringUtils;
import org.apache.qpid.QpidException;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.url.URLSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;

import javax.jms.*;
import java.io.File;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class AmqpThread extends Thread {

    

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

    private DecimalFormat df = null;
    /** 
     * <p>Title: </p> 
     * <p>Description: 构造函数</p>
     * @param queue 
     */
    public AmqpThread(Sva svaParam, String queue){
        sva = new Sva();
        sva.setBrokerPort(svaParam.getBrokerPort());
        sva.setToken(svaParam.getToken());
        sva.setId(svaParam.getId());
        sva.setIdType(svaParam.getIdType());
        sva.setIp(svaParam.getIp());
        sva.setPassword(svaParam.getPassword());
        sva.setStatus(svaParam.getStatus());
        sva.setTokenPort(svaParam.getTokenPort());
        sva.setType(svaParam.getType());
        sva.setUsername(svaParam.getUsername());
        this.queueId = queue;
        log.info("new AmqpThread queueId:"+queueId);

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
        df = new DecimalFormat("0.0");
        log.info("AmqpThread run sva:"+sva.getIp()+",id："+sva.getId()+",userName:"+sva.getUsername());
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

//        String classPath = AmqpThread.class.getClassLoader().getResource("/").getPath();
        String rootPath = "";
//        //windows下
//        if("\\".equals(File.separator)){
//        rootPath = classPath.substring(1,classPath.indexOf("/classes"));
//            log.info("windows rootPath:"+rootPath);
//        rootPath = rootPath.replace("/", "\\");
//        }
//        //linux下
//        if("/".equals(File.separator)){
//
//        rootPath = classPath.substring(0,classPath.indexOf("/classes"));
//            log.info("linux rootPath:"+rootPath);
//        rootPath = rootPath.replace("\\", "/");
//        }
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        log.info("path:"+jarFile.getParentFile());
        rootPath = jarFile.getParentFile().toString()+File.separator;
//        rootPath = rootPath + File.separator + "java_keystore" + File.separator;
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
                Message m = consumer.receive(10000);
                // message为空的情况,
                if(m == null){
                    log.info("Get NULL message, pause for 1 miniute! svaId:"+sva.getId());
//                    sleep(60000);
                    sleep(200);
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
            log.error("URLSyntaxException:"+e.getMessage());
        } catch (QpidException e) {
            log.error("QpidException:"+e.getMessage());
        } catch (JMSException e) {
            log.error("JMSException:"+e.getMessage());
        } catch (URISyntaxException e) {
            log.error("URISyntaxException:"+e.getMessage());
        } catch (Exception e){
            log.error("Exception:"+e.getMessage());
        } finally{
            try {
                if(conn != null)
                {
                    conn.close();
                    log.error("run close conn svaId:"+sva.getId());
                }
            } catch (JMSException e) {
                log.error("JMSException:"+e.getMessage());
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
            if(result.containsKey("hperfrecord")){
                JSONArray jsonArray = result.getJSONArray("hperfrecord");
                for (int i=0;i<jsonArray.size();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    saveHperfrecord(jsonObject);
                }
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

    private void saveHperfrecord(JSONObject jsonObj){
       List<HighHeavyLoadHistory> list = new ArrayList<HighHeavyLoadHistory>();
       String type = null;
        try{
            if(jsonObj.containsKey("HperfRecord")){
                JSONArray jsonArray1 = jsonObj.getJSONArray("HperfRecord");
                log.info("HperfRecord jsonArray1");
                for (int i = 0 ;i<jsonArray1.size();i++){
                    JSONObject json = jsonArray1.getJSONObject(i);
                    if(json.containsKey("fcnuser")){
                        JSONArray jsonArray = json.getJSONArray("fcnuser");
                        getHeavyLoadHistory(list,jsonArray,"fcnuser");
                    }
                    if(json.containsKey("user")){
                        JSONArray jsonArray = json.getJSONArray("user");
                        getHeavyLoadHistory(list,jsonArray,"user");
                    }
                    if(json.containsKey("interference")){
                        JSONArray jsonArray = json.getJSONArray("interference");
                        getHeavyLoadHistory(list,jsonArray,"interference");
                    }
                }
                if(list!=null&&list.size()>0){
                    log.info("hperfstream insert start!");
                    MongodbUtils.saveList(list);
                    log.debug("hperfstream insert end!");

                }
            }
        }catch (Exception e){
            log.info("hperfrecord error:"+e.getMessage());
        }


    }

    private List<HighHeavyLoadHistory> getHeavyLoadHistory(List<HighHeavyLoadHistory> list,JSONArray jsonArray,String type){
        for (int j = 0;j<jsonArray.size();j++){
            HighHeavyLoadHistory highHeavyLoadHistory = new HighHeavyLoadHistory();
            JSONObject jsonObject = jsonArray.getJSONObject(j);
            if(jsonObject.containsKey("timeStamp")){
                highHeavyLoadHistory.setTimeStamp(jsonObject.getLong("timeStamp")*1000);
            }
            if(jsonObject.containsKey("adjustType")){
                highHeavyLoadHistory.setAdjustType(jsonObject.getLong("adjustType"));
                if(jsonObject.getLong("adjustType")==1){
                    highHeavyLoadHistory.setType("用户数调整");
                }
                if(jsonObject.getLong("adjustType")==2){
                    highHeavyLoadHistory.setType("RSRP高门限调整");
                }
                if(jsonObject.getLong("adjustType")==4){
                    highHeavyLoadHistory.setType("RS功率调整");
                }
            }
            if(jsonObject.containsKey("ulSvcCellId")){
                highHeavyLoadHistory.setUlSvcCellId(jsonObject.getLong("ulSvcCellId") & 0xFF);
            }
            if(jsonObject.containsKey("ulDstCellId")){
                highHeavyLoadHistory.setUlDstCellId(jsonObject.getLong("ulDstCellId") & 0xFF);
            }
            if(jsonObject.containsKey("usKickUserCnt")){
                highHeavyLoadHistory.setUsKickUserCnt(jsonObject.getLong("usKickUserCnt"));
            }
            if(jsonObject.containsKey("rspwrDelta")){
                highHeavyLoadHistory.setRspwrDelta(jsonObject.getLong("rspwrDelta"));
            }
            if(jsonObject.containsKey("rsrpDelta")){
                highHeavyLoadHistory.setRsrpDelta(jsonObject.getLong("rsrpDelta"));
            }
            if("fcnuser".equals(type)){
                highHeavyLoadHistory.setBigType("频点间基于用户数的快速负载均衡");
            }
            if("user".equals(type)){
                highHeavyLoadHistory.setBigType("基于用户数的快速调整");
            }
            if("interference".equals(type)){
                highHeavyLoadHistory.setBigType("基于干扰的快速负载均衡");
            }
            list.add(highHeavyLoadHistory);
        }
        return list;
    }

    private void saveHperfstream(JSONObject result)
    {
        JSONArray list = result.getJSONArray("hperfstream");
//        List<WirelessInfo> wirelessInfos = new ArrayList<WirelessInfo>();
        for(int i = 0; i<list.size();i++){
            WirelessInfo lm = new WirelessInfo();
            List<WirelessInfo> wirelessInfoList = new ArrayList<WirelessInfo>();
            JSONObject loc = list.getJSONObject(i);
            if(!parseHperfData(loc, lm,wirelessInfoList)){
                continue;
            }
            if(wirelessInfoList!=null&&wirelessInfoList.size()>0){
                log.info("hperfstream insert start!");
                MongodbUtils.saveList(wirelessInfoList);
                log.debug("hperfstream insert end!");
//                wirelessInfos.addAll(wirelessInfoList);
            }

        }
//        if (wirelessInfos!=null&&wirelessInfos.size()>0) {
//            log.info("hperfstream insert start!");
//            MongodbUtils.saveList(wirelessInfos);
//            log.debug("hperfstream insert end!");
//        }
    }

    private boolean parseHperfData(JSONObject loc, WirelessInfo lm,List<WirelessInfo> list){
        if(loc.containsKey("wirelessInfo")){
            Date now = new Date();
            long uleNodebId = 0;
            String ulServiceCellId = null;

            try{
                String result2 = String.valueOf(getJsonByStr(loc,"ulServiceCellId"));
                if(result2!=null){
                    ulServiceCellId = result2;
                    lm.setUlServiceCellId(String.valueOf(Long.valueOf(ulServiceCellId) & 0xFF));
                }
                Long result = getJsonByStr(loc,"uleNodebId");
                if(result!=null){
                    uleNodebId = Integer.parseInt(result.toString());
                    lm.setUleNodebId(uleNodebId);
                }
                JSONArray jsonArray1 = loc.getJSONArray("wirelessInfo");
                for (int j=0;j<jsonArray1.size();j++){
                    log.info("parseHperfData jsonArray1:");
                    if(j>0){
                        lm = new WirelessInfo();
                        lm.setTimestamp(now);
                        lm.setUlServiceCellId(ulServiceCellId);
                        lm.setUleNodebId(uleNodebId);
                    }else{
                        lm.setTimestamp(now);
                    }
                    JSONObject jsonObject = jsonArray1.getJSONObject(j);
                    Long result1 = getJsonByStr(jsonObject,"ULCellInterference");
                    if(result1!=null){
                        long ULCellInterference = result1;
                        lm.setUlCellInterference(ULCellInterference);
                    }
                    result1 = getJsonByStr(jsonObject,"ucDLAvgMcs");
                    if(result1!=null){
                        long ucDLAvgMcs =result1;
                        lm.setUcDLAvgMcs(ucDLAvgMcs);
                    }
                    result1 = getJsonByStr(jsonObject,"ucDLRbRate");
                    if(result1!=null){
                        long ucDLRbRate = result1;
                        lm.setUcDLRbRate(ucDLRbRate);
                    }
                    result1 = getJsonByStr(jsonObject,"ucULAvgMcs");
                    if(result1!=null){
                        long ucULAvgMcs = result1;
                        lm.setUcULAvgMcs(ucULAvgMcs);
                    }
                    result1 = getJsonByStr(jsonObject,"ucULRbRate");
                    if(result1!=null){
                        long ucULRbRate = result1;
                        lm.setUcULRbRate(ucULRbRate);
                    }
                    result1 = getJsonByStr(jsonObject,"ulActiveUserNum");
                    if(result1!=null){
                        double ulActiveUserNum = Double.valueOf(df.format(result1/1000f));
                        lm.setUlActiveUserNum(ulActiveUserNum);
                    }
                    result1 = getJsonByStr(jsonObject,"ulULActiveUserAvgRate");
                    if(result1!=null){
                        double ulULActiveUserAvgRate = Double.valueOf(df.format(result1/1000f));
                        lm.setUlULActiveUserAvgRate(ulULActiveUserAvgRate);
                    }
                    result1 = getJsonByStr(jsonObject,"ulULCellTraffic");
                    if(result1!=null){
                        double ulULCellTraffic = Double.valueOf(df.format(result1/1000f));
                        lm.setUlULCellTraffic(ulULCellTraffic);
                    }
                    result1 = getJsonByStr(jsonObject,"usAvgUserNum");
                    if(result1!=null){
                        long usAvgUserNum = result1;
                        lm.setUsAvgUserNum(usAvgUserNum);
                    }
                    result1 = getJsonByStr(jsonObject,"usCpuRate");
                    if(result1!=null){
                        long usCpuRate = result1;
                        lm.setUsCpuRate(usCpuRate);
                    }
                    result1 = getJsonByStr(jsonObject,"usMaxUserNum");
                    if(result1!=null){
                        long usMaxUserNum = result1;
                        lm.setUsMaxUserNum(usMaxUserNum);
                    }
                    result1 = getJsonByStr(jsonObject,"timeStamp");
                    if(result1!=null){
                        long usMaxUserNum = result1;
                        lm.setMyTimestamp(usMaxUserNum);
                    }
//                    result1 = getJsonByStr(jsonObject,"timeStamp");
//                    if(result1!=null){
//                        long timestamps = result1;
//                        lm.setTimeStamp(timestamps);
//                    }
                    list.add(lm);
                }
            }catch (Exception e){
                log.info("parseHperfData error:"+e.getMessage());
                return  false;
            }

        }else{
            log.info("parseHperfData no wirelessInfo");
            return false;
        }



//        if(loc.containsKey("Timestamp")){
//            long timestamp = loc.getLong("Timestamp");
//            lm.setTimestamp(Util.dateStringFormat(Util.dateFormat(timestamp,Params.YYYYMMDDHHMMSS),Params.YYYYMMDDHHMMSS));
//        }
//        if(loc.containsKey("svcCellId")){
//            long svcCellId = loc.getLong("svcCellId");
//            lm.setSvcCellId(svcCellId);
//        }
//        if(loc.containsKey("userCnt")){
//            int userCnt = loc.getInt("userCnt");
//            lm.setUserCnt(userCnt);
//        }
//        if(loc.containsKey("ULCellInterference")){
//            String ULCellInterference = loc.getString("ULCellInterference");
//            lm.setULCellInterference(ULCellInterference);
//        }
//        if(loc.containsKey("ucULRbRate")){
//            String ucULRbRate = loc.getString("ucULRbRate");
//            lm.setUcULRbRate(ucULRbRate);
//        }
//        if(loc.containsKey("" +
//                "")){
//            String ucDLRbRate = loc.getString("ucDLRbRate");
//            lm.setUcDLRbRate(ucDLRbRate);
//        }
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

    private Long getJsonByStr(JSONObject jsonObject,String str){
        if(jsonObject.containsKey(str))
        {
            long  result = jsonObject.getLong(str);
            return  result;
        }else {
            return null;
        }
    }
}
