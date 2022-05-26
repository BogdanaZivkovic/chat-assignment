package chatmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import models.User;

// TODO Implement the rest of Client-Server functionalities 
/**
 * Session Bean implementation class ChatBean
 */
@Singleton
@LocalBean
public class ChatManagerBean implements ChatManagerRemote, ChatManagerLocal{

	private List<User> registered = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	
	public ChatManagerBean() {
	}

	@Override
	public User register(User user) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(user.getUsername()));
		if(exists) 
			return null;
		
		registered.add(user); 
		return user;
	}

	@Override
	public boolean login(User user) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword()));
		//boolean alreadyLoggedIn = loggedIn.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(!exists) {
			return false;
		}
		else {
			loggedIn.add(user);
			return true;
		}
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
}
