import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from './model/user';
import { UserService } from './service/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  title = 'chat-client';

  constructor(private userService: UserService, private router : Router) {}

  ngOnInit(): void {
    this.initSocket(this.userService, this.router);
  }
  
  initSocket(userService: UserService, router: Router) {
    let connection: WebSocket|null = new WebSocket("ws://localhost:8080/Chat-war/ws/chat"); // 'chat' should be individual person's username

    connection.onopen = function () {
      console.log("Socket is open");
    }

    connection.onclose = function () {
      connection = null;
    }

    connection.onmessage = function (msg) {
      const data = msg.data.split("!");

      if(data[0] == "LOG_IN" && data[1].includes("Yes")) { 
        
        userService.isSignedIn = true;
        userService.username = data[2];
        console.log(userService.isSignedIn);
        router.navigate(['signed-in-users']);
      }
      else if(data[0] == "REGISTER") {
        alert(data[1]);
      }
      else if(data[0] == "LOGGEDIN") {
        let users: User[] = [];
        data[1].split("|").forEach((user: string) => {
          if (user) {
            let userData = user.split(",");   
            users.push(new User(userData[0], userData[1]))
          }
       });    
       userService.setLoggedInUsers(users);   
      }
      else if(data[0] == "REGISTERED") {
        let users: User[] = [];
        data[1].split("|").forEach((user: string) => {
          if (user) {
            let userData = user.split(",");   
            users.push(new User(userData[0], userData[1]))
          }
       });    
       userService.setRegisteredUsers(users);   
      }
      else {
        alert(data[1]);
      }
    }
  }
}


