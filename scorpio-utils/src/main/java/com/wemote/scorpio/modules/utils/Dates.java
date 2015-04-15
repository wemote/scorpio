package com.wemote.scorpio.modules.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/**
 * Date utils.
 * @author : jayon.xu@gmail.com
 */
public class Dates {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Dates.class);
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 缺省的日期格式
     */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-M-d";
    /**
     * 默认日期类型格试.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            DEFAULT_DATE_FORMAT);
    /**
     * 缺省的日期时间格式
     */
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-M-d HH:mm:ss";
    /**
     * 时间格式
     */
    private static String DATETIME_FORMAT = DEFAULT_DATETIME_FORMAT;
    private static SimpleDateFormat datetimeFormat = new SimpleDateFormat(
            DATETIME_FORMAT);
    /**
     * 缺省的时间格式
     */
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    /**
     * 时间格式
     */
    private static String TIME_FORMAT = DEFAULT_TIME_FORMAT;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat(
            TIME_FORMAT);

    private static final String DEFAULT_UTC_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat(DEFAULT_UTC_DATETIME_FORMAT);

    private Dates() {
        // 私用构造主法.因为此类是工具类.
    }

    /**
     * 获取格式化实例.
     *
     * @param pattern 如果为空使用DEFAULT_DATE_FORMAT
     * @return
     */
    public static SimpleDateFormat getFormatInstance(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            pattern = DEFAULT_DATE_FORMAT;
        }
        return new SimpleDateFormat(pattern);
    }

    /**
     * 格式化Calendar
     *
     * @param calendar
     * @return
     */
    public static String formatCalendar(Calendar calendar) {
        if (calendar == null) {
            return "";
        }
        return dateFormat.format(calendar.getTime());
    }

    public static String formatDateTime(Date d) {
        if (d == null) {
            return "";
        }
        return datetimeFormat.format(d);
    }

    public static String formatDate(Date d) {
        if (d == null) {
            return "";
        }
        return dateFormat.format(d);
    }

    /**
     * 格式化时间
     *
     * @param d
     * @return
     */
    public static String formatTime(Date d) {
        if (d == null) {
            return "";
        }
        return timeFormat.format(d);
    }

    /**
     * 格式化整数型日期
     *
     * @param intDate
     * @return
     */
    public static String formatIntDate(Integer intDate) {
        if (intDate == null) {
            return "";
        }
        Calendar c = newCalendar(intDate);
        return formatCalendar(c);
    }

    /**
     * 根据指定格式化来格式日期.
     *
     * @param date    待格式化的日期.
     * @param pattern 格式化样式或分格,如yyMMddHHmmss
     * @return 字符串型日期.
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        if (Strings.isNullOrEmpty(pattern)) {
            return formatDate(date);
        }
        SimpleDateFormat simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            return formatDate(date);
        }
        return simpleDateFormat.format(date);
    }

    /**
     * 取得Integer型的当前日期
     *
     * @return
     */
    public static Integer getIntNow() {
        return getIntDate(getNow());
    }

    /**
     * 取得Integer型的当前日期
     *
     * @return
     */
    public static Integer getIntToday() {
        return getIntDate(getNow());
    }

    /**
     * 取得Integer型的当前年份
     *
     * @return
     */
    public static Integer getIntYearNow() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 取得Integer型的当前月份
     *
     * @return
     */
    public static Integer getIntMonthNow() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        return month;
    }

    public static String getStringToday() {
        return getIntDate(getNow()) + "";
    }

    /**
     * 根据年月日获取整型日期
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Integer getIntDate(int year, int month, int day) {
        return getIntDate(newCalendar(year, month, day));
    }

    /**
     * 某年月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Integer getFirstDayOfMonth(int year, int month) {
        return getIntDate(newCalendar(year, month, 1));
    }

    /**
     * 某年月的第一天
     *
     * @return
     */
    public static Integer getFirstDayOfThisMonth() {
        Integer year = Dates.getIntYearNow();
        Integer month = Dates.getIntMonthNow();
        return getIntDate(newCalendar(year, month, 1));
    }

    /**
     * 某年月的第一天
     *
     * @param date
     * @return
     */
    public static Integer getFistDayOfMonth(Date date) {
        Integer intDate = getIntDate(date);
        int year = intDate / 10000;
        int month = intDate % 10000 / 100;
        return getIntDate(newCalendar(year, month, 1));
    }

    /**
     * 某年月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Integer getLastDayOfMonth(int year, int month) {
        return intDateSub(getIntDate(newCalendar(year, month + 1, 1)), 1);
    }

    /**
     * 根据Calendar获取整型年份
     *
     * @param c
     * @return
     */
    public static Integer getIntYear(Calendar c) {
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 根据Calendar获取整型日期
     *
     * @param c
     * @return
     */
    public static Integer getIntDate(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return year * 10000 + month * 100 + day;
    }

    /**
     * 根据Date获取整型年份
     *
     * @param d
     * @return
     */
    public static Integer getIntYear(Date d) {
        if (d == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return getIntYear(c);
    }

    /**
     * 根据Date获取整型日期
     *
     * @param d
     * @return
     */
    public static Integer getIntDate(Date d) {
        if (d == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return getIntDate(c);
    }

    /**
     * 根据Integer获取Date日期
     *
     * @param n
     * @return
     */
    public static Date getDate(Integer n) {
        if (n == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.set(n / 10000, n / 100 % 100 - 1, n % 100);
        return c.getTime();
    }

    public static Date getDate(String date) throws ParseException {
        if (date == null || date.length() == 0) {
            return null;
        }

        try {
            if (date.contains("/")) {
                date = date.replaceAll("/", "-");
            }
            return getFormatInstance(DATE_FORMAT).parse(date);
        } catch (ParseException e) {
            LOGGER.error("解析[" + date + "]错误！", e);
            throw e;
        }
    }

    /**
     * 根据年份Integer获取Date日期
     *
     * @param year
     * @return
     */
    public static Date getFirstDayOfYear(Integer year) {
        if (year == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.set(year, 1, 1);
        return c.getTime();
    }

    /**
     * 根据年月日生成Calendar
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Calendar newCalendar(int year, int month, int day) {
        Calendar ret = Calendar.getInstance();
        if (year < 100) {
            year = 2000 + year;
        }
        ret.set(year, month - 1, day);
        return ret;
    }

    /**
     * 根据整型日期生成Calendar
     *
     * @param date
     * @return
     */
    public static Calendar newCalendar(int date) {
        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;

        Calendar ret = Calendar.getInstance();
        ret.set(year, month - 1, day);
        return ret;
    }

    /**
     * 取得Date型的当前日期
     *
     * @return
     */
    public static Date getNow() {
        return new Date();
    }

    /**
     * 取得Date型的当前日期
     *
     * @return
     */
    public static Date getToday() {
        return Dates.getDate(Dates.getIntToday());
    }

    /**
     * 整数型日期的加法
     *
     * @param date
     * @param days
     * @return
     */
    public static Integer intDateAdd(int date, int days) {
        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;

        day += days;

        return getIntDate(year, month, day);
    }

    /**
     * 整数型日期的减法
     *
     * @param date
     * @param days
     * @return
     */
    public static Integer intDateSub(int date, int days) {
        return intDateAdd(date, -days);
    }

    /**
     * 计算两个整型日期之间的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer daysBetweenDate(Integer startDate, Integer endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        Calendar c1 = newCalendar(startDate);
        Calendar c2 = newCalendar(endDate);

        Long lg = (c2.getTimeInMillis() - c1.getTimeInMillis()) / 1000 / 60
                / 60 / 24;
        return lg.intValue();
    }

    /**
     * 计算两个整型日期之间的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer daysBetweenDate(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        Long interval = endDate.getTime() - startDate.getTime();
        interval = interval / (24 * 60 * 60 * 1000);
        return interval.intValue();
    }

    /**
     * 取得当前日期.
     *
     * @return 当前日期, 字符串类型.
     */
    public static String getStringDate() {
        return getStringDate(Dates.getNow());
    }

    /**
     * 根据calendar产生字符串型日期
     *
     * @param d
     * @return eg:20080707
     */
    public static String getStringDate(Date d) {
        if (d == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d);
    }

    public static String getFormatStringDate(Date d) {
        if (d == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(d);
    }

    public static String strDateAdd(String dateStr, int days) throws ParseException {
        if (dateStr == null || "".equals(dateStr)) {
            return "";
        }

        Integer intDate = Dates.getIntDate(Dates.getDate(dateStr));
        Integer intDateAdd = Dates.intDateAdd(intDate, days);
        return Dates.getStringDate(Dates.getDate(intDateAdd));
    }

    public static int getSeconds(Date date) {
        if (date == null) {
            return 0;
        }
        DateTime dateTime = new DateTime(date);
        return (int) (dateTime.getMillis() / 1000);
    }

    public static int getCurrentSeconds() {
        return (int) (DateTime.now().getMillis() / 1000);
    }

    public static Date getLastDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }

    public static Date getSatDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 5);
        return c.getTime();
    }

    public static String formatUTCDateTime(Date date) {
        if (date == null) {
            return null;
        }

        utcDateTimeFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        return utcDateTimeFormat.format(date);
    }

    /**
     * 日期类型格式化，格式化为：yyyy-MM-dd 格式
     * i.e:@JsonSerialize(using=DateJsonSerializer.class)
     */
    public static class DateJsonSerializer extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException,
                JsonProcessingException {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            String formattedDate = formatter.format(value);
            jsonGenerator.writeString(formattedDate);
        }
    }

    public static class DateJsonDeserializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            try {
                return format.parse(jsonParser.getText());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        public static final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);


    }

    /**
     * 在需要序列化的字段的get方法中声明@JsonSerialize(using=TimestampJsonSerializer.class)
     * 其中seconds为秒，需要乘以1000换成毫秒，注意类型为Long，否则会少位数。
     */
    public static class TimestampJsonSerializer extends JsonSerializer<Integer> {

        @Override
        public void serialize(Integer seconds, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(seconds * 1000l);
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            String formattedDate = formatter.format(calendar.getTime());
            jsonGenerator.writeString(formattedDate);
        }
    }

    public static class TimestampJsonDeserializer extends JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            try {
                Date parse = format.parse(jsonParser.getText());
                return Dates.getSeconds(parse);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        public static final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);


    }

}
