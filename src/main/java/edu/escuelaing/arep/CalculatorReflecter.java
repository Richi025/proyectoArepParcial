package edu.escuelaing.arep;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.management.RuntimeErrorException;

public class CalculatorReflecter {


    public Object invoke(String methodInvoque) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Class <?> clase = Math.class;
        Method method = null;

        try {
            method = clase.getMethod(methodInvoque);
        } catch (Error e) {
            throw new RuntimeErrorException(e);
        }
        Object res = null;
        try {
            res = method.invoke(null);
        } catch (Error e) {
            throw new RuntimeErrorException(e);
        }
        return res;
    }   
}




        



