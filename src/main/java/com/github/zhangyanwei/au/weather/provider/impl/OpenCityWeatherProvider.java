package com.github.zhangyanwei.au.weather.provider.impl;

import com.github.zhangyanwei.au.weather.model.CityWeather;
import com.github.zhangyanwei.au.weather.provider.CityWeatherProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;

@Slf4j
@Component
public class OpenCityWeatherProvider implements CityWeatherProvider {

    @Value("${city-weather.provider.open.url}")
    private String url;

    @Value("${city-weather.provider.open.appid}")
    private String appid;

    // For this kind of methods, add @Cacheable on it will feather reduce the unnecessary requests.
    @Override
    public CityWeather get(String cityCode) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("appid", appid)
                .queryParam("id", cityCode)
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WeatherResponse> entity = restTemplate.getForEntity(uri, WeatherResponse.class);
        if (entity.getStatusCode() != HttpStatus.OK) {
            log.warn("Can't retrieve the weather information temporarily, the response status is {}.",
                    entity.getStatusCodeValue());
            return null;
        }
        WeatherResponse weatherResponse = entity.getBody();
        if (weatherResponse == null) {
            log.warn("The response of OpenWeather API can not serialize to WeatherResponse.");
            return null;
        }
        return toCityWeather(weatherResponse);
    }

    private CityWeather toCityWeather(WeatherResponse weatherResponse) {
        CityWeather cityWeather = new CityWeather();
        cityWeather.setCity(weatherResponse.getName());
        WeatherResponse.Weather[] weather = weatherResponse.getWeather();
        cityWeather.setWeather(weather[0].getDescription());
        cityWeather.setTemperature(weatherResponse.getMain().getTemp());
        cityWeather.setWind(weatherResponse.getWind().getSpeed());
        cityWeather.setUpdatedTime(weatherResponse.getDt());
        return cityWeather;
    }

    /**
     * Represents the API response of <a href="https://openweathermap.org/current#cityid">OpenWeather</a>.
     */
    @Data
    public static class WeatherResponse {

        /**
         * City Name
         */
        private String name;

        /**
         * Time of data calculation, unix, UTC
         */
        private Date dt;

        /**
         * Weather summary
         */
        private Weather[] weather;

        /**
         * Weather details contain temperature (others ignored).
         */
        private Main main;

        /**
         * Wind information.
         */
        private Wind wind;

        @Data
        public static class Main {

            /**
             * Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
             */
            private Float temp;
        }

        @Data
        public static class Weather {

            /**
             * Weather condition within the group
             */
            private String description;
        }

        @Data
        public static class Wind {

            /**
             * Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
             */
            private Float speed;
        }
    }

}
