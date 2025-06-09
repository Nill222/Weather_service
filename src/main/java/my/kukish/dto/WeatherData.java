package my.kukish.dto;

import java.util.List;

public class WeatherData {
    private final List<String> time;
    private final List<Double> temperature;

    public WeatherData(List<String> time, List<Double> temperature) {
        this.time = time;
        this.temperature = temperature;
    }

    public List<String> getTime() {
        return time;
    }

    public List<Double> getTemperature() {
        return temperature;
    }
}

