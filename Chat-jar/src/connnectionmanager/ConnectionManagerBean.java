package connnectionmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
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
import util.FileUtils;
import ws.WSChat;

@Singleton
@Startup
@Remote(ConnectionManager.class)
@LocalBean
@Path("/connection")
public class ConnectionManagerBean implements ConnectionManager{

	private Host localNode;
	private List<String> connectedNodes = new ArrayList<String>();
	
	@EJB ChatManagerRemote chatManager;
	
	@EJB WSChat ws;
	
	@PostConstruct
	private void init() {
		try {	
			String address = getNodeAddress();
			String alias = System.getProperty("jboss.node.name") + ":8080";
			String master = getMaster() + ":8080"; 
			
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
		connectedNodes.add(nodeAlias);	
	}
	
	@Override
	public void deleteNode(String alias) {
		// TODO Auto-generated method stub
		
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
}
