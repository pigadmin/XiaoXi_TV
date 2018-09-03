package xiaoxi.tv.bean;

import java.io.Serializable;

public class AdEntities implements Serializable {
    private int appearWay;

    private int id;

    private String iconpath;

    private String ngPath;

    private int orders;

    private int adgroupid;

    private int playtime;

    public void setAppearWay(int appearWay) {
        this.appearWay = appearWay;
    }

    public int getAppearWay() {
        return this.appearWay;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setIconpath(String iconpath) {
        this.iconpath = iconpath;
    }

    public String getIconpath() {
        return this.iconpath;
    }

    public void setNgPath(String ngPath) {
        this.ngPath = ngPath;
    }

    public String getNgPath() {
        return this.ngPath;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public int getOrders() {
        return this.orders;
    }

    public void setAdgroupid(int adgroupid) {
        this.adgroupid = adgroupid;
    }

    public int getAdgroupid() {
        return this.adgroupid;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    public int getPlaytime() {
        return this.playtime;
    }
}
