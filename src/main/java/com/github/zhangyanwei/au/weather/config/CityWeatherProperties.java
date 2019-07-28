package com.github.zhangyanwei.au.weather.config;

import com.github.zhangyanwei.au.weather.model.City;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "city-weather")
@Data
public class CityWeatherProperties {
    private List<City> cities;
}
