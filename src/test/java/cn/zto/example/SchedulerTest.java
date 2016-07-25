package cn.zto.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:*applicationContext.xml"})
public class SchedulerTest {
	
	@Test
	public void test() throws Exception{
		Thread.sleep(20*1000);
	}
	
}