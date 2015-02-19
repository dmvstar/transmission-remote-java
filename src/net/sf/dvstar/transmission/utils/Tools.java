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

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.sf.dvstar.transmission.TransmissionView;
import net.sf.dvstar.transmission.protocol.transmission.ProtocolConstants;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.klomp.snark.MetaInfo;

/**
 *
 * @author dstarzhynskyi
 */
public class Tools {

    public static long toLong(String s) {
        long ret = 0;
        try {
            ret = Long.parseLong(s);
        } catch (NumberFormatException ex) {
        }
        return ret;
    }

    public static double toDouble(String s) {
        double ret = 0;
        try {
            ret = Double.parseDouble(s);
        } catch (NumberFormatException ex) {
        }
        return ret;
    }

    public static String getPercentage(double percent) {
        return percent + " %";
    }

    public static double calcPercentage(double x, double total) {
        double ret = 0.00;
        if (total > 0.00) {
            ret = formatDouble(((double) x / (double) total) * 100, 2);
        } else {
            ret = formatDouble(((double) x) * 100, 2);
        }

        return ret;
        /*
        if (total > 0) {
        return ret;
        } else {
        return 100;
        }
         */
    }

    public static double calcRatio(long upload_total, long download_total) {
        if (download_total <= 0 || upload_total <= 0) {
            return -1;
        } else {
//            return Math.round( upload_total / download_total);
            return (upload_total / download_total);
        }
    }

    /**
     * Format scale for double
     * @param d value
     * @param scale scale
     * @return double with precision
     */
    public static double formatDouble(double d, int scale) {
        double dd = Math.pow(10, scale);
        return Math.round(d * dd) / dd;
    }

    /**
     * Print array as String
     * @param srows
     * @return
     */
    public static String printArray(int[] srows) {
        String ret = "";
        if (srows != null) {
            for (int i = 0; i < srows.length; i++) {
                ret += "[" + srows[i] + "]";
            }
        }
        return ret;
    }

    public static String printArray(byte[] fileStatsArray) {
        String ret = "";
        if (fileStatsArray != null) {
            for (int i = 0; i < fileStatsArray.length; i++) {
                ret += "[" + fileStatsArray[i] + "]";
            }
        }
        return ret;
    }

    /**
     *
     * @param bytes
     * @return
     * @tode localise prefix !!
     */
    public static String getFileSize(float bytes) {
        if (bytes >= (1099511627776L)) {
            float size = (bytes / (float) (1099511627776L));
            return String.format("%.2f %s", size, "TB");
        } else if (bytes >= (1024 * 1024 * 1024)) {
            float size = (bytes / (float) (1024 * 1024 * 1024));
            return String.format("%.2f %s", size, "GB");
        } else if (bytes >= (1024 * 1024)) {
            float size = (bytes / (float) (1024 * 1024));
            return String.format("%.2f %s", size, "Mb");
        } else if (bytes >= 1024) {
            float size = (bytes / (float) 1024);
            return String.format("%.2f %s", size, "kb");
        } else if (bytes > 0 & bytes < 1024) {
            float size = bytes;
            return String.format("%.2f %s", size, "b");
        } else {
            return "0 " + " b";
        }
    }

    public static String getVisibleSpeed(float speed) {
        String ret = "0 B/s";
        float ospeed = 0;

        if (speed >= (1024 * 1024)) {
            ospeed = (speed / (float) (1024 * 1024));
            ret = String.format("%.2f %s", ospeed, "MB/s");
        } else if (speed >= (1024)) {
            ospeed = (speed / (float) (1024));
            ret = String.format("%.2f %s", ospeed, "KB/s");
        } else {
            ret = String.format("%.2f %s", speed, "B/s");
        }

        return ret;
    }

    public static String delBraces(String input, char[] brace) {
        String ret = input;

        if (brace != null) {
            for (int i = 0; i < brace.length; i++) {
                if (ret.charAt(0) == brace[i]) {
                    ret = ret.substring(1);
                }
                if (ret.charAt(ret.length() - 1) == brace[i]) {
                    ret = ret.substring(0, ret.length() - 1);
                }
            }
        }

        return ret;
    }

    /**
     * Dump sysinfo print
     */
    public static void printSysInfo() {

        System.out.println("STATUS_CHECK=" + ProtocolConstants.STATUS_CHECK);
        System.out.println("STATUS_DOWNLOAD=" + ProtocolConstants.STATUS_DOWNLOAD);
        System.out.println("STATUS_SEED=" + ProtocolConstants.STATUS_SEED);
        System.out.println("STATUS_CHECK_WAIT=" + ProtocolConstants.STATUS_CHECK_WAIT);
        System.out.println("STATUS_PAUSED=" + ProtocolConstants.STATUS_PAUSED);

        System.out.printf("proxySet=%s proxyHost=%s proxyPort=%s system=%s",
                System.getProperty("proxySet"),
                System.getProperty("proxyHost"),
                System.getProperty("proxyPort"),
                System.getProperty("java.net.useSystemProxies"));

    }

    /**
     * Parse string to array by delimiter
     * @param namesList String with list
     * @param sep Separator
     * @return list of items
     */
    public static String[] getStringArray(String namesList, String sep) {
        String ret[] = null;

        if (namesList != null) {
            StringTokenizer tok = new StringTokenizer(namesList, sep);
            if (tok.countTokens() > 0) {
                ret = new String[tok.countTokens()];
                int i = 0;
                while (tok.hasMoreTokens()) {
                    ret[i] = tok.nextToken();
                    i++;
                }
            }
        }

        return ret;
    }
    public static final String DEFAULT_LOCALE_PATH = "net/sf/dvstar/transmission/resources/";
    public static final String DEFAULT_LOCALE_CLAZ = "TransmissionView";

    /**
     * Deternmine count of availables locale by main frame TransmissionView resource
     * @return list with Locales
     */
    public static java.util.List<Locale> getListOfAvailLanguages() {
        java.util.List supportedLocales = new java.util.ArrayList();

        try {
            Set names = getResoucesInPackage(DEFAULT_LOCALE_PATH);
            Iterator it = names.iterator();
            while (it.hasNext()) {
                String n = (String) it.next();

                String lang = n.substring(n.lastIndexOf('/') + 1);

                // only except resources with extension '.properties'
                if (lang.indexOf(".properties") < 0) {
                    continue; // not very nice but efficient
                }

                if (lang.indexOf(DEFAULT_LOCALE_CLAZ) < 0) {
                    continue;
                }

                lang = lang.substring(0, lang.indexOf(".properties"));

                StringTokenizer tokenizer = new StringTokenizer(lang, "_");
                if (tokenizer.countTokens() <= 1) {
                    Locale model = Locale.ENGLISH;// getDefault(); //new Locale("en", "US");
                    supportedLocales.add(model);
                    continue;
                }

                String language = "";
                String country = "";
                String variant = "";

                String[] parts = new String[tokenizer.countTokens()];

                int i = 0;
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();

                    switch (i) {
                        case 0:
                            //the word locale
                            break;
                        case 1:
                            language = token;
                            break;
                        case 2:
                            country = token;
                            break;
                        case 3:
                            variant = token;
                            break;
                        default:
                        //
                    }
                    i++;

                }

                Locale model = new Locale(language, country, variant);
                supportedLocales.add(model);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sort the list. Probably should use the current locale when getting the
        // DisplayLanguage so the sort order is correct for the user.
        Collections.sort(supportedLocales, new Comparator() {

            public int compare(Object lhs, Object rhs) {
                //Locale ll = (Locale)lhs;
                //Locale rl = (Locale)rhs;
                String ls = ((Locale) lhs).getDisplayLanguage();
                String rs = ((Locale) rhs).getDisplayLanguage();

                // this is not very nice
                // We should introduce a MyLocale
                if (ls.equals("pap")) {
                    ls = "Papiamentu";
                }
                if (rs.equals("pap")) {
                    rs = "Papiamentu";
                }


                //return ll.getDisplayLanguage().compareTo( rl.getDisplayLanguage() );
                return ls.compareTo(rs);
            }
        });

        return supportedLocales;
    }

    public static Set getResoucesInPackage(String packageName) throws IOException {
        String localPackageName;

        if (packageName.endsWith("/")) {
            localPackageName = packageName;
        } else {
            localPackageName = packageName + '/';
        }

        ClassLoader cl = Tools.class.getClassLoader();
        Enumeration dirEnum = cl.getResources(localPackageName);

        Set names = new HashSet();

        // Loop CLASSPATH directories
        while (dirEnum.hasMoreElements()) {
            URL resUrl = (URL) dirEnum.nextElement();

            // Pointing to filesystem directory
            if (resUrl.getProtocol().equals("file")) {
                try {
                    File dir = new File(resUrl.getFile());
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            File file = files[i];
                            if (file.isDirectory()) {
                                continue;
                            }
                            names.add(localPackageName + file.getName());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Pointing to Jar file
            } else if (resUrl.getProtocol().equals("jar")) {
                JarURLConnection jconn = (JarURLConnection) resUrl.openConnection();
                JarFile jfile = jconn.getJarFile();
                Enumeration entryEnum = jfile.entries();
                while (entryEnum.hasMoreElements()) {
                    JarEntry entry = (JarEntry) entryEnum.nextElement();
                    String entryName = entry.getName();
                    // Exclude our own directory
                    if (entryName.equals(localPackageName)) {
                        continue;
                    }
                    String parentDirName = entryName.substring(0, entryName.lastIndexOf('/') + 1);
                    if (!parentDirName.equals(localPackageName)) {
                        continue;
                    }
                    names.add(entryName);
                }
            } else {
                // Invalid classpath entry
            }
        }

        return names;
    }

    public static String printDateStamp() {
        Date curr = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy hh:mm:ss", Locale.ENGLISH);
        return sdf.format(curr);
    }

    /**
     * Determine who call this method
     * @return String with
     */
    public static String whoCalledMe() {
        StackTraceElement[] stackTraceElements =
                Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[4];
        String classname = caller.getClassName();
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        return "[Called by] " + classname + "." + methodName + ":" + lineNumber;
    }

    /**
     * Determine method who call this method
     * @return String with
     */
    public static String whoCalledMethod() {
        StackTraceElement[] stackTraceElements =
                Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[4];
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        return "[Called by] " + methodName + ":" + lineNumber;
    }

    /**
     * Determine method who call this method
     * @return String with
     */
    public static String getCalledMethod() {
        StackTraceElement[] stackTraceElements =
                Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[2];
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        return "[Called by] " + methodName + ":" + lineNumber;
    }


    /**
     * Show call stack for
     * @return String with call stack
     */
    public static String showCallStack() {
        String ret = "\n ";
        StackTraceElement[] stackTraceElements =
                Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTraceElements.length; i++) {
            StackTraceElement ste = stackTraceElements[i];
            String classname = ste.getClassName();
            String methodName = ste.getMethodName();
            int lineNumber = ste.getLineNumber();
            ret += (" [" + lineNumber + "]:" + classname + ".(" + methodName + ")\n");
        }

        return ret;
    }

    /**
     * Make Locale from string
     * @param property
     * @return
     */
    public static Locale parseConfigLocale(String property) {
        Locale ret = new Locale(property);
        int index = 0;
        if ((index = property.indexOf("_")) > 0) {
            ret = new Locale(property.substring(0, index), property.substring(index + 1));
        }
        return ret;
    }

    /**
     * Get country bi IP Address
     * @param address IP
     * @return country name or IP
     */
    public static String getCountryByAddress(String address) {
        return address;
    }

    public static List makeMetaInfoFiles(MetaInfo metaInfo) {
        List ret = new ArrayList();
        if (metaInfo != null) {

            if (metaInfo.getFiles() != null) {
                ret.add(metaInfo.getName());
                Iterator filesIterator = metaInfo.getFiles().iterator();
                while (filesIterator.hasNext()) {
                    ret.add(Tools.delBraces(filesIterator.next().toString(), new char[]{'[', ']'}));
                }
            } else {
                ret.add(".");
                ret.add(metaInfo.getName());
            }

        }
        return ret;
    }

    /**
     * Set toggle Detail in JFileChooserDetails
     * @param comp
     */
    public static void switchJFileChooserDetails(final Component[] comp) {
        for (int x = 0; x < comp.length; x++) {
            if (comp[x] instanceof javax.swing.JPanel) {
                switchJFileChooserDetails(((javax.swing.JPanel) comp[x]).getComponents());
                Component[] c = ((javax.swing.JPanel) comp[x]).getComponents();
                for (int i = 0; i < c.length; i++) {

                    if (c[i] instanceof javax.swing.JToggleButton) {
                        ((javax.swing.JToggleButton) c[i]).doClick();
                    }
                }
            }
        }
    }
    /*
    public static int BANDWIDTH_LOW = -1;
    public static int BANDWIDTH_NORMAL = 0;
    public static int BANDWIDTH_HIGH = 1;

    public static String BANDWIDTH_LOW_DESC = "torrent.file.band.low";
    public static String BANDWIDTH_NORMAL_DESC = "torrent.file.band.normal";
    public static String BANDWIDTH_HIGH_DESC = "torrent.file.band.high";
     */

    public static String formatPriority(int n) {
        String ret = "";
        ResourceMap globalResourceMap = Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getResourceMap(TransmissionView.class);
        if (n < 0) {
            ret = globalResourceMap.getString(ProtocolConstants.BANDWIDTH_LOW_DESC);
        } else if (n > 0) {
            ret = globalResourceMap.getString(ProtocolConstants.BANDWIDTH_HIGH_DESC);
        } else {
            ret = globalResourceMap.getString(ProtocolConstants.BANDWIDTH_NORMAL_DESC);
        }
        return ret;
    }

    public static boolean checkText(String text) {
        boolean ret = false;
        if(text != null && text.length()>0) ret = true;
        return ret;
    }

    public static boolean checkNumber(String text) {
        boolean ret = false;
        if(text != null && text.length()>0) {
            try {
                if(Integer.parseInt(text)>=0) ret = true;
            } catch(Exception e) {}
        }
        return ret;
    }
}
