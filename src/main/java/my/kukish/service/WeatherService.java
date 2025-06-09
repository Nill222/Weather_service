package my.kukish.service;

import my.kukish.dto.WeatherData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class WeatherService {

    public WeatherData getWeatherData(double latitude, double longitude) throws IOException {
        String urlStr = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m",
                latitude, longitude);

        System.out.println("Запрос к погодному API: " + urlStr);

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int status = connection.getResponseCode();
        System.out.println("Статус ответа: " + status);

        InputStream inputStream;
        if (status == 200) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
            String errorResponse = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            System.err.println("Ошибка от API: " + errorResponse);
            throw new IOException("Ошибка получения данных с погодного API, статус: " + status);
        }

        String response = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining());

        JSONObject json = new JSONObject(response);
        JSONObject hourly = json.getJSONObject("hourly");
        JSONArray timeArray = hourly.getJSONArray("time");
        JSONArray temperatureArray = hourly.getJSONArray("temperature_2m");

        List<String> timeList = new ArrayList<>();
        List<Double> tempList = new ArrayList<>();

        for (int i = 0; i < 24 && i < timeArray.length(); i++) {
            timeList.add(timeArray.getString(i));
            tempList.add(temperatureArray.getDouble(i));
        }

        return new WeatherData(timeList, tempList);
    }

}

