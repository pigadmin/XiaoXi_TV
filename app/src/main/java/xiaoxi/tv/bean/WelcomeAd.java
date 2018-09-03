package xiaoxi.tv.bean;

import java.io.Serializable;
import java.util.List;

public class WelcomeAd implements Serializable {
    private int id;

    private String name;

    private int type;

    private String filePath;

    private String bgFile;

    private int inter;

    private int position;

    private int uType;

    private int adType;

    private String targetAgent;

    private List<WeladDetails> weladDetails;

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

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setBgFile(String bgFile) {
        this.bgFile = bgFile;
    }

    public String getBgFile() {
        return this.bgFile;
    }

    public void setInter(int inter) {
        this.inter = inter;
    }

    public int getInter() {
        return this.inter;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public void setUType(int uType) {
        this.uType = uType;
    }

    public int getUType() {
        return this.uType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getAdType() {
        return this.adType;
    }

    public void setTargetAgent(String targetAgent) {
        this.targetAgent = targetAgent;
    }

    public String getTargetAgent() {
        return this.targetAgent;
    }

    public void setWeladDetails(List<WeladDetails> weladDetails) {
        this.weladDetails = weladDetails;
    }

    public List<WeladDetails> getWeladDetails() {
        return this.weladDetails;
    }


}
