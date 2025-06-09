package my.kukish.service;

import my.kukish.dto.CityCoordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeoService {

    public CityCoordinates getCoordinates(String city) throws Exception {
        String urlStr = "https://geocoding-api.open-meteo.com/v1/search?name=" + city;

        System.out.println("Запрос к геокодеру: " + urlStr);

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int status = connection.getResponseCode();
        System.out.println("Статус ответа геокодера: " + status);

        if (status != 200) {
            throw new RuntimeException("Ошибка при вызове геокодера, статус: " + status);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        System.out.println("Ответ от геокодера: " + response);

        JSONObject json = new JSONObject(response.toString());
        JSONArray results = json.optJSONArray("results");

        if (results == null || results.length() == 0) {
            throw new RuntimeException("Город не найден: " + city);
        }

        JSONObject firstResult = results.getJSONObject(0);
        double latitude = firstResult.getDouble("latitude");
        double longitude = firstResult.getDouble("longitude");

        System.out.println("Найденные координаты: широта=" + latitude + ", долгота=" + longitude);

        return new CityCoordinates(latitude, longitude);
    }
}

