package org.sw.worker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mks.api.CmdRunner;
import com.mks.api.Command;
import com.mks.api.IntegrationPoint;
import com.mks.api.IntegrationPointFactory;
import com.mks.api.Session;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.util.APIVersion;

@SpringBootApplication
@RestController
@Configuration
@EnableScheduling
public class Worker implements SchedulingConfigurer {

    @Resource
    private ApplicationContext appContext;

    @Resource
    private WorkerArgs args;
	
    public static void main(String[] args) {
    	SpringApplication.run(Worker.class, args);
    }

    /**
     * 获取服务器当前时间戳
     * 
     * @return
     */
    @GetMapping("/worker/timestamp")
    public EchoDTO getServerTimestamp() {
        long epochMilli = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        EchoDTO echoDTO = new EchoDTO();
        echoDTO.setTimestamp(epochMilli);
        return echoDTO;
    }

    /**
     * 获取 worker 启动参数
     * 
     * @return
     */
    @GetMapping("/worker/args")
    public WorkerArgs getWorkerArgs() {
        return args;
    }

    /**
     * 查询 RVS 信息
     * 
     * @return
     * @throws APIException
     */
    @GetMapping("/worker/rvsinfo")
    public AboutDTO getRVSInfo() throws APIException {
        IntegrationPoint ip = appContext.getBean(IntegrationPoint.class);
        Session session = ip.createNamedSession("worker", APIVersion.API_4_16, args.getUser(), args.getPasswordValue());
        Command command = new Command("im", "about");
        CmdRunner runner = session.createCmdRunner();
        Response response = runner.execute(command);
        WorkItem workitem = response.getWorkItems().next();
        String title = workitem.getField("title").getValueAsString();
		String apiversion = workitem.getField("apiversion").getValueAsString();
		String version = workitem.getField("version").getValueAsString();
		String build = workitem.getField("build").getValueAsString();
		String patchLevel = workitem.getField("patch-level").getValueAsString();
        AboutDTO aboutDTO = new AboutDTO();
        aboutDTO.setTitle(title);
        aboutDTO.setApiversion(apiversion);
        aboutDTO.setVersion(version);
        aboutDTO.setBuild(build);
        aboutDTO.setPatchLevel(patchLevel);
        release(runner);
        release(session);
        return aboutDTO;
    }

    /**
     * 注册定时任务
     * 
     * @param taskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        JobWapper job = appContext.getBean(JobWapper.class);
        if(args.isImmediate()){
            job.run();
        }
        taskRegistrar.addTriggerTask(job, triggerContext -> {
            return new CronTrigger(args.getCron()).nextExecutionTime(triggerContext);
        });
    }

    @Bean("rvs")
    public IntegrationPoint getIntegrationPoint() {
        try {
            String[] token = args.getDatasource().split(":");
            IntegrationPoint ip = IntegrationPointFactory.getInstance().createIntegrationPoint(token[0], Integer.valueOf(token[1]), APIVersion.API_4_16);
            return ip;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void release(CmdRunner runner) {
        try {
            runner.release();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    private void release(Session session) {
        try {
            session.release();
        } catch (IOException | APIException e) {
            e.printStackTrace();
        }
    }
}
