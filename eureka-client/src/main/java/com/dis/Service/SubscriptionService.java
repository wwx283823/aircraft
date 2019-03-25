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
//        if(GlobalConf.getAmqpThread(sva.getId())!=null){
//            log.info("thread subscribeHeavyLoad is not null svaId:"+sva.getId());
//            return;
//        }else{
//            log.info("thread subscribeHeavyLoad is null svaId:"+sva.getId());
//        }
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
//            if(GlobalConf.getAmqpThread(sva.getId())!=null){
//                GlobalConf.removeAmqpThread(sva.getId());
//                log.info("remove thread subscribeHeavyLoad svaId:"+sva.getId());
//            }
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

    public void hperfrecord(Sva sva){
        if(GlobalConf.getAmqpThread(sva.getId())!=null){
            String status = GlobalConf.getAmqpThread(sva.getId()).getState().name();
            log.info("thread hperfrecord is not null svaId:"+sva.getId()+",size:"+GlobalConf.getAmqpMapSize()+",status:"+status);
            if(status!="RUNNABLE"){
                GlobalConf.removeAmqpThread(sva.getId());
                log.info("thread hperfrecord remove");
            }else{
                return;
            }
        }else{
            log.info("thread hperfrecord is null svaId:"+sva.getId());
        }
        log.info("hperfrecord started:"
                + "appName:" + sva.getUsername()
                + ",ip:" + sva.getIp()
                + ",port:" + sva.getTokenPort()
        );;
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

//            if(GlobalConf.getAmqpThread(sva.getId())!=null){
//                GlobalConf.removeAmqpThread(sva.getId());
//                log.info("remove thread hperfrecord id"+sva.getId());
//            }
            Map<String,String> tokenResult = this.httpsPost(url, content, charset,"POST", null, svaSSLVersion);
            String token = tokenResult.get("token");
            sva.setToken(token);
            if(StringUtils.isEmpty(token)){
                log.info("hperfrecord token got failed:appName:" + sva.getUsername());
            }
            log.info("hperfrecord token got:"+token);
            url = "https://" + sva.getIp() + ":" + sva.getTokenPort()
                    + "/enabler/catalog/hperfrecordreg/json/v1.0";
            content = "{\"APPID\":\"" + sva.getUsername() + "\"}";;
            log.info("hperfrecord content:"+content);
            // 获取订阅ID
            Map<String,String> subResult = this.httpsPost(url, content, charset,"POST", tokenResult.get("token"),svaSSLVersion);
//                log.info("hperfrecord result:" + subResult.get("result"));
            JSONObject jsonObj = JSONObject.fromObject(subResult.get("result"));
            //判断是否订阅成功,成功为0
            JSONObject svaResult =  jsonObj.getJSONObject("result");
            int svaString = svaResult.getInt("error_code");
            if (0==svaString) {
                JSONArray list = jsonObj.getJSONArray("Subscribe Information");
                JSONObject obj = (JSONObject) list.get(0);
                String queueId = obj.getString("QUEUE_ID");
                log.info("hperfrecord queueId:" + queueId);
                // 如果获取queueId，则进入数据对接逻辑
                if(StringUtils.isNotEmpty(queueId)){
                    log.info("hperfrecord AmqpThread");
                    AmqpThread at = new AmqpThread(sva,queueId);
                    GlobalConf.addAmqpThread(sva.getId(), at);
                    at.start();
                    log.info("hperfrecord starting AmqpThread");
                }else{
                    log.info("hperfrecord queueId got failed:appName:" + sva.getUsername());
                }
            }

        }
        catch (IOException e)
        {
            log.error("hperfrecord IOException.", e);

        }
        catch (KeyManagementException e)
        {
            log.error("hperfrecord KeyManagementException.", e);

        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("hperfrecord NoSuchAlgorithmException.", e);
        }
    }

    public void unHperfrecord(Sva sva)
    {
        log.info("unHperfrecord started!");
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
                log.error("[unHperfrecord]token got failed:appName:" + sva.getUsername());
                return;
            }
            log.info("[unHperfrecord]token got:"+token);


            url = "https://" + sva.getIp() + ":" + sva.getTokenPort()
                    + "/enabler/catalog/hperfstreamunreg/json/v1.0";
            content = "{\"APPID\":\"" + sva.getUsername()  + "\"}";
            Map<String,String> subResult = this.httpsPost(url, content,charset, "DELETE", token, svaSSLVersion);
//            log.info("[unHperfrecord]result:" + subResult.get("result"));
            // 关闭amqp连接
            GlobalConf.removeAmqpThread(sva.getId());
            log.info("unHperfrecord remove thread id:"+sva.getId());
        }
        catch (KeyManagementException e)
        {
            log.error("unHperfrecord KeyManagementException.", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("unHperfrecord NoSuchAlgorithmException.", e);
        }
        catch (IOException e)
        {
            log.error("unHperfrecord IOException.", e);
        }
        catch (Exception e)
        {
            log.error("unHperfrecord Exception.", e);
        }
    }

}
