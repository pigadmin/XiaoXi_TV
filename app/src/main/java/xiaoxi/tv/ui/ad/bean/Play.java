package xiaoxi.tv.ui.ad.bean;

import java.io.Serializable;

public class Play implements Serializable {
    private int id;

    private int sid;

    private int type;

    private String sname;

    private int status;

    private String surl;

    private int stype;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setSid(int sid){
        this.sid = sid;
    }
    public int getSid(){
        return this.sid;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setSname(String sname){
        this.sname = sname;
    }
    public String getSname(){
        return this.sname;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setSurl(String surl){
        this.surl = surl;
    }
    public String getSurl(){
        return this.surl;
    }
    public void setStype(int stype){
        this.stype = stype;
    }
    public int getStype(){
        return this.stype;
    }
}
