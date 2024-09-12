package edu.escuelaing.arep;

import java.net.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class ServerHTTPfachada {
   public static void main(String[] args) throws IOException, NoSuchMethodException, SecurityException,
         IllegalAccessException, IllegalArgumentException, InvocationTargetException {

      int port = 36000;
      try (ServerSocket serverSocket = new ServerSocket(port)) {
         System.out.println("Fachada  en el puerto " + port);
         while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
               BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
               String inputLine;
               String comando = null;
               while ((inputLine = in.readLine()) != null) {
                  if (inputLine.startsWith("GET")) {
                     comando = inputLine.split(" ")[1].substring(10); // Extraer comando después de "/computar?comando="
                     break;
                  }
                  if (inputLine.isEmpty()) {
                     break;
                  }
               }

               String resultado = sentAReflexCalculator(comando);
               String index = "HTTP/1.1 200 OK\r\n"
                              + "Content-Type: text/html\r\n"
                              + "\r\n"
                              + "<!DOCTYPE html>\n"
                              + "<html>\n"
                              + "   <head>"
                              + "       <title>Form Example</title>\n"
                              + "       <meta charset=\"UTF-8\">\n"
                              + "       <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                              + "    </head>\n"
                              + "    <body>\n"
                              + "       <h1>Form with GET</h1>\n"
                              + "       <form action=\"/consulta\">\n"
                              + "             <label for=\"name\">Name:</label><br>\n"
                              + "             <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n"
                              + "             <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n"
                              + "       </form>\n"
                              + "       <div id=\"getrespmsg\"></div>\n"
                              + "       <script>\n"
                              + "            function loadGetMsg() {\n"
                              + "                let nameVar = document.getElementById(\"name\").value;\n"
                              + "                const xhttp = new XMLHttpRequest();\n"
                              + "                xhttp.onload = function() {\n"
                              + "                   document.getElementById(\"getrespmsg\").innerHTML = this.responseText;\n"
                              + "                }\n"
                              + "                xhttp.open(\"GET\", \"/consulta?name=\"+nameVar);\n"
                              + "                xhttp.send();\n"
                              + "             }\n"
                              + "       </script>\n"
                              + "       <h1>Form with POST</h1>\n"
                              + "       <form action=\"/hellopost\">\n"
                              + "             <label for=\"postname\">Name:</label><br>\n"
                              + "             <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\n"
                              + "             <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\n"
                              + "       </form>\n"
                              + "       <div id=\"postrespmsg\"></div>\n"
                              + "       <script>\n"
                              + "            function loadPostMsg(name){\n"
                              + "                let url = \"/hellopost?name=\" + name.value;\n"
                              + "                fetch (url, {method: 'POST'})\n"
                              + "                   .then(x => x.text())\n"
                              + "                   .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\n"
                              + "             }\n"
                              + "       </script>\n"
                              + "    </body>\n"
                              + " </html>\n";

               out.println("HTTP/1.1 200 OK");
               out.println("Content-Type: application/json");
               out.println("Content-Length: " + resultado.length());
               out.println();
               out.println(index);
               out.println(resultado);
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static String sentAReflexCalculator(String metodo) throws IOException, NoSuchMethodException,
         SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      try (Socket calculatorSocket = new Socket("localhost", 5000)) {
         PrintWriter out = new PrintWriter(calculatorSocket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(calculatorSocket.getInputStream()));
         out.println(metodo);
         CalculatorReflecter calculatorReflecter = new CalculatorReflecter();
         StringBuilder response = new StringBuilder();
         String line;
         calculatorReflecter.invoke(metodo);
         while ((line = in.readLine()) != null) {
            response.append(line);
         }
         return response.toString();
      }
   }

}
