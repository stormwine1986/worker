package org.sw.worker;

import java.beans.Transient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Worker 启动参数
 * 
 */
@Component
@ConfigurationProperties(prefix = "worker")
public class WorkerArgs {

    private String cron; // e.g. --worker.cron="* * 23 ? * ?"

    private String user; // --worker.user=admin

    private String password; // --worker.password=Casco123

    private String datasource; // --worker.datasource=192.100.230.54:7001

    private String query; // --worker.query="Worker:XXXXXXXXXXX"

    private boolean immediate = false ; // --worker.immediate=true

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return "***";
    }

    @Transient
    public String getPasswordValue() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }
}
