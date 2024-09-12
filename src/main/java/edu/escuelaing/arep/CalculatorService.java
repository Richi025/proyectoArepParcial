package edu.escuelaing.arep;


import java.lang.Math;

public class CalculatorService {

    public double suma (double i, double j){
        return i + j;
    }

    public double resta (double i, double j){
        return i - j;
    }

    public double multiplicar (double i, double j){
        return i * j;
    }

    public double dividir (double i, double j){
        if (j>0){
            return i / j;
        }else{
            System.out.println("error división entre 0");
            return -99999;
        }
        
    }

}
