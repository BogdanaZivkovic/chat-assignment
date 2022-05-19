package agents;

import java.time.LocalDateTime;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import chatmanager.ChatManagerRemote;
import models.User;
import ws.WSChat;
/**
 * Sledece nedelje cemo prebaciti poruke koje krajnji korisnik treba da vidi da se 
 * salju preko Web Socketa na front-end (klijentski deo aplikacije)
 * @author Aleksandra
 *
 */
@Stateful
@Remote(Agent.class)
public class UserAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String agentId;
	
	@EJB
	private CachedAgentsRemote cachedAgents;
	@EJB
	private WSChat ws;

	
	@PostConstruct
	public void postConstruct() {
		System.out.println("Created User Agent!");
	}
	
	@Override
	public void handleMessage(Message msg) {
		TextMessage tmsg = (TextMessage) msg;
		String receiver;
		String sender;
		String content;
		String subject;
		String response;
		try {
			receiver = (String) tmsg.getObjectProperty("receiver");
			sender = (String) tmsg.getObjectProperty("sender");
			content = (String) tmsg.getObjectProperty("content");
			subject = (String) tmsg.getObjectProperty("subject");
			if (receiver.equals(agentId)) {
				System.out.println("Received from: " + sender);
				System.out.println("Subject: " + subject);
				System.out.println("Content: " + content);

				response = "MESSAGE_USER!" + sender;
				ws.onMessage(receiver, response);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String init(String agentId) {
		this.agentId = agentId;
		cachedAgents.addRunningAgent(agentId, this);
		return agentId;
	}

	private String generateId() {
		Random r = new Random();
		int low = 10;
		int high = 100;
		return Integer.toString(r.nextInt(high - low) + low);
	}

	@Override
	public String getAgentId() {
		return agentId;
	}
}
