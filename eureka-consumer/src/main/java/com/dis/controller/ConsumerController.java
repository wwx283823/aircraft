package com.dis.controller;

import com.dis.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    ConsumerService consumerService;

    @RequestMapping("/consumerController")
    public String getConsumer(@RequestParam("name") String name){
            return consumerService.getHi(name);
    }

    @RequestMapping("/consumerModel")
    public String getConsumer(){
        return consumerService.getModel();
    }
}
