package xiaoxi.tv.bean;

import java.io.Serializable;

public class Logo implements Serializable{
    private int id;

    private String logoPath;

    private String bgPath;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getLogoPath() {
        return this.logoPath;
    }

    public void setBgPath(String bgPath) {
        this.bgPath = bgPath;
    }

    public String getBgPath() {
        return this.bgPath;
    }
}
