import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Message } from '../model/message';

const baseUrl = 'http://localhost:8080/Chat-war/api/messages/';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private http : HttpClient) {}

  sendMessage(message: Message) {
    return this.http.post(baseUrl + "user", message);
  }
}
