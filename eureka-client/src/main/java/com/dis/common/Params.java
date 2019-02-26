package com.dis.common;

/**
 * @ClassName: Params
 * @Description: 公共常量值
 * @author JunWang
 * @date 2017年6月16日 上午11:23:45
 * 
 */
public class Params {

    /**
     * txt文档中解析出的字段名
     */
    public static final String[] VISITOR_COLUMNS={"date","ipv4","ipv6","acr","eci","gender","age","localAddress","homeAddress",
            "homeAddressCI","workAddress","workAddressCI","expendAbility"};
    
    public static final String[] LAC_COLUMNS={"lac","city"};
    
    /**
     * session存活时间，秒
     */
    public static final int SESSION_SAVA_TIME = 30;

    public static final String LOCATION = "bi_location_";

    public static final String SHOPLOCATION = "bi_static_shop_";
    

    public static final String STORELOCATION = "bi_static_store_";  

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String DD = "dd";

    public static final String YYYYMM = "yyyyMM";

    public static final String YYYYMM00 = "yyyy-MM-00";

    public static final String YYYYMMM = "yyyy-MM";

    public static final String YYYYMMDD2 = "yyyy-MM-dd";

    public static final String YYMMDD = "yy/MM/dd";

    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";

    public static final String YYYYMMDDHH = "yyyyMMddHH";

    public static final String YYYYMMddHH00 = "YYYY-MM-dd HH:00:00";

    public static final String YYYYMMdd0000 = "YYYY-MM-dd 00:00:00";

    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String EEEE = "EEEE";

    public static final String MMDD = "MM月dd日";

    /**
     * 成功获取数据
     */
    public static final int RETURN_CODE_200 = 200;

    /**
     * 授权登录成功，但是是第一次授权，需要再传用户信息
     */
    public static final int RETURN_CODE_201 = 201;

    /**
     * 参数错误
     */
    public static final int RETURN_CODE_300 = 300;

    /**
     * 不在登录状态
     */
    public static final int RETURN_CODE_301 = 301;

    /**
     * 手机号或邮箱已经存在
     */
    public static final int RETURN_CODE_302 = 302;
    
    /**
     * 失败（通用）
     */
    public static final int RETURN_CODE_400 = 400;

    public static final int RETURN_CODE_500 = 500;

    /**
     * @Fields RETURN_KEY_ERROR : 返回值key，代表错误
     */
    public static final String RETURN_KEY_ERROR = "error";

    /**
     * @Fields RETURN_KEY_DATA : 返回值key，代表数据
     */
    public static final String RETURN_KEY_DATA = "data";
}
