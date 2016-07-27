package cn.zto.example;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component 
public class QueryJob {
	
	private static int i = 0;
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void query(){
		i++;
		log.info("XXX log :"+  i + "£¬Ê±¼ä :" +new Date());
	}

}