import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../model/user';

const baseUrl = 'http://localhost:8080/Chat-war/api/users/';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  isSignedIn = false;
  loggedInUsers: User[] = [];
  registeredUsers: User[] = [];
  user: User = new User('', '');

  constructor(private http : HttpClient) { }

  signIn(user : User ) {
    return this.http.post(baseUrl + "login", user).subscribe({
      next: (data) => {
        this.user = data as User;
        this.isSignedIn = true;
      },
      error: () => {alert("Error: Please enter correct username and password!")}
    })
  }
  
  signOut() {
    return this.http.delete(baseUrl + "loggedIn/" + this.user.username).subscribe({
      next: () => {
        this.isSignedIn = false;
        this.user = new User('', '');
      }
    })
  }

  register(user: User) {
    return this.http.post(baseUrl + "register", user).subscribe({
      next: () => {alert("Successfully registered!")},
      error: () => {alert("Error: User with this username already exists!")}
    });
  }

  getLoggedInUsers() {
    return this.http.get(baseUrl + "loggedIn").subscribe({
      next: (loggedInUsers) => {
        this.loggedInUsers = loggedInUsers as User[] 
      }
    })
  }

  getRegisteredUsers() {
    return this.http.get(baseUrl + "registered").subscribe({
      next: (registeredUsers) => {
        this.registeredUsers = registeredUsers as User[] 
      }
    })
  }
}
