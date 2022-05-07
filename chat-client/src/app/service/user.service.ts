import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../model/user';

const baseUrl = 'http://localhost:8080/Chat-war/api/chat/';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  isSignedIn = false;
  loggedInUsers: User[] = [];

  constructor(private http : HttpClient) { }

  signIn(user : User ) {
    return this.http.post(baseUrl + "login", user);
  }
  
  signOut() {
    this.isSignedIn = false;
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
}
