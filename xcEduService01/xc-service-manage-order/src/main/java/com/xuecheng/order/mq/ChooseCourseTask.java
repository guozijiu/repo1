package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ChooseCourseTask {
    private static final Logger LOGGER= (Logger) LoggerFactory.getLogger(ChooseCourseTask.class);
    @Autowired
    TaskService taskService;

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask){
        if (xcTask!=null && StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask.getId());
        }
    }


    //定时发送加选课任务
    @Scheduled(cron = "0/3 * * * * *")
    public void sentChoosecourseTask(){
        //得到1分钟之前的时间
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList((Data) time, 100);
        System.out.println(xcTaskList);
        //调用service发布消息,将添加选课的任务发送给mq
        for (XcTask xcTask:xcTaskList ){
            if (taskService.getTask(xcTask.getId(), xcTask.getVersion())>0){
                String ex = xcTask.getMqExchange();//要发送的交换机
                String routingkey = xcTask.getMqRoutingkey();//发送消息要带的routingKey
                taskService.publish(xcTask,ex,routingkey);
            }
        }
    }

    //定义任务调试策略
    //@Scheduled(cron = "0/3* * * * *")//每隔3秒去执行
    public void task1(){
        LOGGER.info("==========测试开始======");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("==========测试定时任务结束======");
    }

}
