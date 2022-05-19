package rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import chatmanager.ChatManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.Message;
import models.User;

@Stateless
@Path("/messages")
public class MessageRestBean implements MessageRest{

	@EJB
	private MessageManagerRemote messageManager;
	@EJB
	private ChatManagerRemote chatManager;
	
	@Override
	public void sendMessageToAll(Message message) {	
		AgentMessage agentMessage = new AgentMessage();
		
		for (User loggedInUser : chatManager.loggedInUsers()) {
			agentMessage.userArgs.put("receiver", loggedInUser.getUsername());
			agentMessage.userArgs.put("sender", message.getSender().getUsername());
			agentMessage.userArgs.put("subject", message.getSubject());
			agentMessage.userArgs.put("content", message.getContent());	
			agentMessage.userArgs.put("command", "MESSAGE");
			messageManager.post(agentMessage);
		}
	}

	@Override
	public void sendMessageToUser(Message message) {
		AgentMessage agentMessage = new AgentMessage();
		agentMessage.userArgs.put("receiver", message.getReceiver().getUsername());
		agentMessage.userArgs.put("sender", message.getSender().getUsername());
		agentMessage.userArgs.put("subject", message.getSubject());
		agentMessage.userArgs.put("content", message.getContent());	
		agentMessage.userArgs.put("command", "MESSAGE");
		messageManager.post(agentMessage);				
	}


	@Override
	public void getUserMessages(String username) {
		AgentMessage agentMessage = new AgentMessage();
		agentMessage.userArgs.put("receiver", username);
		agentMessage.userArgs.put("command", "GET_MESSAGES");
		messageManager.post(agentMessage);		
	}
}
