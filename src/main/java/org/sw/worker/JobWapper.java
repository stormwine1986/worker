package org.sw.worker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mks.api.CmdRunner;
import com.mks.api.Command;
import com.mks.api.IntegrationPoint;
import com.mks.api.Option;
import com.mks.api.Session;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.api.util.APIVersion;

/**
 * worker 的工作
 * 
 */
@Component
public class JobWapper implements Runnable {

    private static Logger log = WLogger.getLogger(JobWapper.class);

    private boolean idle = true;

    private long lastStartupDate = 0L;

    private long lastExecuteCost = 0L;

    @Resource
    private IntegrationPoint ip;

    @Resource
    private WorkerArgs args;

    @Override
    public void run() {
        idle = false;
        lastStartupDate = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
        log.info("START-UP," + LocalDateTime.now().toString());
        CmdRunner runner = null;
        Session session = null;
        try {
            session = ip.createNamedSession("worker", APIVersion.API_4_16, args.getUser(), args.getPasswordValue());
            runner = session.createCmdRunner(); 
            Command command = new Command("im", "issues");
            if(args.getQuery().startsWith("Worker:")){
                // 使用预定义查询
                command.addOption(new Option("query",args.getQuery()));
            } else {
                command.addOption(new Option("queryDefinition", args.getQuery()));
            }
            Response response = runner.execute(command);
            WorkItemIterator workItems = response.getWorkItems();
            List<WorkItem> resultSet = new ArrayList<>();
            while(workItems.hasNext()){
                resultSet.add(workItems.next());
            }
            // 执行任务
            Job job = new Job(resultSet, session);
            job.execute();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }finally{
            if(runner!=null) release(runner);
            if(session!=null) release(session);
            idle = true;
            lastExecuteCost = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).getEpochSecond() - lastStartupDate;
            log.info("Execute-Cost(s)," + lastExecuteCost);
        }
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

    public boolean isIdle() {
        return idle;
    }

    public long getLastStartupDate() {
        return lastStartupDate;
    }
}
