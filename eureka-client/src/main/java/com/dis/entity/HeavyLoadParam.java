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
    //2参数：小区最大用户数门限，与邻区用户数超出比例，上行prb利用率门限，用户数开关,RSRP门限开关,策略单次调整用户数步长,策略单次调整psrp门限步长
    //用户数开关,RSRP门限开关,RS功率开关，三个参数值依次拼接成一个二进制数，比如：用户数开关=1，RSRP门限开关=0，RS功率开关=1，则二进制数为101，
    // 在转换成十进制数，作为参数effecttype的值传上去，effecttype:5
    private int type;
    //调整策略
    private int effecttype;
    //最大上行干扰门限
    private String ulcellmaxinterference; //-115dbm
    //上行prb利用率门限
    private String ulrbmaxrate;
    //下行prb利用率门限
    private String dlrbmaxrate;
    //用户数开关
    private int usercntsw; //0 关闭，1 开启
    //RSRP门限开关
    private int rsrpdeltasw; //0 关闭，1 开启
    //RS功率开关
    private int rspwrdeltasw; //0 关闭，1 开启
    //策略单次调整用户数步长
    private int usercnt;
    //策略单次调整psrp门限步长
    private int rsrpdelta;
    //策略单词调整rs功率步长
    private int srspwrdeltasw;
    //小区最大用户数门限
    private int maxcelluser;
    //与邻区用户数超出比例
    private String neibouruserrate;

    //获取参数
    public  String getParam(){
        String param = null;
        String idtype = null;
        if(type==0){
            param = String.valueOf(this.usercntsw);
            idtype = ",\"idtype\":\"fcnuser\""+",\"ulrbmaxrate\":\""+this.ulrbmaxrate+"\""+",\"dlrbmaxrate\":\""
                    +this.dlrbmaxrate+"\""+",\"usercnt\":\""+this.usercnt+"\"";
        }else if(type==1){
            param = String.valueOf(this.usercntsw)+String.valueOf(this.rsrpdeltasw)+String.valueOf(this.rspwrdeltasw);;
            idtype = ",\"idtype\":\"interference\""+",\"ulcellmaxinterference\":\""+this.ulcellmaxinterference+"\""
                    +",\"usercnt\":\""+this.usercnt+"\""+",\"rsrpdelta\":\""+this.rsrpdelta+"\""+",\"srspwrdeltasw\":\""+this.srspwrdeltasw+"\"";
        }else if(type==2){
            param = String.valueOf(this.usercntsw)+String.valueOf(this.rsrpdeltasw);
            idtype = ",\"idtype\":\"user\""+",\"maxcelluser\":\""+this.maxcelluser+"\""+",\"neibouruserrate\":\""
                    +this.neibouruserrate+"\""+",\"ulrbmaxrate\":\""+this.ulrbmaxrate+"\""+",\"usercnt\":\""
                    +this.usercnt+"\""+",\"rsrpdelta\":\""+this.rsrpdelta+"\"";
        }else {
            return null;
        }
        this.effecttype = Util.binaryToDecimal(param);
        String result =idtype+",\"effecttype\":\""+this.effecttype+"\"}";
        return  result;
    }

}
