package my.kukish.controller;

import my.kukish.dto.WeatherData;
import my.kukish.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class WeatherController {

    private final WeatherService weatherService; // сервис, который делает запрос к open-meteo и возвращает JSON

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam String city, Model model) throws IOException {
        String jsonResponse = weatherService.getWeatherJson(city); // получаем JSON с погодой
        WeatherData weatherData = WeatherData.fromJson(jsonResponse);

        // Передаём списки в модель
        model.addAttribute("hours", weatherData.getHours());
        model.addAttribute("temperatures", weatherData.getTemperatures());
        model.addAttribute("city", city);

        return "weather"; // thymeleaf шаблон weather.html
    }
}
