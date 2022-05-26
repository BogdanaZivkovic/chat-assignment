package connnectionmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import chatmanager.ChatManagerRemote;
import models.Host;
import models.User;
import util.FileUtils;
import ws.WSChat;

@Singleton
@Startup
@Remote(ConnectionManager.class)
@LocalBean
@Path("/connection")
public class ConnectionManagerBean implements ConnectionManager{

	private Host localNode = new Host();
	private List<String> connectedNodes = new ArrayList<String>();
	
	@EJB ChatManagerRemote chatManager;
	
	@EJB WSChat ws;
	
	@PostConstruct
	private void init() {
		try {	
			String address = getNodeAddress();
			String alias = System.getProperty("jboss.node.name") + ":8080";
			String master = getMaster() + ":8080"; 
			
			localNode.setAddress(address);
			localNode.setAlias(alias);
			
			System.out.println(("MASTER ADDR: " + master + ", node name: " + alias + ", node address: " + address));
			if (master != null && !master.equals(":8080")) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "/Chat-war/api/connection");
				ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
				connectedNodes = rest.registerNode(alias);
				connectedNodes.remove(alias);
				connectedNodes.add(master);
				System.out.println("Handshake successful. Connected nodes: " + connectedNodes);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public List<String> registerNode(String connection) {
		System.out.println("New node registered: " + connection);
		for (String c : connectedNodes) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = client.target("http://" + c + "/Chat-war/api/connection");
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
			rest.addNode(connection);
		}
		connectedNodes.add(connection);
		return connectedNodes;
	}
	
	
	@Override
	public void addNode(String nodeAlias) {
		System.out.println("Added node: " + nodeAlias);
		connectedNodes.add(nodeAlias);	
	}
	
	@Override
	public void deleteNode(String nodeAlias) {
		System.out.println("Deleted node: " + nodeAlias);		
		connectedNodes.remove(nodeAlias);		
	}
	
	@Override
	public String pingNode() {
		System.out.println("Ping");
		return "Ok";
	}
	
	private String getNodeAddress() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			return (String) mBeanServer.getAttribute(http, "boundAddress");
		} catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	private String getMaster() {
		try {
			File f = FileUtils.getFile(ConnectionManager.class, "", "connections.properties");
			FileInputStream fileInput = new FileInputStream(f);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			return properties.getProperty("master");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<String> getNodes() {
		return connectedNodes;
	}


	@Override
	public List<User> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@PreDestroy
	private void shutDown() {
		notifyAllToShutDownNode(localNode.getAlias());
	}	
	
	private void notifyAllToShutDownNode(String alias) {
		for (String cn: connectedNodes) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = client.target("http://" + cn + "/Chat-war/api/connection");
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
			rest.deleteNode(localNode.getAlias());
			client.close();	
		}
	}
	
	@Schedule(hour = "*", minute="*", second="*/60")
	private void heartbeat() {
		System.out.println("Heartbeat protocol");
		for(String cn : connectedNodes) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean nodeAlive = isNodeAlive(cn);
					if(!nodeAlive) {
						System.out.println("Deleting unresponsive node" + cn);
						connectedNodes.remove(cn);
						notifyAllToShutDownNode(cn);
					}
				}
			}).start();
		}
	}
	
	private boolean isNodeAlive(String nodeAlias) {
		
		int pingTriesCount = 2;
		boolean nodeAlive = false;
		
		for(int i=0; i <pingTriesCount; i++) {
			try {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + nodeAlias + "/Chat-war/api/connection");
				ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
				String response = rest.pingNode();
				client.close();
				if(response.equals("Ok")) {
					nodeAlive = true;
					break;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}	
		}
		return nodeAlive;
	}
	
	public String getNodeName() {
		return localNode.getAlias();
	}
}
