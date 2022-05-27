package aliyunRAMTools.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.util.Assert;

/**
 * 日期工具类,多线程安全
 *
 * @author charles 2016年8月29日 下午5:01:10
 */
public class DateFormatter {

    public final static String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public final static String UTC_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateParser DATE_PARSER = FastDateFormat.getInstance(DEFAULT_DATE_FORMAT);
    private static final DateParser DATETIME_PARSER = FastDateFormat.getInstance(DEFAULT_DATETIME_FORMAT);
    private static final DateParser UTC_DATETIME_PARSER =
        FastDateFormat.getInstance(UTC_DATE_TIME_FORMAT, TimeZone.getTimeZone("UTC"));

    public static void main(String[] args) {
        System.out.println(parseDate("2020-10-12"));
        System.out.println(parseDate("2020-10-01"));
        System.out.println(parseDateTime("2020-10-01 10:10:10"));
        System.out.println(parseDateTime("2020-10-02 23:10:10"));
        System.out.println(parseUTCDateTime("2021-12-31T09:21:19Z"));
    }

    /**
     * 将字符串解析成Date类型
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static Date parseDate(String dateString) {
        Assert.hasText(dateString, "dateString can not be blank!");
        try {
            Date date = DATE_PARSER.parse(dateString);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 将字符串解析成Date类型,格式为DEFAULT_DATE_FORMAT
     *
     * @param dateTimeString
     * @return
     */
    public static Date parseDateTime(String dateTimeString) {
        Assert.hasText(dateTimeString, "dateString can not be blank!");
        try {
            Date date = DATETIME_PARSER.parse(dateTimeString);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Date parseUTCDateTime(String dateTimeString) {
        Assert.hasText(dateTimeString, "dateString can not be blank!");
        try {
            Date date = UTC_DATETIME_PARSER.parse(dateTimeString);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 将Date解析成String
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String toDateString(Date date, String pattern) {
        Assert.notNull(date, "date can not be null!");
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 将Date解析成String,格式为DEFAULT_DATE_FORMAT
     *
     * @param date
     * @return
     */
    public static String toDateString(Date date) {
        return toDateString(date, DEFAULT_DATETIME_FORMAT);
    }

}
