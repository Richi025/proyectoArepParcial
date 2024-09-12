package edu.escuelaing.arep;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CalculatorReflexServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(36000);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line;
            String cmd = null;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("GET /calculate?command=")) {
                    cmd = line.split("=")[1].split(" ")[0];
                    break;
                }
                if (!in.ready()) {
                    break;
                }
            }

            if (cmd != null) {
                String op = parseOperation(cmd);
                List<Double> argsList = parseParameters(cmd);

                String result = performOperation(op, argsList);
                out.println("HTTP/1.1 200 OK\r\n" + "Content-Type: application/json\r\n" + "\r\n" + "{\"result of operation\": " + result + "}");
            }
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    public static String parseOperation(String input) {
        return input.substring(0, input.indexOf('('));
    }

    public static List<Double> parseParameters(String input) {
        List<Double> parameters = new ArrayList<>();
        String paramString = input.substring(input.indexOf('(') + 1, input.indexOf(')'));
        if (!paramString.isEmpty()) {
            String[] paramArray = paramString.split(",");
            for (String param : paramArray) {
                parameters.add(Double.parseDouble(param.trim()));
            }
        }
        return parameters;
    }

    public static String performOperation(String operation, List<Double> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if ("pi".equals(operation)) {
            return String.valueOf(Math.PI);
        } else if ("bbsort".equals(operation)) {
            return sortList(params).toString();
        } else {
            Class<?> clazz = Math.class;
            if (params.size() == 1) {
                Method method = clazz.getMethod(operation, double.class);
                Double result = (Double) method.invoke(null, params.get(0));
                return result.toString();
            } else if (params.size() == 2) {
                Method method = clazz.getMethod(operation, double.class, double.class);
                Double result = (Double) method.invoke(null, params.get(0), params.get(1));
                return result.toString();
            } else {
                throw new IllegalArgumentException("Incorrect number of parameters");
            }
        }
    }

    public static List<Double> sortList(List<Double> numbers) {
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size() - 1 - i; j++) {
                if (numbers.get(j) > numbers.get(j + 1)) {
                    Double temp = numbers.get(j);
                    numbers.set(j, numbers.get(j + 1));
                    numbers.set(j + 1, temp);
                }
            }
        }
        return numbers;
    }
}
