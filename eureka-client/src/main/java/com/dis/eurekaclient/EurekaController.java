package com.dis.eurekaclient;

import com.dis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class EurekaController {

    @Value("${server.port}")
    private String port;

    @RequestMapping("/hi")
    public String home(@RequestParam String name)
    {
        log.info("get hi,port"+port);
        return "hi " + name + ",i am from port:" + port;
    }

    @RequestMapping("/hiModel")
    public Map<String,Object> getModel()
    {
        User user = new User();
        user.setAge(22);
        user.setName("王俊");
        user.setPort(port);
        Map<String,Object> maps = new HashMap<String,Object>();
        maps.put("data",user);
        maps.put("status",200);
        log.info("get hiModel,port"+port);
        return  maps;
    }
}

