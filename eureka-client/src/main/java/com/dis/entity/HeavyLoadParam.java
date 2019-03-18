package com.dis.entity;

import com.dis.common.Util;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "HeavyLoadParam")
@Data
public class HeavyLoadParam implements Serializable  {

    @Id
    private String  _id;
    //0:频点间基于用户数的快速负载均衡 1：基于干扰的快速负载均衡 2：基于用户数的快速调整
    //0参数：上行prb利用率门限，下行prb利用率门限，用户数开关，策略单次调整用户数步长
    //1参数：最大上行干扰门限，用户数开关,RSRP门限开关,RS功率开关,策略单次调整用户数步长,策略单次调整psrp门限步长,策略单词调整rs功率步长
    //2参数：小区最大用户数门限，与邻区用户数超出比例，上行prb利用率门限，用户数开关,RS功率开关,策略单次调整用户数步长,策略单次调整psrp门限步长
    //用户数开关,RSRP门限开关,RS功率开关，三个参数值依次拼接成一个二进制数，比如：用户数开关=1，RSRP门限开关=0，RS功率开关=1，则二进制数为101，
    // 在转换成十进制数，作为参数effecttype的值传上去，effecttype:5
    private int type;
    private int type1;
    private int type2;
    //调整策略
    private int effecttype;
    //最大上行干扰门限
    private int ulcellmaxinterference; //-115dbm
    //上行prb利用率门限
    private int ulrbmaxrate;

    private int ulrbmaxratek;
    //下行prb利用率门限
    private int dlrbmaxrate;
    //用户数开关
    private int usercntsw; //0 关闭，1 开启

    //用户数开关
    private int usercntswg; //0 关闭，1 开启

    //用户数开关
    private int usercntswk; //0 关闭，1 开启

    //RSRP门限开关
    private int rsrpdeltasw; //0 关闭，1 开启
    //RS功率开关
    private int rspwrdeltasw; //0 关闭，1 开启

    //RS功率开关
    private int rspwrdeltaswg; //0 关闭，1 开启
    //策略单次调整用户数步长
    private int usercnt;

    private int usercntg;

    private int usercntk;
    //策略单次调整psrp门限步长
    private int rsrpdelta;
    //策略单词调整rs功率步长
    private int rspwrdelta;

    private int rspwrdeltag;
    //小区最大用户数门限
    private int maxcelluser;
    //与邻区用户数超出比例
    private int neibouruserrate;
    //三种状态的开关  0 关闭，1开启
    private int openClose;

    private String cellId;
    //获取参数
    public  String getParam(){
        String param = null;
        String idtype = null;
        if(type==0){
            param = String.valueOf(this.usercntsw);
            idtype = ",\"idtype\":\"fcnuser\""+",\"userBalcSwitch\":\""+this.openClose+"\",\"ulrbmaxrate\":\""+this.ulrbmaxrate+"\""+",\"dlrbmaxrate\":\""
                    +this.dlrbmaxrate+"\""+",\"usercnt\":\""+this.usercnt+"\"";
        }else if(type==1){
            param = String.valueOf(this.usercntswg)+String.valueOf(this.rsrpdeltasw)+String.valueOf(this.rspwrdeltaswg);;
            idtype = ",\"idtype\":\"interference\""+",\"interferenceSW\":\""+this.openClose+"\",\"ulcellmaxinterference\":\""+this.ulcellmaxinterference+"\""
                    +",\"usercnt\":\""+this.usercntg+"\""+",\"rsrpdelta\":\""+this.rsrpdelta+"\""+",\"rspwrdelta\":\""+this.rspwrdeltag+"\"";
        }else if(type==2){
            param = String.valueOf(this.usercntswk)+0+String.valueOf(this.rspwrdeltasw);
            idtype = ",\"idtype\":\"user\""+",\"userCntSW\":\""+this.openClose+"\",\"maxcelluser\":\""+this.maxcelluser+"\""+",\"neibouruserrate\":\""
                    +this.neibouruserrate+"\",\"ulrbmaxrate\":\""+this.ulrbmaxratek+"\""+",\"rspwrdeltasw\":\""+this.rspwrdeltasw+"\""+",\"usercnt\":\""
                    +this.usercntk+"\""+",\"rspwrdelta\":\""+this.rspwrdelta+"\"";
        }else {
            return null;
        }
        this.effecttype = Util.binaryToDecimal(param);
        String result =idtype+",\"effecttype\":\""+this.effecttype+"\"}";
        return  result;
    }

    //获取参数
    public  String getIntParam(){
        String param = null;
        String idtype = null;
        if(type==0){
            param = String.valueOf(this.usercntsw);
            idtype = ",\"idtype\":\"fcnuser\""+",\"userBalcSwitch\":"+this.openClose+",\"ulrbmaxrate\":"+this.ulrbmaxrate+",\"dlrbmaxrate\":"
                    +this.dlrbmaxrate+",\"usercnt\":"+this.usercnt;
        }else if(type==1){
            param = String.valueOf(this.rspwrdeltasw)+String.valueOf(this.rsrpdeltasw)+String.valueOf(this.usercntsw);;
            idtype = ",\"idtype\":\"interference\""+",\"interferenceSW\":"+this.openClose+",\"ulcellmaxinterference\":"+this.ulcellmaxinterference
                    +",\"usercnt\":"+this.usercnt+",\"rsrpdelta\":"+this.rsrpdelta+",\"rspwrdelta\":"+this.rspwrdelta;
        }else if(type==2){
            param = String.valueOf(this.rspwrdeltasw)+String.valueOf(this.usercntsw);
            idtype = ",\"idtype\":\"user\""+",\"userCntSW\":"+this.openClose+",\"maxcelluser\":"+this.maxcelluser+",\"neibouruserrate\":"
                    +this.neibouruserrate+",\"rspwrdeltasw\":"+this.rspwrdeltasw+",\"usercnt\":"
                    +this.usercnt+",\"rspwrdelta\":"+this.rspwrdelta;
        }else {
            return null;
        }
        this.effecttype = Util.binaryToDecimal(param);
        String result =idtype+",\"effecttype\":"+this.effecttype+"}";
        return  result;
    }

}
