package com.dis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "heavyLoad")
@Data
public class HeavyLoad  implements Serializable  {

    @Id
    private String  _id;

    private Date timestamp;

    private Long svcCellId;
    //用户数
    private Long userCnt;

    private String cellId;

    //干扰率
    private Long cellInterference;
    //上行rpr利用率
    private Long ucULRbRate;
    //下行rpr利用率
    private Long ucDLRbRate;
}
