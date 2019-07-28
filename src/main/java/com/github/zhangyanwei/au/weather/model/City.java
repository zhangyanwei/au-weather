package com.github.zhangyanwei.au.weather.model;

import lombok.Data;

@Data
public class City {

    /**
     * City code, you can find it from the link: http://bulk.openweathermap.org/sample/
     */
    private String code;

    /**
     * City name just for display. Will only use the city code to retrieve the weather information.
     */
    private String name;
}
