package xiaoxi.tv.bean;

import java.io.Serializable;

public class Auth implements Serializable {
    private int id;

    private String name;

    private int groupId;

    private String mac;

    private int status;

    private String version;

    private int online;

    private long connectTime;

    private long disconectTime;

    private String wifi;

    private String ip;

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

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return this.mac;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return this.online;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public long getConnectTime() {
        return this.connectTime;
    }

    public void setDisconectTime(long disconectTime) {
        this.disconectTime = disconectTime;
    }

    public long getDisconectTime() {
        return this.disconectTime;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getWifi() {
        return this.wifi;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

}
