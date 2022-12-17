package org.sw.worker;

import java.util.List;
import java.util.logging.Logger;
import com.mks.api.Session;
import com.mks.api.response.WorkItem;

public class Job {

    private static Logger log = WLogger.getLogger(Job.class);

    protected List<WorkItem> resultSet;
    protected Session session;

    public Job(List<WorkItem> resultSet, Session session) {
        this.resultSet = resultSet;
        this.session = session;
    }


    public void execute() throws Exception{
        log.info("resultSet.size = " + resultSet.size());
        // TODO 具体的计算逻辑写在这里
    }
}
