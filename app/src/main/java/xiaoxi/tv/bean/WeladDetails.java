package xiaoxi.tv.bean;

import java.io.Serializable;

public class WeladDetails implements Serializable {
    private int id;

    private int aid;

    private int uid;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getAid() {
        return this.aid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return this.uid;
    }

}
