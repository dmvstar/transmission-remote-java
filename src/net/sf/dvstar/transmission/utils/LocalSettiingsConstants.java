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

/**
 *
 * @author dstarzhynskyi
 */
public interface LocalSettiingsConstants {

    public static String CONF_CLI_HOST = "conn-host";
    public static String CONF_CLI_PORT = "conn-port";
    public static String CONF_CLI_USESSL = "conn-usessl";
    public static String CONF_CLI_AUTOCONNECT = "conn-auto";
    public static String CONF_CLI_USER = "auth-login";
    public static String CONF_CLI_LOCALE = "default-locale";
    public static String CONF_CLI_PASS = "auth-passwd";
    public static String CONF_CLI_AUTHENABLED = "auth-enabled";
    public static String CONF_CLI_PROXYENABLED = "proxy-enabled";
    public static String CONF_CLI_PROXYHOST = "proxy-host";
    public static String CONF_CLI_PROXYPORT = "proxy-port";
    public static String CONF_CLI_PROXYUSER = "proxy-login";
    public static String CONF_CLI_PROXYPASS = "proxy-passwd";
    public static String CONF_CLI_PROXYAUTH = "proxy-auth";
    public static String CONF_CLI_STARTPAUSED = "start-paused";
    public static String CONF_CLI_RETRYLIMIT = "conn-try-count";
    public static String CONF_CLI_MINTOTRAY = "min-to-tray";
    public static String CONF_CLI_REFRESHRATE = "conn-refresh";
    public static String CONF_CLI_CURRENTPROFILE = "current-profile";
    public static String CONF_CLI_PROFILES = "client-profiles";
    public static String CONF_CLI_CUSTOMPATH = "custom-path";

    public static String CONF_CLI_USED_DIRS = "used-dir-list";
    public static String CONF_CLI_LAST_USED_DIR = "last-used-dir";

    public static String CONF_CLI_COL_STATUS_CHECK_WAIT = "status-color-cwait";//,  Waiting in queue to check files
    public static String CONF_CLI_COL_STATUS_CHECK = "status-color-wait";//',  Checking files
    public static String CONF_CLI_COL_STATUS_DOWNLOAD = "status-color-down";//,  Downloading
    public static String CONF_CLI_COL_STATUS_SEED = "status-color-seed";//,  Seeding
    public static String CONF_CLI_COL_STATUS_STOPPED = "status-color-stop";//   Torrent is stopped

    public static String CONF_CLI_COL_PB_025_B = "pb-color-25b";
    public static String CONF_CLI_COL_PB_050_B = "pb-color-50b";
    public static String CONF_CLI_COL_PB_075_B = "pb-color-75b";
    public static String CONF_CLI_COL_PB_100_B = "pb-color-100b";
    public static String CONF_CLI_COL_PB_100_A = "pb-color-100a";



}
