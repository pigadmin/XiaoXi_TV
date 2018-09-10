package xiaoxi.tv.ui.ad.bean;

import java.io.Serializable;

public class Command implements Serializable {
    private Play play;

    private int command;

    public void setPlay(Play play) {
        this.play = play;
    }

    public Play getPlay() {
        return this.play;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getCommand() {
        return this.command;
    }

}
