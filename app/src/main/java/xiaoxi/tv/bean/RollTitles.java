package xiaoxi.tv.bean;

import java.io.Serializable;

public class RollTitles implements Serializable {
    private int id;

    private String title;

    private int status;

    private String content;

    private long starttime;

    private long endtime;

    private int type;

    private String targetAgent;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return this.content;
    }
    public void setStarttime(long starttime){
        this.starttime = starttime;
    }
    public long getStarttime(){
        return this.starttime;
    }
    public void setEndtime(long endtime){
        this.endtime = endtime;
    }
    public long getEndtime(){
        return this.endtime;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setTargetAgent(String targetAgent){
        this.targetAgent = targetAgent;
    }
    public String getTargetAgent(){
        return this.targetAgent;
    }
}
