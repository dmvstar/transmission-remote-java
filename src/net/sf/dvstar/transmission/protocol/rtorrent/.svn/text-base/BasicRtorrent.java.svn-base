
package net.sf.dvstar.transmission.protocol.rtorrent;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcCallback;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

/**
 * java api for xmlrpc api of rtorrent 0.78
 */
public class BasicRtorrent {

    private  XmlRpcClient client;

        /**
     * Constructor
     *
     * @param boxConfig
     *            rtorrent config
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    public BasicRtorrent() throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String url = "scgi://";
        client = new XmlRpcClient(url, true);
    }
    
    public BasicRtorrent(String url) throws MalformedURLException {
        client = new XmlRpcClient(url, true);
    }




    private XmlRpcCallback emptyCallBack = new XmlRpcCallback() {

        @Override
        public void onResult(Object arg0) {
            //System.out.println(arg0);
        }

        @Override
        public void onFault(int arg0, String arg1) {
            //System.out.println(arg1);
        }

        @Override
        public void onException(XmlRpcException arg0) {
        }
    };

    public static Long getLong(Object obj) {
        return Long.parseLong(obj.toString());
    }

    private static String getTorrentState(int state, int opened, double donePercent, String error) {
        String sState = (error == null || error.length() == 0) ? "ok" : "error";
        switch (state) {
            case 1: //started
                if (opened == 0) {
                    sState += "_stop";
                } else {
                    if (donePercent >= 1) {
                        sState += "_seed";
                    } else {
                        sState += "_leech";
                    }
                }
                break;
            case 0: //stopped
                sState += "_stop";
                break;
            default:
                sState += "_unknown_+" + state;
                break;
        }
        return sState;
    }


    public String getVersion() throws XmlRpcFault {
        return (String) client.invoke("system.client_version", new Object[]{});
    }

    @SuppressWarnings("unchecked")
    public String[] getSupportedCommands() throws XmlRpcException, XmlRpcFault {
        XmlRpcArray res = (XmlRpcArray) client.invoke("system.listMethods", new Object[]{});
        return (String[]) res.toArray(new String[res.size()]);
    }

    public void getTorrentsList(Vector<Torrent> torrents) throws XmlRpcFault {
        Multicall.TorrentCall dcall = Multicall.getDCall(getClient(), "default");
        dcall.addCommand("d.get_hash");
        dcall.addCommand("d.get_name");
        dcall.addCommand("d.get_size_chunks");
        dcall.addCommand("d.get_completed_chunks");
        dcall.addCommand("d.get_chunk_size");
        dcall.addCommand("d.get_message");
        dcall.addCommand("d.get_peers_connected");
        dcall.addCommand("d.get_peers_not_connected");
        dcall.addCommand("d.get_peers_complete");
        dcall.addCommand("d.get_state");
        dcall.addCommand("d.get_down_rate");
        dcall.addCommand("d.get_up_rate");
        dcall.addCommand("d.is_open");
        Object[] res = dcall.execute();


        for (int i = 0; i < res.length; i++) {
            Object[] info = ((XmlRpcArray) res[i]).toArray();
            long chunkSize = getLong(info[4]);
            Torrent torrent = new Torrent();
            torrent.setHash(((String) info[0]));
            torrent.setName((String) info[1]);
            torrent.setSize(getLong(info[2]) * chunkSize);
            torrent.setDownPercent((getLong(info[3]) * chunkSize) / torrent.getSize().doubleValue());
            torrent.setError((String) info[5]);
            long conn = getLong(info[6]);
            long notConn = getLong(info[7]);
            long compl = getLong(info[8]);
            torrent.setLeechers(conn - compl);
            torrent.setSeeders(compl);
            torrent.setPeers(conn + notConn);
            int state = (getLong(info[9])).intValue();
            int opened = (getLong(info[12])).intValue();
            torrent.setState(getTorrentState(state, opened, torrent.getDoneProgress(), torrent.getErrorString()));
            torrent.setDownRate(getLong(info[10]));
            torrent.setUpRate(getLong(info[11]));
            torrents.add(torrent);
        }


    }

    public Vector<TorrentFileDesc> getFileList(Torrent t) throws XmlRpcFault {
        Multicall.FileCall dcall = Multicall.getFCall(this.getClient(), t.getHashString());
        dcall.addCommand("f.get_path");
        dcall.addCommand("f.get_size_bytes");
        dcall.addCommand("f.get_size_chunks");
        dcall.addCommand("f.get_completed_chunks");
        dcall.addCommand("f.get_priority");
        HashMap[] res = dcall.execute();

        Vector<TorrentFileDesc> files = new Vector<TorrentFileDesc>();
        for (HashMap map : res) {
            long chunkSize = getLong(map.get("f.get_size_chunks"));
            TorrentFileDesc file = new TorrentFileDesc();
            file.setPath((String) map.get("f.get_path"));
            file.setSize(getLong(map.get("f.get_size_bytes")));
            file.setDownPercent((double) getLong(map.get("f.get_completed_chunks")) / chunkSize);
            files.add(file);
        }

        return files;
    }

    public Vector<TorrentFileDesc> getPeerList(String id) throws XmlRpcFault {
        Multicall.PeerCall dcall = Multicall.getPCall(this.getClient(), id);
        dcall.addCommand("p.get_address");
        dcall.addCommand("p.get_client_version");
        dcall.addCommand("f.get_completed_percent");
        dcall.addCommand("f.get_down_rate");
        dcall.addCommand("f.get_down_total");
        dcall.addCommand("f.get_up_rate");
        dcall.addCommand("f.get_up_total");
        HashMap[] res = dcall.execute();

        java.lang.System.out.println(res);

        Vector<TorrentFileDesc> files = new Vector<TorrentFileDesc>();
        /*        for (HashMap map : res) {
        long chunkSize = getLong(map.get("f.get_size_chunks"));
        File file = new File();
        file.setPath((String) map.get("f.get_path"));
        file.setSize(getLong(map.get("f.get_size_bytes")));
        file.setDownPercent((double) getLong(map.get("f.get_completed_chunks")) / chunkSize);
        files.add(file);
        }
         */
        return files;
    }

    public long getGlobalUpLimit() throws XmlRpcFault {
        return Long.parseLong(client.invoke("get_up_rate", new Object[]{}).toString());
    }

    public long getGlobalDownLimit() throws XmlRpcFault {
        return Long.parseLong(client.invoke("get_down_rate", new Object[]{}).toString());
    }

    public void setGlobalDownLimit(String speed) {
        client.invokeAsynchronously("set_download_rate", new Object[]{speed + "k"}, this.emptyCallBack);
    }

    public void setGlobalUpLimit(String speed) {
        client.invokeAsynchronously("set_upload_rate", new Object[]{speed + "k"}, this.emptyCallBack);
    }

    public boolean isProcessStarted() {
        try {
            getVersion();
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    public XmlRpcClient getClient() {
        return client;
    }

    public void loadTorrent(java.io.File torrent) {
        //TODO: do async
        FileInputStream is = null;
        try {
            int length = (int) torrent.length();
            byte[] tmp = new byte[length];
            is = new FileInputStream(torrent);
            is.read(tmp);
            client.invokeAsynchronously("load_raw_start", new Object[]{tmp},this.emptyCallBack);
        }catch (IOException ex) {
            Logger.getLogger(BasicRtorrent.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(BasicRtorrent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void startTorrent(Torrent t) {
        this.client.invokeAsynchronously("d.start", new Object[]{t.getHashString()}, this.emptyCallBack);

    }

    public void stopTorrent(Torrent t) {
        this.client.invokeAsynchronously("d.stop", new Object[]{t.getHashString()}, this.emptyCallBack);
    }

    public void removeTorrent(Torrent t) {
        this.client.invokeAsynchronously("d.erase", new Object[]{t.getHashString()}, this.emptyCallBack);

    }
}
