package edu.escuelaing.arep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class ServerHTTPfachada {
   public static void main(String[] args) throws IOException {
      ServerSocket serverSocket = new ServerSocket(36001);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line;
            String cmd = null;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("GET /execute?command=")) {
                    cmd = line.split("=")[1].split(" ")[0];
                    break;
                }
                if (!in.ready()) {
                    break;
                }
            }

            if (cmd != null) {
                URL reflexCalcUrl = new URL("http://localhost:36000/calculate?command=" + cmd);
                HttpURLConnection conn = (HttpURLConnection) reflexCalcUrl.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader responseIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = responseIn.readLine();
                responseIn.close();

                out.println("HTTP/1.1 200 OK\r\n" + "Content-Type: application/json\r\n" + "\r\n" + response);
            } else {
                String htmlContent = generateClientHtml();
                out.println("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + htmlContent);
            }

            out.close();
            in.close();
            clientSocket.close();
        }
    }

    public static String generateClientHtml() {
        return "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta charset=\"UTF-8\">\n"
            + "<title>Reflexive Calculator</title>\n"
            + "</head>\n"
            + "<body>\n"
            + "<h1>Reflexive Calculator</h1>\n"
            + "<form>\n"
            + "<label for=\"command\">Command with parameters:</label><br>\n"
            + "<input type=\"text\" id=\"command\" name=\"command\" placeholder=\"insertOperation\"><br><br>\n"
            + "<input type=\"button\" value=\"Calculate\" onclick=\"sendRequest()\">\n"
            + "</form>\n"
            + "<div id=\"response\"></div>\n"
            + "<script>\n"
            + "function sendRequest() {\n"
            + "let command = document.getElementById(\"command\").value;\n"
            + "const xhr = new XMLHttpRequest();\n"
            + "xhr.onload = function() {\n"
            + "document.getElementById(\"response\").innerHTML = this.responseText;\n"
            + "}\n"
            + "xhr.open(\"GET\", \"/execute?command=\" + command);\n"
            + "xhr.send();\n"
            + "}\n"
            + "</script>\n"
            + "</body>\n"
            + "</html>";
    }
}
