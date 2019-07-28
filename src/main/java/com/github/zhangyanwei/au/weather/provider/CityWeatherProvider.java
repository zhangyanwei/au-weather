package com.github.zhangyanwei.au.weather.provider;

import com.github.zhangyanwei.au.weather.model.CityWeather;

public interface CityWeatherProvider {
    CityWeather get(String cityCode);
}
