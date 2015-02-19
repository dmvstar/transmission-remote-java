/*
 * transmission-remote-java remote control for transmission daemon
 *
 * Copyright (C) 2009-2011 Dmytro Starzhynskyi (dvstar)
 * http://transmission-rj.sourceforge.net/
 * http://code.google.com/p/transmission-remote-java/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.dvstar.transmission.utils;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import net.sf.dvstar.transmission.utils.TimeSpanConstant.TorrentDatePrintFormat;

/**
 *
 * TimeSpan converting second time period to class
 *
 * @author dstarzhynskyi
 */
public class TimeSpan implements Comparable<TimeSpan>, Comparator<TimeSpan> {

    private long year = 0;
    private long month = 0;
    private long day = 0;
    private long hour = 0;
    private long minute = 0;
    private long sec = 0;
    private Long second = new Long(0);
    private Integer[] div = {60, 60, 60, 24, 7, 4, 12};
    private String[] unit = {"s", "m", "h", "D", "W", "M", "Y"};
    private int l = 0;

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getMinute() {
        return minute;
    }

    public void setMinute(long minute) {
        this.minute = minute;
    }

    public long getMonth() {
        return month;
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public long getSecond() {
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public TimeSpan() {
    }

    public TimeSpan(Long sec) {
        this.second = sec;
    }

    @Override
    public int compareTo(TimeSpan o) {
        return second.compareTo(o.second);
    }

    @Override
    public int compare(TimeSpan o1, TimeSpan o2) {
        return o1.compareTo(o2);
    }

    @Override
    public String toString() {
        int x = second.intValue();
        int rest = 0;
        while (!(x < div[l])) {
            rest = x % div[l];
            x /= div[l++];
        }

        if (x == 0) {
            return "-";
        } else {
            String out = x + unit[l];
            if (l > 0) {
                out += " " + rest + unit[l - 1];
            }
            return out;
        }
    }

    public static TimeSpan fromSeconds(String eta) {
        return fromSeconds( Long.parseLong(eta) );
    }
    
    public static TimeSpan fromSeconds(double eta) {
        return fromSeconds((long)eta);
    }
    /**
     * Create instance from second
     * @param eta
     * @return
     */
    public static TimeSpan fromSeconds(long eta) {
        TimeSpan span = new TimeSpan(eta);
        double buf = eta, res = 0.0, tmp = 0.0;
        // day
        if (buf > 60.0 * 60.0 * 24) {
            res = buf % (60.0 * 60.0 * 24);
            tmp = buf / (60.0 * 60.0 * 24);
            span.day = (long) tmp;
            buf = buf - (60.0 * 60.0 * 24) * span.day;
        }
        // hour
        if (buf > 60.0 * 60.0) {
            res = buf % (60.0 * 60.0);
            tmp = buf / (60.0 * 60.0);
            span.hour = (long) tmp;
            buf = buf - 60.0 * 60.0 * span.hour;
        }
        // minuite
        if (buf >= 60.0) {
            res = buf % 60.0; // sec
            buf = buf / 60.0; //min
            span.minute = (long) buf;
            span.sec = (long) res;
        } else {
            //buf = buf / 60.0; //min
            // second
            if (buf < 60.0) {
                span.sec = (long) buf;
            }
        }
        return span;
    }

    /**
     * Return date for full format
     * @param osec time
     * @return string with date
     */
    public static String getDateFromEpochStr(Long osec) {
        String ret = "";

        ret = getDateFromEpochStr(osec, TorrentDatePrintFormat.TIME_MODE_DATE_MMSS);

        return ret;
    }

    public static String getDateFromEpochStr(Long osec, TorrentDatePrintFormat mode) {
        String ret = "";
        String dateFormat = TimeSpanConstant.FMT_TIME_MODE_DATE_MMSS;

        switch(mode) {
            case TIME_MODE_DATE_MMSS:
                dateFormat = TimeSpanConstant.FMT_TIME_MODE_DATE_MMSS;
                break;
            case TIME_MODE_DATE_ONLY:
                dateFormat = TimeSpanConstant.FMT_TIME_MODE_DATE_ONLY;
                break;

        }

        long sec = osec.longValue();
        Date dt = new Date(sec * 1000);

        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        ret = fmt.format(dt);

        return ret;
    }

    public static String getDateFromEpochStr(Object osec) {
        String ret = "";

        long sec = Long.parseLong(osec.toString());

        ret = getDateFromEpochStr(sec);

        return ret;
    }

    /**
     * Make simple printable output
     * @param ts input
     * @return string for use
     */
    public static String formatTimespanLong(TimeSpan ts) {
        String ret = "";
        String day = ts.day > 0 ? ts.day + "d " : "";
        String hou = ts.hour > 0 ? ts.hour + "h " : "";
        String min = ts.minute > 0 ? ts.minute + "m " : "";
        String sec = ts.sec > 0 ? ts.sec + "s" : "";
        ret = day + hou + min + sec;
        return ret;
    }

    public static void main(String args[]) {
        String val = "270";
        if (args.length > 0) {
            val = args[0];
        }
        TimeSpan span = TimeSpan.fromSeconds(Double.parseDouble(val));
        System.out.println(val + "=[" + TimeSpan.formatTimespanLong(span) + "]");
        val = "3600";
        span = TimeSpan.fromSeconds(Double.parseDouble(val));
        System.out.println(val + "=[" + TimeSpan.formatTimespanLong(span) + "]");
        val = "3665";
        span = TimeSpan.fromSeconds(Double.parseDouble(val));
        System.out.println(val + "=[" + TimeSpan.formatTimespanLong(span) + "]");
        val = "3735";
        span = TimeSpan.fromSeconds(Double.parseDouble(val));
        System.out.println(val + "=[" + TimeSpan.formatTimespanLong(span) + "]");
        val = "10800";
        span = TimeSpan.fromSeconds(Double.parseDouble(val));
        System.out.println(val + "=[" + TimeSpan.formatTimespanLong(span) + "]");
        val = "86468";
        span = TimeSpan.fromSeconds(Double.parseDouble(val));
        System.out.println(val + "=[" + TimeSpan.formatTimespanLong(span) + "]");
        System.out.println(val + "=[" + span + "]");
    }
}
