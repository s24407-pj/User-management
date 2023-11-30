import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MyCalculatorService {

  constructor() {
  }

  sub(value1: number, value2: number): number {
    return value1 - value2;
  }

  sum(value1: number, value2: number): number {
    return +value1 + +value2;
  }

  multiply(value1: number, value2: number): number {
    return value1 * value2;
  }

  divide(value1: number, value2: number): number {
    return value1 / value2;
  }
}
