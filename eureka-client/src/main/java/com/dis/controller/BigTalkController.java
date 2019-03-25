package com.dis.controller;

import com.dis.Service.SubscriptionService;
import com.dis.common.MongodbUtils;
import com.dis.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@RestController
public class BigTalkController {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Sva sva;

    @Autowired
    private Echart echart;

    @RequestMapping("/subBigTalkByCellId")
    public String setBigTalk(HeavyLoadParam heavyLoadParam){
        String result = subscriptionService.hperfdef(sva,heavyLoadParam);
        if("success".equals(result)){
            MongodbUtils.save(heavyLoadParam,"HeavyLoadParamHistory");
        }
        return result;
    }
    @RequestMapping("/newSubBigTalkByCellId")
    public Map<String,String> newSubBigTalkByCellId(HeavyLoadParam heavyLoadParam){
        List<HeavyLoadParam> heavyLoadParamList = (List<HeavyLoadParam>) MongodbUtils.findAll(new HeavyLoadParam());
        HeavyLoadParam oldH = null;
        if(heavyLoadParamList.size()>0){
            for(HeavyLoadParam heavyLoadParam1:heavyLoadParamList){
                oldH = heavyLoadParam1;
            }
        }
        String result1 = "null";
        String result2 = "null";
        String result = "null";
//        HeavyLoadParam he = new HeavyLoadParam();
        if(heavyLoadParam.getType1()==1){
            heavyLoadParam.setType(1);
            heavyLoadParam.setOpenClose(1);
            if(oldH!=null&&oldH.getType1() == heavyLoadParam.getType1()){
                oldH.setType(1);
                oldH.setOpenClose(1);
                if(!oldH.getIntParam().equals(heavyLoadParam.getIntParam())){
                    result1 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                    result1 = "success";
                }
            }else{
                result1 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                result1  = "success";
            }
//            String ssss = heavyLoadParam.getIntParam();
        }else{
            heavyLoadParam.setType(1);
            heavyLoadParam.setOpenClose(0);
            if(oldH!=null&&oldH.getType1() == heavyLoadParam.getType1()){
                oldH.setType(1);
                oldH.setOpenClose(0);
                if(!oldH.getIntParam().equals(heavyLoadParam.getIntParam())){
                    result1 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                    result1 = "success";
                }
            }else{
                result1 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                result1 = "success";
            }

        }
        if(heavyLoadParam.getType2()==1){
            heavyLoadParam.setType(2);
            heavyLoadParam.setOpenClose(1);
            if(oldH!=null&&oldH.getType2() == heavyLoadParam.getType2()){
                oldH.setType(2);
                oldH.setOpenClose(1);
//            String ssss = heavyLoadParam.getIntParam();
                if(!oldH.getIntParam().equals(heavyLoadParam.getIntParam())){
                    result2 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                    result2 = "success";
                }
            }else{
                result2 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                result2 = "success";
            }

        }else{
            heavyLoadParam.setType(2);
            heavyLoadParam.setOpenClose(0);
            if(oldH!=null&&oldH.getType2() == heavyLoadParam.getType2()){
                oldH.setType(2);
                oldH.setOpenClose(0);
//            String ssss = heavyLoadParam.getIntParam();
                if(!oldH.getIntParam().equals(heavyLoadParam.getIntParam())){
                    result2 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                    result2 = "success";
                }
            }else{
                result2 = subscriptionService.hperfdef(sva,heavyLoadParam);
//                result2 = "success";
            }

        }
        if(heavyLoadParam.getType3()==1){
            heavyLoadParam.setType(0);
            heavyLoadParam.setOpenClose(1);
            if(oldH!=null&&oldH.getType3() == heavyLoadParam.getType3()){
                oldH.setType(0);
                oldH.setOpenClose(1);
//            String ssss = heavyLoadParam.getIntParam();
                if(!oldH.getIntParam().equals(heavyLoadParam.getIntParam())){
                    result = subscriptionService.hperfdef(sva,heavyLoadParam);
//                    result = "success";
                }
            }else{
                result = subscriptionService.hperfdef(sva,heavyLoadParam);
//                result = "success";
            }

        }else{
            heavyLoadParam.setType(0);
            heavyLoadParam.setOpenClose(0);
            if(oldH!=null&&oldH.getType3() == heavyLoadParam.getType3()){
                oldH.setType(0);
                oldH.setOpenClose(0);
//            String ssss = heavyLoadParam.getIntParam();
                if(!oldH.getIntParam().equals(heavyLoadParam.getIntParam())){
                    result = subscriptionService.hperfdef(sva,heavyLoadParam);
//                    result = "success";
                }
            }else{
                result = subscriptionService.hperfdef(sva,heavyLoadParam);
//                result = "success";
            }
        }
        if(oldH!=null){
            MongodbUtils.remove(oldH);
        }
        MongodbUtils.save(heavyLoadParam);
        Map<String,String> map = new HashMap<String,String>(3);
        map.put("result1",result1);
        map.put("result2",result2);
        map.put("result",result);
        return map;
    }
    @RequestMapping("/getParam")
    public List<HeavyLoadParam> getParam(){
//        MongodbUtils.findAll(new HeavyLoadParam());
        return (List<HeavyLoadParam>)MongodbUtils.findAll(new HeavyLoadParam());
    }
    @RequestMapping("/getHistoryBySva")
    public String getHistoryBySva(){
        Sva mySva = new Sva();
        mySva.setId(sva.getId()+1);
        mySva.setUsername(sva.getUsername2());
        mySva.setType(sva.getType());
        mySva.setTokenPort(sva.getTokenPort());
        mySva.setStatus(sva.getStatus());
        mySva.setPassword(sva.getPassword());
        mySva.setIp(sva.getIp());
        mySva.setBrokerPort(sva.getBrokerPort());
        mySva.setIdType(sva.getIdType());
        subscriptionService.hperfrecord(mySva);
        return "success";
    }
    @RequestMapping("/getNewHistory")
    public List<HighHeavyLoadHistory> getNewHistory(){
        List<HighHeavyLoadHistory> highHeavyLoadHistories = (List<HighHeavyLoadHistory>) MongodbUtils.findAll(new HighHeavyLoadHistory());
        return highHeavyLoadHistories;
    }
    @RequestMapping("/getHistoryBigTalkByCellId")
    public List<HighHeavyLoadHistory> getHistoryBigTalkByCellId(HeavyLoad heavyLoad){
        HighHeavyLoadHistory HighHeavyLoadHistory = null;
        if(HighHeavyLoadHistory==null){
            HighHeavyLoadHistory = new HighHeavyLoadHistory();
        }
        String[] keys = {"ulSvcCellId"};
        Object[]  values = {heavyLoad.getSvcCellId()};
        List<HighHeavyLoadHistory> list = (List<HighHeavyLoadHistory>) MongodbUtils.find(HighHeavyLoadHistory,keys,values,"HighHeavyLoadHistory","timeStamp");
        return list;
    }

    @RequestMapping("/getEchartsDataByCellId")
    public List<WirelessInfo> getEchartsDataByCellId(WirelessInfo wirelessInfos){
        Long tiesTamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        WirelessInfo wirelessInfo = null;
        if(wirelessInfo==null){
            wirelessInfo = new WirelessInfo();
        }

        Date timestamp = new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-echart.getAllTime()*60*1000);
        String[] keys = {"ulServiceCellId","timestamp"};
        Object[]  values = {wirelessInfos.getUlServiceCellId(),timestamp};

        List<WirelessInfo> list = (List<WirelessInfo>)MongodbUtils.findByCellIdAndGt(wirelessInfo,keys,values,"timestamp");
        return list;
    }

    @RequestMapping("/refreshEchartsDataByCellId")
    public WirelessInfo refreshEchartsDataByCellId(WirelessInfo wirelessInfo){
        String ulServiceCellId = wirelessInfo.getUlServiceCellId();
        String timestamp = wirelessInfo.getTimes();
        Date addTimes = null;
        Date endTimes = null;
        Date setTimes = null;
        long timess = 0;
        long nowTimestamp = new Date().getTime();
        if(timestamp==null){
            addTimes = new Date(nowTimestamp-echart.getRefreshs()*1000);
            endTimes = new Date(nowTimestamp-echart.getRefresh()*1000+1000);
            setTimes = new Date(nowTimestamp-echart.getRefresh()*1000);
        }else{
            SimpleDateFormat simdate1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                timess = simdate1.parse(timestamp).getTime();
                addTimes = new Date(timess);
                endTimes = new Date(timess+echart.getRefresh()*1000+1000);
                setTimes =  new Date(timess+echart.getRefresh()*1000);
            }catch (Exception e){
                log.info("refreshEchartsDataByCellId error:"+e.getMessage());
                return  null;
            }

        }
        if(timess>nowTimestamp-echart.getRefresh()*1000*2){
            timess = nowTimestamp-echart.getRefresh()*1000*2;
            endTimes = new Date(timess+echart.getRefresh()*1000+1000);
            setTimes =  new Date(timess+echart.getRefresh()*1000);
        }
//        Date sssd = new Date();
        String[] keys = {"ulServiceCellId","timestamp"};
        Object[]  values = {ulServiceCellId,addTimes};
//        Object[]  values2 = {ulServiceCellId,sssd};
        WirelessInfo wirelessInfo1 = null;
        List<? extends Object> result = MongodbUtils.findOneByCellIdAndTimestamp(wirelessInfo,keys,values,endTimes,"timestamp");
        if(result!=null&&result.size()>0){
            wirelessInfo1 =  (WirelessInfo)(result.get(0));
            wirelessInfo1.setTimestamp(setTimes);
        }
//        WirelessInfo wirelessInfo2 = (WirelessInfo)MongodbUtils.findOne(wirelessInfo,keys,values2);
        if(wirelessInfo1==null){
            wirelessInfo1 = new WirelessInfo();
            wirelessInfo1.setUlServiceCellId(wirelessInfo.getUlServiceCellId());
            wirelessInfo1.setTimestamp(setTimes);
        }
        return wirelessInfo1;
    }

    private boolean isSeam(HeavyLoadParam heavyLoadParamn,HeavyLoadParam heavyLoadParamo){

        return  false;
    }
}
