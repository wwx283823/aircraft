package com.dis.controller;

import com.dis.common.MongodbUtils;
import com.dis.entity.HeavyLoad;
import com.dis.entity.HighHeavyLoad;
import com.dis.entity.WirelessInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class WirelessInfoController {

    @RequestMapping("/getWirelessInfoByParam")
    public List<WirelessInfo> getWirelessInfoByParam(HeavyLoad heavyLoad){
        List<String> listKey = new ArrayList<String>();
        List<Long> listValue = new ArrayList<Long>();
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
        if(listKey.size()>0&&listValue.size()>0){
            String[] keys = listKey.toArray(new String[listKey.size()]);
            Long[] values = listValue.toArray(new Long[listValue.size()]);
            List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findByGt(new WirelessInfo(),keys,values);
            return  wirelessInfoList;
        }else {
            return  null;
        }
    }

    @RequestMapping("/getWirelessInfos")
    public List<WirelessInfo> getWirelessInfos(){
        List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findAll(new WirelessInfo());
        return  wirelessInfoList;
    }

}
