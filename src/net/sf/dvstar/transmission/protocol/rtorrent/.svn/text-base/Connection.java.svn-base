package net.sf.dvstar.transmission.protocol.rtorrent;

import redstone.xmlrpc.XmlRpcClient;

/**
 *
 * @author sdv
 */
public class Connection {

    public static void main(String[] args) throws Exception {
        XmlRpcClient client = new XmlRpcClient("http://localhost/RPC2", false);
        String out;

        BasicRtorrent rtor = new BasicRtorrent("http://localhost/RPC2");
        out = "getVersion()="+rtor.getVersion();
        System.out.println( out );
        out = "getSupportedCommands()="+rtor.getSupportedCommands();
        System.out.println( out );

        //Object token = client.invoke("system.listMethods", new Object[]{});
        //java.lang.System.out.println( token );
    }
}
