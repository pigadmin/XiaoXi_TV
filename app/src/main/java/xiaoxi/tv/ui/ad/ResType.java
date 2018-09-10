package xiaoxi.tv.ui.ad;

import java.util.HashMap;
import java.util.Map;

public class ResType {
    public static Map<String, Integer> type = new HashMap<String, Integer>();

    static {
        // 1 image
        type.put(".bmp", 1);
        type.put(".jpeg", 1);
        type.put(".jpg", 1);
        type.put(".png", 1);
        // 2 audio
        type.put(".mp3", 2);
        type.put(".wav", 2);
        // 3 video
        type.put(".3gp", 3);
        type.put(".wmv", 3);
        type.put(".asf", 3);
        type.put(".avi", 3);
        type.put(".mov", 3);
        type.put(".mp4", 3);
        type.put(".mpeg", 3);
        type.put(".mpg", 3);
        type.put(".mkv", 3);
        type.put(".ts", 3);
        type.put(".rmvb", 3);
        type.put(".flv", 3);
        // 4 text
        type.put(".txt", 4);
        type.put(".text", 4);
        // 5
        type.put(".docx", 5);
        type.put(".doc", 5);
        type.put(".pptx", 5);
        type.put(".ppt", 5);
        type.put(".xlsx", 5);
        type.put(".xls", 5);
    }
}