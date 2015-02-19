package net.sf.dvstar.transmission.protocol.rtorrent;

import java.util.HashMap;
import java.util.Vector;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

public class Multicall {

	public static Syscall getSyscall(XmlRpcClient client){
		return (new Multicall()).new Syscall(client);
	}
	
	public static FileCall getFCall(XmlRpcClient client, String fileId){
		return (new Multicall()).new FileCall(client, fileId);
	}

    public static PeerCall getPCall(XmlRpcClient client, String fileId){
		return (new Multicall()).new PeerCall(client, fileId);
	}


	public static TorrentCall getDCall(XmlRpcClient client, String viewId){
		return (new Multicall()).new TorrentCall(client, viewId);
	}

	
	public class Syscall {
		private final XmlRpcClient client;
		private Vector<HashMap<String, Object>> sysCall = new Vector<HashMap<String, Object>>();

		public Syscall(XmlRpcClient client)
		{
			this.client = client;
		}
		
		public void addCommand(String command, Object... params)
		{
			HashMap<String, Object> tParam = new HashMap<String, Object>();
			tParam.put("methodName", command);
			tParam.put("params", params);
			sysCall.add(tParam);
		}
		
		public HashMap<String,Object> execute() throws XmlRpcFault
		{
			XmlRpcArray arr = (XmlRpcArray)client.invoke("system.multicall",new Object[] { sysCall.toArray() });
			HashMap<String,Object> result=new HashMap<String,Object>();
			for (int i = 0; i < arr.size(); i++) {
				Object value = arr.get(i);
				if (value instanceof redstone.xmlrpc.XmlRpcStruct)
					throw new XmlRpcException(((redstone.xmlrpc.XmlRpcStruct)value).getString("faultString"));
				value = ((XmlRpcArray)value).get(0);
				String name=(String) sysCall.get(i).get("methodName");
				result.put(name, value);
			}
			return result;
		}
	}
	
	public class FileCall {
		private final XmlRpcClient client;
		private Vector<Object> call = new Vector<Object>();

		public FileCall(XmlRpcClient client, String fileId)
		{
			this.client = client;
			call.add(fileId);
			call.add(0);
		}
		
		public void addCommand(String command)
		{
			call.add(command+"=");
		}
		
		@SuppressWarnings("unchecked")
		public HashMap[] execute() throws XmlRpcException, XmlRpcFault
		{
			XmlRpcArray files = (XmlRpcArray)client.invoke("f.multicall", call.toArray());
			HashMap[] result=new HashMap[files.size()];
			//create list of commands. First two parameters are system reserved
			String[] commands=new String[call.size()-2];
			for (int i = 2; i < call.size(); i++) {
				String name=(String) call.get(i);
				//truncate '=' at end
				name=name.substring(0,name.length()-1);
				commands[i-2]=name;
			}
			for (int i = 0; i < files.size(); i++) {
				XmlRpcArray file = (XmlRpcArray) files.get(i);
				HashMap map=new HashMap();
				for (int ii = 0; ii < file.size(); ii++)
					map.put(commands[ii], file.get(ii));
				
				result[i]=map;
			}
			
			return result;
		}
	}


	public class PeerCall {
		private final XmlRpcClient client;
		private Vector<Object> call = new Vector<Object>();

		public PeerCall(XmlRpcClient client, String fileId)
		{
			this.client = client;
			call.add(fileId);
			call.add(0);
		}

		public void addCommand(String command)
		{
			call.add(command+"=");
		}

		@SuppressWarnings("unchecked")
		public HashMap[] execute() throws XmlRpcException, XmlRpcFault
		{
			XmlRpcArray files = (XmlRpcArray)client.invoke("p.multicall", call.toArray());
			HashMap[] result=new HashMap[files.size()];
			//create list of commands. First two parameters are system reserved
			String[] commands=new String[call.size()-2];
			for (int i = 2; i < call.size(); i++) {
				String name=(String) call.get(i);
				//truncate '=' at end
				name=name.substring(0,name.length()-1);
				commands[i-2]=name;
			}
			for (int i = 0; i < files.size(); i++) {
				XmlRpcArray file = (XmlRpcArray) files.get(i);
				HashMap map=new HashMap();
				for (int ii = 0; ii < file.size(); ii++)
					map.put(commands[ii], file.get(ii));

				result[i]=map;
			}

			return result;
		}
	}


	public class TorrentCall {
		private final XmlRpcClient client;
		private Vector<Object> call = new Vector<Object>();

		public TorrentCall(XmlRpcClient client, String viewId)
		{
			this.client = client;
			call.add(viewId);
		}
		
		public void addCommand(String command)
		{
			call.add(command+"=");
		}
		
		public Object[] execute() throws XmlRpcFault
		{
			XmlRpcArray res = (XmlRpcArray)client.invoke("d.multicall", call.toArray());
			return res.toArray();
		}
	}

}