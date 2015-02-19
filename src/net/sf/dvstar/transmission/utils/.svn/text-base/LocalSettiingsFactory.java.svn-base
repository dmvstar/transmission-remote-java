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

import java.util.Properties;

/**
 *
 * @author dstarzhynskyi
 */
public class LocalSettiingsFactory implements LocalSettiingsConstants{
    private boolean UseSSL = false;
    private boolean UseAuth = false;
    private boolean UseProxy = false;
    private String Host;
    private String Port;
    private int IPort;
    private int IProxyPort;
    String CustomPath;
    private String ProxyHost;
    private String ProxyPort;

    private String User;
    private String Pass;


    String urlCache;
    private ConfigStorage prop;

    public LocalSettiingsFactory(){

    }

    public String refreshUrlCache()
    {
            // http://195.74.67.237:9091/rpc/transmission/
            try {
                //System.out.println();
                this.urlCache = String.format(
                        "%s://%s:%d%srpc",
                        new Object[] { isUseSSL() ? "https" : "http", getHost(), getIPort(), CustomPath == null ? "/transmission/" : CustomPath });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.urlCache;
    }

    public void setConfigProperties(ConfigStorage prop) {
        this.prop = prop;

        Host = prop.getProperty(CONF_CLI_HOST,"localhost");
        Port = prop.getProperty(CONF_CLI_PORT,"9091");
        IPort = Integer.parseInt(Port);
        UseAuth = Boolean.parseBoolean( prop.getProperty(CONF_CLI_AUTHENABLED,"false") );
        setProxyHost(prop.getProperty(CONF_CLI_PROXYHOST, "localhost"));
        ProxyPort = prop.getProperty(CONF_CLI_PROXYPORT,"localhost");
        setIProxyPort(Integer.parseInt(ProxyPort));
        UseProxy = Boolean.parseBoolean( prop.getProperty(CONF_CLI_PROXYENABLED,"false") );
        User = prop.getProperty(CONF_CLI_USER,"");
        Pass = prop.getProperty(CONF_CLI_PASS,"");
        
    }

    /**
     * @return the Host
     */
    public String getHost() {
        return Host;
    }

    /**
     * @return the Port
     */
    public String getPort() {
        return Port;
    }

    /**
     * @return the UseSSL
     */
    public boolean isUseSSL() {
        return UseSSL;
    }

    /**
     * @return the UseAuth
     */
    public boolean isUseAuth() {
        return UseAuth;
    }

    /**
     * @return the UseProxy
     */
    public boolean isUseProxy() {
        return UseProxy;
    }

    /**
     * @return the ProxyHost
     */
    public String getProxyHost() {
        return ProxyHost;
    }

    /**
     * @param ProxyHost the ProxyHost to set
     */
    public void setProxyHost(String ProxyHost) {
        this.ProxyHost = ProxyHost;
    }

    /**
     * @return the ProxyPort
     */
    public String getProxyPort() {
        return ProxyPort;
    }

    /**
     * @return the User
     */
    public String getUser() {
        return User;
    }

    /**
     * @return the Pass
     */
    public String getPass() {
        return Pass;
    }

    /**
     * @return the IPort
     */
    public int getIPort() {
        return IPort;
    }

    /**
     * @return the IProxyPort
     */
    public int getIProxyPort() {
        return IProxyPort;
    }

    /**
     * @param IProxyPort the IProxyPort to set
     */
    public void setIProxyPort(int IProxyPort) {
        this.IProxyPort = IProxyPort;
    }


}
