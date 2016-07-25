package cn.zto.example;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QueryJob extends QuartzJobBean{
	
	private static int i = 0;
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void query(){
		i++;
		log.info(" log :"+  i + "£¬Ê±¼ä :" +new Date());
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		query();
	}
}