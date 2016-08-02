package cn.zto.example.service;

import java.util.Date;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryJob{
	
	private static int i = 0;
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
    public void query() throws JobExecutionException  
    {  
		i++;
		log.info("这是1111111111111111跑的log :"+  i + "，时间 :" +new Date());
    }  
	
}