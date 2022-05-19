package rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import agentmanager.AgentManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.Message;
import util.JNDILookup;

@Stateless
@Path("/messages")
public class MessageRestBean implements MessageRest{

	@EJB
	private MessageManagerRemote messageManager;
	
	@Override
	public void sendMessageToAll() {	
	}

	@Override
	public void sendMessageToUser(Message message) {
		AgentMessage agentMessage = new AgentMessage();
		agentMessage.userArgs.put("receiver", message.getReceiver().getUsername());
		agentMessage.userArgs.put("sender", message.getSender().getUsername());
		agentMessage.userArgs.put("subject", message.getSubject());
		agentMessage.userArgs.put("content", message.getContent());	
		messageManager.post(agentMessage);				
	}


	@Override
	public void getUserMessages(String username) {
		
		
	}

}
