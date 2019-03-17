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
        if(heavyLoadParamList.size()>0){
            for(HeavyLoadParam heavyLoadParam1:heavyLoadParamList){
                MongodbUtils.remove(heavyLoadParam1);
            }

        }
        MongodbUtils.save(heavyLoadParam);
        String result1 = "null";
        String result2 = "null";
        String result = "null";
        if(heavyLoadParam.getType1()==1){
            heavyLoadParam.setType(1);
            result1 = subscriptionService.hperfdef(sva,heavyLoadParam);
        }
        if(heavyLoadParam.getType2()==1){
            heavyLoadParam.setType(2);
            result2 = subscriptionService.hperfdef(sva,heavyLoadParam);
        }
        if(heavyLoadParam.getType()==1){
            heavyLoadParam.setType(0);
            result = subscriptionService.hperfdef(sva,heavyLoadParam);
        }

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
        String type[] = {"fcnuser","user","interference"};
        String result = subscriptionService.hperfrecord(sva,type);
        return result;
    }
    @RequestMapping("/getNewHistory")
    public List<HighHeavyLoadHistory> getNewHistory(){
        List<HighHeavyLoadHistory> highHeavyLoadHistories = (List<HighHeavyLoadHistory>) MongodbUtils.findAll(new HighHeavyLoadHistory());
        return highHeavyLoadHistories;
    }
    @RequestMapping("/getHistoryBigTalkByCellId")
    public List<HeavyLoadParam> getHistoryBigTalkByCellId(HeavyLoadParam heavyLoad){
        HeavyLoadParam heavyLoadParam = null;
        if(heavyLoadParam==null){
            heavyLoadParam = new HeavyLoadParam();
        }
        String[] keys = {"cellId"};
        String[]  values = {heavyLoad.getCellId()};
        List<HeavyLoadParam> list = (List<HeavyLoadParam>) MongodbUtils.find(heavyLoadParam,keys,values,"HeavyLoadParamHistory");
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
        if(timestamp==null){
            addTimes = new Date(new Date().getTime()-echart.getRefreshs()*1000);
        }else{
            SimpleDateFormat simdate1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                addTimes = new Date(simdate1.parse(timestamp).getTime()+echart.getRefresh()*1000);

            }catch (Exception e){
                log.info("refreshEchartsDataByCellId error:"+e.getMessage());
                return  null;
            }

        }
        Date sssd = new Date();
        String[] keys = {"ulServiceCellId","timestamp"};
        Object[]  values = {ulServiceCellId,addTimes};
        Object[]  values2 = {ulServiceCellId,sssd};
        WirelessInfo wirelessInfo1 = (WirelessInfo)MongodbUtils.findOne(wirelessInfo,keys,values);
        WirelessInfo wirelessInfo2 = (WirelessInfo)MongodbUtils.findOne(wirelessInfo,keys,values2);
        if(wirelessInfo1==null){
            wirelessInfo1 = new WirelessInfo();
            wirelessInfo1.setUlServiceCellId(wirelessInfo.getUlServiceCellId());
            wirelessInfo1.setTimestamp(addTimes);
        }
        return wirelessInfo1;
    }
}
