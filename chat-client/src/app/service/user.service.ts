import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../model/user';

const baseUrl = 'http://localhost:8080/Chat-war/api/chat/';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  isSignedIn = false;
  username = "";
  loggedInUsers: User[] = [];
  registeredUsers: User[] = [];

  constructor(private http : HttpClient) { }

  signIn(user : User ) {
    return this.http.post(baseUrl + "login", user);
  }

  setLoggedInUsername(username: string) {
    this.username = username;
  }
  
  signOut() {
    this.isSignedIn = false;
    return this.http.delete(baseUrl + "loggedIn/" + this.username);

  }

  register(user: User) {
    return this.http.post(baseUrl + "register", user);
  }

  setLoggedInUsers(users : User[]) {
    this.loggedInUsers = users;
  }

  getLoggedInUsers() {
    return this.http.get(baseUrl + "loggedIn");
  }

  setRegisteredUsers(users : User[]) {
    this.registeredUsers = users;
  }

  getRegisteredUsers() {
    return this.http.get(baseUrl + "registered");
  }

}
