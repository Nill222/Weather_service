package my.kukish.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import my.kukish.dto.CityCoordinates;
import my.kukish.dto.WeatherData;
import my.kukish.service.GeoService;
import my.kukish.service.RedisCacheService;
import my.kukish.service.WeatherService;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherHandler implements HttpHandler {
    private final GeoService geoService = new GeoService();
    private final WeatherService weatherService = new WeatherService();
    private final RedisCacheService redisCacheService = new RedisCacheService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();

        if (query == null || !query.startsWith("city=")) {
            sendResponse(exchange, 400, "Missing or invalid query parameter ?city=...");
            return;
        }

        String city = query.split("=")[1];
        System.out.println("Received request for city: " + city);

        WeatherData weatherData;

        try {
            // 1. Проверка Redis-кэша
            String cached = redisCacheService.get(city);
            if (cached != null) {
                System.out.println("Данные из Redis-кэша");
                JSONObject json = new JSONObject(cached);
                JSONArray timeArr = json.getJSONArray("time");
                JSONArray tempArr = json.getJSONArray("temperature");

                List<String> timeList = new ArrayList<>();
                List<Double> tempList = new ArrayList<>();
                for (int i = 0; i < timeArr.length(); i++) {
                    timeList.add(timeArr.getString(i));
                    tempList.add(tempArr.getDouble(i));
                }
                weatherData = new WeatherData(timeList, tempList);
            } else {
                System.out.println("Данные не найдены в кэше, обращаемся к API");
                CityCoordinates coordinates = geoService.getCoordinates(city);
                weatherData = weatherService.getWeatherData(coordinates.getLatitude(), coordinates.getLongitude());

                JSONObject toCache = new JSONObject();
                toCache.put("time", weatherData.getTime());
                toCache.put("temperature", weatherData.getTemperature());

                redisCacheService.save(city, toCache.toString());
            }

            // Формируем HTML
            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Weather in ")
                    .append(city)
                    .append("</title></head><body><h1>Прогноз погоды на 24 часа для ")
                    .append(city)
                    .append("</h1><table border='1'><tr><th>Время</th><th>Температура (°C)</th></tr>");

            for (int i = 0; i < 24 && i < weatherData.getTime().size(); i++) {
                html.append("<tr><td>")
                        .append(weatherData.getTime().get(i))
                        .append("</td><td>")
                        .append(weatherData.getTemperature().get(i))
                        .append("</td></tr>");
            }

            html.append("</table><br><h2>График температуры</h2>");

            // График
            String timeJson = new JSONArray(weatherData.getTime()).toString();
            String tempJson = new JSONArray(weatherData.getTemperature()).toString();

            String chartUrl = "https://quickchart.io/chart?c=" +
                    URLEncoder.encode("{type:'line',data:{labels:" + timeJson +
                            ",datasets:[{label:'Температура',data:" + tempJson + "}]} }", StandardCharsets.UTF_8);

            html.append("<img src='").append(chartUrl).append("' alt='График температуры' />");

            html.append("</body></html>");

            byte[] response = html.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
        }
    }


    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
