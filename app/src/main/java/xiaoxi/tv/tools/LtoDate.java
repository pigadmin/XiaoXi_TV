package xiaoxi.tv.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LtoDate {

	static SimpleDateFormat yMd = new SimpleDateFormat("yyyy/MM/dd");
	static SimpleDateFormat Hm = new SimpleDateFormat("HH:mm");
	static SimpleDateFormat Md = new SimpleDateFormat("MM/dd");
	static SimpleDateFormat E = new SimpleDateFormat("E");

	static SimpleDateFormat HMmd= new SimpleDateFormat("HH:mm MM/dd");
	static SimpleDateFormat yMdHmE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss E");


	public static String yMd(long dates) {
		Date date = new Date(dates);
		return yMd.format(date);

	}

	public static String Hm(long dates) {
		Date date = new Date(dates);
		return Hm.format(date);

	}

	public static String Md(long dates) {
		Date date = new Date(dates);
		return Md.format(date);

	}

	public static String E(long dates) {
		Date date = new Date(dates);
		return E.format(date);

	}

	public static String yMdHmE(long dates) {
		Date date = new Date(dates);
		return yMdHmE.format(date);

	}

	public static String HMmd(long dates) {
		Date date = new Date(dates);
		return HMmd.format(date);

	}

}
