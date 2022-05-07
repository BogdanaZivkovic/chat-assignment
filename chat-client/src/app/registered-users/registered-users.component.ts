import { Component, OnInit } from '@angular/core';
import { Sort } from '@angular/material/sort';
import { User } from '../model/user';

@Component({
  selector: 'app-registered-users',
  templateUrl: './registered-users.component.html',
  styleUrls: ['./registered-users.component.css']
})
export class RegisteredUsersComponent implements OnInit {

  users: User[] = [];
  sortedUsers: User[] = [];

  constructor() { }

  ngOnInit(): void {
    this.users = [new User('pera', 'pera'), new User('mika', 'mika'), new User('zika', 'zika')];
    this.sortedUsers = this.users.slice();
  }

  sortData(sort: Sort) {
    const data = this.users.slice();
    if (!sort.active || sort.direction === '') {
      this.sortedUsers = data;
      return;
    }

    this.sortedUsers = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case 'username': return compare(a.username, b.username, isAsc);
        default: return 0;
      }
    });
  }
}

function compare(a: number | string, b: number | string, isAsc: boolean) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}

