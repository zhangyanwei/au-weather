package com.github.zhangyanwei.au.weather.model;

import lombok.Data;

import java.util.Date;

@Data
public class CityWeather {
    private String city;
    private Date updatedTime;
    private String weather;
    private Float temperature;
    private Float wind;
}
