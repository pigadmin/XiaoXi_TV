package xiaoxi.tv.bean;

import java.io.Serializable;

public class RollTitles implements Serializable {
    private long endtime;

    private String content;

    private int id;

    private int speed;

    private String color;

    private int status;

    private long begintime;

    private String name;

    private String gName;


    private int type;

    private int size;

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getEndtime() {
        return this.endtime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setBegintime(long begintime) {
        this.begintime = begintime;
    }

    public long getBegintime() {
        return this.begintime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setGName(String gName) {
        this.gName = gName;
    }

    public String getGName() {
        return this.gName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

}
