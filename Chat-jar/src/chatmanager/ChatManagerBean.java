package chatmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import models.Message;
import models.User;

// TODO Implement the rest of Client-Server functionalities 
/**
 * Session Bean implementation class ChatBean
 */
@Stateful
@LocalBean
public class ChatManagerBean implements ChatManagerRemote, ChatManagerLocal {

	private List<User> registered = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	private List<Message> messages = new ArrayList<Message>();
	
	/**
	 * Default constructor.
	 */
	public ChatManagerBean() {
	}

	@Override
	public boolean register(User user) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(user.getUsername()));
		if(exists) 
			return false;
		
		registered.add(user); 
		return true;
	}

	@Override
	public boolean login(String username, String password) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists)
			loggedIn.add(new User(username, password));
		return exists;
	}

	@Override
	public List<User> loggedInUsers() {
		return loggedIn;
	}
	
	@Override
	public List<User> registeredUsers() {
		return registered;
	}

	@Override
	public boolean logout(String username) {
		for(User user : loggedIn) {
			if (user.getUsername().equals(username)) {
				loggedIn.remove(user);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean sendMessage(Message message) {
		return messages.add(message);
	}
}
