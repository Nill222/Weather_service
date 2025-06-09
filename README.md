# Weather Service

## Описание

Простой HTTP-сервис прогноза погоды, написанный на Java (без Spring).  
Сервис получает данные о погоде на ближайшие 24 часа по названию города и отображает:

- Таблицу времени и температур
- График температур по часам

## Возможности

- Получение координат города с помощью [Open-Meteo Geocoding API](https://geocoding-api.open-meteo.com)
- Получение прогноза температуры на 24 часа с [Open-Meteo Forecast API](https://api.open-meteo.com)
- Кэширование в Redis (время жизни данных — 15 минут)
- Визуализация данных с помощью QuickChart.io
- Поддержка командной строки, без использования IDE или Spring

## Как запустить

### Требования:
- Java 17+
- Redis (запущен локально по умолчанию: `localhost:6379`)
- Maven

### Сборка и запуск:

```bash
git clone https://github.com/Nill222/Weather_service.git
cd Weather_service
mvn clean package
java -jar target/weather-service.jar
