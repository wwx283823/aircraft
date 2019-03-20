package com.dis.Service;

import com.dis.common.AmqpThread;
import com.dis.common.GlobalConf;
import com.dis.common.HttpsService;
import com.dis.common.MongodbUtils;
import com.dis.entity.HeavyLoad;
import com.dis.entity.HeavyLoadParam;
import com.dis.entity.HighHeavyLoadHistory;
import com.dis.entity.Sva;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SubscriptionService extends HttpsService  {

    /**
     * @Fields amqpDao : amqp对接入库dao
     */
//    @Autowired
//    private MongodbUtils mongodbUtils;

    /**
     * @Fields hasIdType : 向sva订阅时是否要加idType
     */


    private String svaSSLVersion = "TLSv1";

    private static final String FLAG1 = "1";

//    /**
//     * @Title: subscribeSvaInBatch
//     * @Description: 批量订阅sva
//     */
//    public void subscribeSvaInBatch(){
////            subscribeSva(sva);
//        subscribeHeavyLoad(sva);
//    }

    public void subscribeHeavyLoad(Sva sva){
        log.info("subscribeHeavyLoad started:"
                + "appName:" + sva.getUsername()
                + ",ip:" + sva.getIp()
                + ",port:" + sva.getTokenPort()
        );

        // 获取token地址
        String url = "https://" + sva.getIp() + ":"
                + sva.getTokenPort() + "/v3/auth/tokens";
        // 获取token参数
        String content = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\": {\"user\": {\"domain\": \"Api\",\"name\": \""
                + sva.getUsername()
                + "\",\"password\": \""
                + sva.getPassword() + "\"}}}}}";
        String charset = "UTF-8";
        log.info("subscribeHeavyLoad content:" + content);
//        this.insertHeavyLoad();
//        MongodbUtils.findAll(new HeavyLoad());

        try{
            // 获取token值
            Map<String,String> tokenResult = this.httpsPost(url, content, charset,"POST", null, svaSSLVersion);
            String token = tokenResult.get("token");
            sva.setToken(token);

            if(StringUtils.isEmpty(token)){
                log.info("subscribeHeavyLoad token got failed:appName:" + sva.getUsername());
                return;
            }
            log.info("subscribeHeavyLoad token got:"+token);

            url = "https://" + sva.getIp() + ":" + sva.getTokenPort()
                    + "/enabler/catalog/hperfstreamreg/json/v1.0";
            content = "{\"APPID\":\"" + sva.getUsername()+"\"}";
            log.info("subscribeHeavyLoad param:"+content);
            // 获取订阅ID
            Map<String,String> subResult = this.httpsPost(url, content, charset,"POST", tokenResult.get("token"),svaSSLVersion);
            log.info("subscribeHeavyLoad result:" + subResult.get("result"));
            JSONObject jsonObj = JSONObject.fromObject(subResult.get("result"));
            //判断是否订阅成功,成功为0
            JSONObject svaResult =  jsonObj.getJSONObject("result");
            int svaString = svaResult.getInt("error_code");
            if (0==svaString) {
                JSONArray list = jsonObj.getJSONArray("Subscribe Information");
                JSONObject obj = (JSONObject) list.get(0);
                String queueId = obj.getString("QUEUE_ID");
                log.info("subscribeHeavyLoad queueId:" + queueId);
                // 如果获取queueId，则进入数据对接逻辑
                if(StringUtils.isNotEmpty(queueId)){
//                    if(mongodbUtils==null){
//                        mongodbUtils = new MongodbUtils();
//                    }
                    log.info("subscribeHeavyLoad AmqpThread");
                    AmqpThread at = new AmqpThread(sva,queueId);
                    GlobalConf.addAmqpThread(sva.getId(), at);
                    at.start();
                    log.info("subscribeHeavyLoad starting AmqpThread");
                }else{
                    log.info("subscribeHeavyLoad queueId got failed:appName:" + sva.getUsername());
                }
            }
            else{
                log.info("subscribeHeavyLoad sva Subscription failed: "+jsonObj);
            }

        }
        catch (IOException e)
        {
            log.error("subscribeHeavyLoad IOException.", e);
        }
        catch (KeyManagementException e)
        {
            log.error("subscribeHeavyLoad KeyManagementException.", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("subscribeHeavyLoad NoSuchAlgorithmException.", e);
        }
    }


    public void unSubscribeHeavyLoad(Sva sva)
    {
        log.info("unSubscribeHeavyLoad started!");
        String url = "";
        String content = "";

        try
        {
            // 获取token
            url = "https://" + sva.getIp() + ":"
                    + sva.getTokenPort() + "/v3/auth/tokens";
            content = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\": {\"user\": {\"domain\": \"Api\",\"name\": \""
                    + sva.getUsername()
                    + "\",\"password\": \""
                    + sva.getPassword() + "\"}}}}}";
            String charset = "UTF-8";
            Map<String,String> tokenResult = this.httpsPost(url, content, charset,"POST", null, svaSSLVersion);
            String token = tokenResult.get("token");
            if(StringUtils.isEmpty(token)){
                log.error("[unSubscribeHeavyLoad]token got failed:appName:" + sva.getUsername());
                return;
            }
            log.info("[unSubscribeHeavyLoad]token got:"+token);


            url = "https://" + sva.getIp() + ":" + sva.getTokenPort()
                    + "/enabler/catalog/hperfstreamunreg/json/v1.0";
            content = "{\"APPID\":\"" + sva.getUsername()  + "\"}";
            Map<String,String> subResult = this.httpsPost(url, content,charset, "DELETE", token, svaSSLVersion);
            log.info("[unSubscribeHeavyLoad]result:" + subResult.get("result"));
            // 关闭amqp连接
            GlobalConf.removeAmqpThread(sva.getId());
            log.info("[unSubscribeHeavyLoad]connection closed!");
        }
        catch (KeyManagementException e)
        {
            log.error("unSubscribeHeavyLoad KeyManagementException.", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("unSubscribeHeavyLoad NoSuchAlgorithmException.", e);
        }
        catch (IOException e)
        {
            log.error("unSubscribeHeavyLoad IOException.", e);
        }
        catch (Exception e)
        {
            log.error("unSubscribeHeavyLoad Exception.", e);
        }
    }

    public String hperfdef(Sva sva, HeavyLoadParam heavyLoadParam){
        log.info("hperfdef started:"
                + "appName:" + sva.getUsername()
                + ",ip:" + sva.getIp()
                + ",port:" + sva.getTokenPort()
        );
        // 获取token地址
        String url = "https://" + sva.getIp() + ":"
                + sva.getTokenPort() + "/v3/auth/tokens";
        // 获取token参数
        String content = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\": {\"user\": {\"domain\": \"Api\",\"name\": \""
                + sva.getUsername()
                + "\",\"password\": \""
                + sva.getPassword() + "\"}}}}}";
        String charset = "UTF-8";
        log.info("hperfdef token content:" + content);
//        this.insertHeavyLoad();
//        MongodbUtils.findAll(new HeavyLoad());
        try{
            // 获取token值
            Map<String,String> tokenResult = this.httpsPost(url, content, charset,"POST", null, svaSSLVersion);
            String token = tokenResult.get("token");
            sva.setToken(token);

            if(StringUtils.isEmpty(token)){
                log.info("hperfdef token got failed:appName:" + sva.getUsername());
                return "failed";
            }
            log.info("hperfdef token got:"+token);
            url = "https://" + sva.getIp() + ":" + sva.getTokenPort()
                    + "/enabler/catalog/hperfdef/json/v1.0";
            String idTypeString = heavyLoadParam.getIntParam();
            content = "{\"APPID\":\"" + sva.getUsername()
                    + "\"" + idTypeString;
            log.info("hperfdef content:"+content);
            // 获取订阅ID
            Map<String,String> subResult = this.httpsPost(url, content, charset,"POST", tokenResult.get("token"),svaSSLVersion);
            log.info("hperfdef result:" + subResult.get("result"));
            JSONObject jsonObj = JSONObject.fromObject(subResult.get("result"));
            //判断是否订阅成功,成功为0
            JSONObject svaResult =  jsonObj.getJSONObject("result");
            int svaString = svaResult.getInt("error_code");
            if (0==svaString) {
                log.info("hperfdef success!");
                return "success";
            }
            else{
                log.info("hperfdef failed!");
                return "failed";
            }
        }
        catch (IOException e)
        {
            log.error("hperfdef IOException.", e);
            return "failed";
        }
        catch (KeyManagementException e)
        {
            log.error("hperfdef KeyManagementException.", e);
            return "failed";
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("hperfdef NoSuchAlgorithmException.", e);
            return "failed";
        }
    }

    public String hperfrecord(Sva sva,String[] strType){
        log.info("hperfrecord started:"
                + "appName:" + sva.getUsername()
                + ",ip:" + sva.getIp()
                + ",port:" + sva.getTokenPort()
        );
        // 获取token地址
        String url = "https://" + sva.getIp() + ":"
                + sva.getTokenPort() + "/v3/auth/tokens";
        // 获取token参数
        String content = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\": {\"user\": {\"domain\": \"Api\",\"name\": \""
                + sva.getUsername()
                + "\",\"password\": \""
                + sva.getPassword() + "\"}}}}}";
        String charset = "UTF-8";
        log.info("hperfrecord token content:" + content);
//        this.insertHeavyLoad();
//        MongodbUtils.findAll(new HeavyLoad());
        try{
            // 获取token值
            Map<String,String> tokenResult = this.httpsPost(url, content, charset,"POST", null, svaSSLVersion);
            String token = tokenResult.get("token");
            sva.setToken(token);
            if(StringUtils.isEmpty(token)){
                log.info("hperfrecord token got failed:appName:" + sva.getUsername());
                return "failed";
            }
            log.info("hperfrecord token got:"+token);
            url = "https://" + sva.getIp() + ":" + sva.getTokenPort()
                    + "/enabler/catalog/hperfrecord/json/v1.0";
            List<HighHeavyLoadHistory> loadHistories = new ArrayList<HighHeavyLoadHistory>();
            for(int i = 0;i<strType.length;i++){
                String idTypeString = ",\"idtype\":\""+strType[i]+"\"}";
                        content = "{\"APPID\":\"" + sva.getUsername()
                        + "\"" + idTypeString;
                log.info("hperfrecord content:"+content);
                // 获取订阅ID
                Map<String,String> subResult = this.httpsPost(url, content, charset,"POST", tokenResult.get("token"),svaSSLVersion);
//                log.info("hperfrecord result:" + subResult.get("result"));
                JSONObject jsonObj = JSONObject.fromObject(subResult.get("result"));
                //判断是否订阅成功,成功为0
                JSONObject svaResult =  jsonObj.getJSONObject("result");
                int svaString = svaResult.getInt("error_code");
                if (0==svaString) {
                    log.info("hperfrecord success!");
                    HighHeavyLoadHistory(jsonObj,loadHistories,strType[i]);
                }
            }
            List<HighHeavyLoadHistory> list = (List<HighHeavyLoadHistory>) MongodbUtils.findAll(new HighHeavyLoadHistory());
            if(list.size()>0){
                for(HighHeavyLoadHistory highHeavyLoadHistory:list){
                    MongodbUtils.remove(highHeavyLoadHistory);
                }
            }
            if(loadHistories.size()>0){
                MongodbUtils.saveList(loadHistories);
                return "success";
            }else{
                return "failed";
            }

        }
        catch (IOException e)
        {
            log.error("hperfrecord IOException.", e);
            return "failed";
        }
        catch (KeyManagementException e)
        {
            log.error("hperfrecord KeyManagementException.", e);
            return "failed";
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("hperfrecord NoSuchAlgorithmException.", e);
            return "failed";
        }
    }



    private void HighHeavyLoadHistory(JSONObject jsonObj, List<HighHeavyLoadHistory> list,String type){
        try{
            if(jsonObj.containsKey("HperfRecord")){
                JSONArray jsonArray1 = jsonObj.getJSONArray("HperfRecord");
                log.info("HperfRecord jsonArray1");
                for (int i = 0 ;i<jsonArray1.size();i++){
                    JSONObject json = jsonArray1.getJSONObject(i);
                    if(json.containsKey("records")){
                        JSONArray jsonArray = json.getJSONArray("records");
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
                    }

                }
            }
        }catch (Exception e){
            log.info("hperfrecord error:"+e.getMessage());
        }


    }
}
