package my.kukish;

import com.sun.net.httpserver.HttpServer;
import my.kukish.handler.WeatherHandler;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/weather", new WeatherHandler());
        server.setExecutor(null); // по умолчанию
        server.start();
        System.out.println("Сервер запущен на http://localhost:8080/weather?city=Moscow");
    }
}
