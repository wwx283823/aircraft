package com.dis.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Aspect
@Component
@Slf4j
public class GlobalWebLog {

    @Pointcut("execution(public * com.dis.*.*.*(..))")
    public void myWebLog(){

    }

    //AOP 前置通知
    @Before("myWebLog()")
    public void  doBefore(JoinPoint joinPoint) throws Throwable{
        //接收到的请求，记录请求结果
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        //记录请求内容
        log.info("url:"+httpServletRequest.getRequestURL().toString());
        log.info("HTTP_METHOD:"+httpServletRequest.getMethod());
        log.info("IP::"+httpServletRequest.getRemoteAddr());
        Enumeration<String> enumeration = httpServletRequest.getParameterNames();
        while (enumeration.hasMoreElements()){
            String name = (String)enumeration.nextElement();
            log.info("name:"+name+",value:"+httpServletRequest.getParameter(name));
        }
    }
    //AOP 后置通知
    @AfterReturning(returning = "ret",pointcut = "myWebLog()")
    public void doAfterReturn(Object ret) throws Throwable{
        //处理完请求返回内容
        log.info("return result:"+ret);
    }
}
