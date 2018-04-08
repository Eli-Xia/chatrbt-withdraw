package net.monkeystudio.chatrbtw.local;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 本地化标签
 * @author hebo
 *
 */
public class Msg {

	private static ResourceBundle rb = ResourceBundle.getBundle("net.monkeystudio.chatrbtw.local.Msg", Locale.CHINA);
	
	public final static String text(String key, String... args) {
		if (args == null) {
			return rb.getString(key);
		}
		else {
			return MessageFormat.format(rb.getString(key), (Object[]) args);
		}
	}
}
