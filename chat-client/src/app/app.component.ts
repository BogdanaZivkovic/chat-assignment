import { Component, OnInit } from '@angular/core';
import { User } from './model/user';
import { UserService } from './service/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  title = 'chat-client';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.initSocket(this.userService);
  }
  
  initSocket(userService: UserService) {
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
        console.log(userService.isSignedIn);
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
      else {
        alert(data[1]);
      }
    }
  }
}


