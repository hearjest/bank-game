import { Injectable } from '@angular/core';
import { Order } from './orders';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({providedIn:"root"})
export class OrderService {

    private url:string;

    constructor(private http:HttpClient){
        this.url='http://localhost:8080/orders/getByItemId';
    }

    public getOrders(id:number):Observable<Order[]>{
        return this.http.get<Order[]>(`${this.url}`,{
            params:{
                id:id
            }
        });
    }

}
