package com.dis.service;

import org.omg.CORBA.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Entity;

@Service
public class ConsumerService {

    @Autowired
    RestTemplate restTemplate;


    public String getHi(String name){
       return restTemplate.getForObject("http://service-client/hi?name="+name,String.class);
    }

    public String getModel(){
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity("http://service-client/hiModel",Object.class);
        responseEntity.getBody();
        responseEntity.getStatusCode();
        responseEntity.getHeaders();
        return  responseEntity.getBody().toString();
    }
}
