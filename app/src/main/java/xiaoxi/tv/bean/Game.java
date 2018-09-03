package xiaoxi.tv.bean;

import java.io.Serializable;

public class Game implements Serializable {
    private int id;

    private String name;

    private String path;

    private String icon;

    private String package_name;

    private int enable;

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

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPackage_name() {
        return this.package_name;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getEnable() {
        return this.enable;
    }

}
