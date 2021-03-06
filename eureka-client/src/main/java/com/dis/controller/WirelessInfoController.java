package com.dis.controller;

import com.dis.common.MongodbUtils;
import com.dis.entity.Echart;
import com.dis.entity.HeavyLoad;
import com.dis.entity.HighHeavyLoad;
import com.dis.entity.WirelessInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class WirelessInfoController {

    @Autowired
    private Echart echart;

    @RequestMapping("/getWirelessInfoByParam")
    public List<WirelessInfo> getWirelessInfoByParam(HeavyLoad heavyLoad){
        List<HeavyLoad> lists = (List<HeavyLoad>)MongodbUtils.findAll(new HeavyLoad());
        if(lists.size()>0){
            for(HeavyLoad heavyLoad1:lists){
                MongodbUtils.remove(heavyLoad1);
            }
        }
        MongodbUtils.save(heavyLoad);
        List<String> listKey = new ArrayList<String>();
        List<Object> listValue = new ArrayList<Object>();
        if(heavyLoad.getUcDLRbRate()!=null){
            listKey.add("ucDLRbRate");
            listValue.add(heavyLoad.getUcDLRbRate());
        }
        if(heavyLoad.getCellInterference()!=null){
            listKey.add("ulCellInterference");
            listValue.add( heavyLoad.getCellInterference());
        }
        if(heavyLoad.getUcULRbRate()!=null){
            listKey.add("ucULRbRate");
            listValue.add(heavyLoad.getUcULRbRate());
        }
        if(heavyLoad.getUserCnt()!=null){
            listKey.add("usMaxUserNum");
            listValue.add(heavyLoad.getUserCnt());
        }
        listValue.add(new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-echart.getRefresh()*1000));
        listKey.add("timestamp");
        if(listKey.size()>0&&listValue.size()>0){
            String[] keys = listKey.toArray(new String[listKey.size()]);
            Object[] values = listValue.toArray(new Object[listValue.size()]);
            List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findByGt(new WirelessInfo(),keys,values);
            return  wirelessInfoList;
        }else {
            return  null;
        }
    }

    @RequestMapping("/getWirelessInfos")
    public List<WirelessInfo> getWirelessInfos(){
        String[]  key ={"timestamp"};
        Date s = new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-echart.getRefreshs()*1000*15);
        Object[] values = {s};
        log.info("getWirelessInfos data:"+s);
        List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findByGtDescAndLimit(new WirelessInfo(),key,values,"timestamp",echart.getSelectCount());
        return  wirelessInfoList;
    }

    @RequestMapping("/getWirelessInfoByCellId")
    public List<WirelessInfo> getWirelessInfoByCellId(HeavyLoad heavyLoad){
        String cellId = heavyLoad.getCellId();
        String[]  key ={"ulServiceCellId","timestamp"};
        Date s = new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-echart.getRefreshs()*1000*15);
        Object[] values = {cellId,s};
        List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findByCellIdAndDescAndLimit(new WirelessInfo(),key,values,"timestamp",echart.getSelectCount());
        return  wirelessInfoList;
    }

    @RequestMapping("/getHeavyLoadParam")
    public List<HeavyLoad> getHeavyLoadParam(HeavyLoad heavyLoad){
        List<HeavyLoad> lists = (List<HeavyLoad>)MongodbUtils.findAll(new HeavyLoad());
        return  lists;
    }

    @RequestMapping("/reWirelessInfoByParam")
    public List<WirelessInfo> reWirelessInfoByParam(HeavyLoad heavyLoad){
        List<String> listKey = new ArrayList<String>();
        List<Object> listValue = new ArrayList<Object>();
        if(heavyLoad.getUcDLRbRate()!=null){
            listKey.add("ucDLRbRate");
            listValue.add(heavyLoad.getUcDLRbRate());
        }
        if(heavyLoad.getCellInterference()!=null){
            listKey.add("ulCellInterference");
            listValue.add( heavyLoad.getCellInterference());
        }
        if(heavyLoad.getUcULRbRate()!=null){
            listKey.add("ucULRbRate");
            listValue.add(heavyLoad.getUcULRbRate());
        }
        if(heavyLoad.getUserCnt()!=null){
            listKey.add("usMaxUserNum");
            listValue.add(heavyLoad.getUserCnt());
        }
        listValue.add(new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-echart.getRefresh()*1000));
        listKey.add("timestamp");
        if(listKey.size()>0&&listValue.size()>0){
            String[] keys = listKey.toArray(new String[listKey.size()]);
            Object[] values = listValue.toArray(new Object[listValue.size()]);
            List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findByGt(new WirelessInfo(),keys,values);
            return  wirelessInfoList;
        }else {
            return  null;
        }
    }

}
