import { Component, signal ,inject} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { OrderService } from './order-service';


@Component({
  imports: [RouterOutlet, AsyncPipe],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('client');

  bang="HEY THERE バナナ";
  testt=inject(OrderService);
  orders$=this.testt.getOrders(102);
  constructor(){
    
  }
  
  
}
