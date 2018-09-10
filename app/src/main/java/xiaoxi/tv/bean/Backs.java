package xiaoxi.tv.bean;

import java.io.Serializable;

public class Backs implements Serializable {
    private int id;

    private String path;

    private int type;

    private int inter;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setInter(int inter) {
        this.inter = inter;
    }

    public int getInter() {
        return this.inter;
    }

}
