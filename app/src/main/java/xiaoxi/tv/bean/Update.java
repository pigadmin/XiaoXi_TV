package xiaoxi.tv.bean;

import java.io.Serializable;

public class Update implements Serializable {
    private int id;

    private String name;

    private String path;

    private String beginTime;

    private String endTime;

    private double version;

    private int type;

    private int gid;

    private String gName;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getBeginTime() {
        return this.beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public double getVersion() {
        return this.version;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getGid() {
        return this.gid;
    }

    public void setGName(String gName) {
        this.gName = gName;
    }

    public String getGName() {
        return this.gName;
    }
}
