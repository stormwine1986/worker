package org.sw.worker;

public class AboutDTO {
    
    private String title;
    private String apiversion;
    private String version;
    private String build;
    private String patchLevel;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getApiversion() {
        return apiversion;
    }
    public void setApiversion(String apiversion) {
        this.apiversion = apiversion;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getBuild() {
        return build;
    }
    public void setBuild(String build) {
        this.build = build;
    }
    public String getPatchLevel() {
        return patchLevel;
    }
    public void setPatchLevel(String patchLevel) {
        this.patchLevel = patchLevel;
    }

    
}
