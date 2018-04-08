package net.monkeystudio.base.utils;

import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by bint on 2017/10/31.
 */

public class Log {
    private static Logger log = Logger.getLogger("chatrbtw");

    static {
        //Level level = Level.FINE; // dev
//		Level level = Level.INFO; // prod
        //log.setLevel(level);

//		if (level.equals(Level.FINE)) {
//			Handler[] handlers = log.getHandlers();
//			for (Handler h : handlers) {
//				log.removeHandler(h);
//			}
//
//			ConsoleHandler handler = new ConsoleHandler();
//	        handler.setLevel(level);
//	        log.addHandler(handler);
//		}
    }

    private static String getCaller() {
        return "[" + Thread.currentThread().getStackTrace()[3].getClassName() + "] ";
    }

    private static String getTraceId() {
        long t = new Date().getTime();
        long seed = new Random().nextInt(100000);
        return t + "" + seed;
    }

    /**
     * error log
     *
     * @param s
     */
    private static String e(String s) {

        String traceId = getTraceId();
        log.error(getCaller() + " " + s + "[TraceID:" + traceId + "]");
        //StringBuilder sb = new StringBuilder();
        //sb.append(traceId).append(" ").append(getCaller()).append(s);
        //log.severe(sb.toString());

        return traceId;
    }

    /**
     * debug log
     *
     * @param logs
     */
    public static void d(String... logs) {

        String logInfo = replace(logs);
        log.debug(getCaller() + " " + logInfo);
        //if (log.getLevel() == Level.FINE) {
        //	log.info(getCaller() + s);
        //}
    }

    public static void d(Integer integer) {
        log.debug(getCaller() + " " + integer);
    }

    /**
     * info log
     *
     * @param s
     */
    public static void i(String s) {
        log.info(getCaller() + " " + s);
        //log.info(getCaller() + s);
    }


    public static void i(String... logs) {
        String logInfo = replace(logs);
        String traceId = getTraceId();
        log.info(getCaller() + " " + logInfo + "[TraceID:" + traceId + "]");
    }

    public static void w(String s) {
        log.warn(getCaller() + " " + s);
        //log.warning(getCaller() + s);
    }

    /**
     * 记录错误日志
     *
     * @param logs 传入 " ? has error"和"00700" 得到 " 00700 has error"
     * @return
     */
    public static String e(String... logs) {
        String logInfo = replace(logs);
        String traceId = getTraceId();
        log.error(getCaller() + " " + logInfo + "[TraceID:" + traceId + "]");

        return traceId;
    }

    private static String replace(String... strings) {
        if (strings.length == 0) {
            return null;
        }

        if (strings.length <= 1) {
            return strings[0];
        }

        String base = null;

        String result = "";

        for (int i = 0; i < strings.length; i++) {

            if (i == 0) {
                base = strings[0];
                continue;
            }

            Integer index = base.indexOf("?");

            if (index == -1) {
                continue;
            }

            String param = strings[i];

            base = replace(base, index, param);
        }

        return base;
    }

    public static String e(Exception exception) {

        StringBuilder sb = new StringBuilder();

        sb.append(exception.getClass().getName() + ": " + exception.getMessage() + "\n");

        for (StackTraceElement ele : exception.getStackTrace()) {
            sb.append(MessageFormat.format("\tat {0}.{1}({2}:{3})\n",
                    ele.getClassName(), ele.getMethodName(), ele.getFileName(), ele.getLineNumber()));
            ;
        }

        exception.printStackTrace();

        String traceId = getTraceId();
        log.error(getCaller() + " " + sb.toString() + "[TraceID:" + traceId + "]");

        return traceId;
    }

    /**
     * 把指定的位置的字符替换为指定字符串
     * @param source 原字符串
     * @param index 替换的字符的位置
     * @param target 要替换成的字符串
     * @return
     */
    private static String replace(String source , Integer index ,String target){
        String result = source.substring(0, index) + target +source.substring(index+1);
        return result;
    }

}
