package xiaoxi.tv.bean;

import java.io.Serializable;
import java.util.List;

public class AdList implements Serializable {
    private long endtime;

    private String position;

    private int id;

    private long begintime;

    private String name;

    private String ranges;

    private int type;

    private List<AdEntities> adEntities ;

    public void setEndtime(long endtime){
        this.endtime = endtime;
    }
    public long getEndtime(){
        return this.endtime;
    }
    public void setPosition(String position){
        this.position = position;
    }
    public String getPosition(){
        return this.position;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setBegintime(long begintime){
        this.begintime = begintime;
    }
    public long getBegintime(){
        return this.begintime;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setRanges(String ranges){
        this.ranges = ranges;
    }
    public String getRanges(){
        return this.ranges;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setAdEntities(List<AdEntities> adEntities){
        this.adEntities = adEntities;
    }
    public List<AdEntities> getAdEntities(){
        return this.adEntities;
    }


}
