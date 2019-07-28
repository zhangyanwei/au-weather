package com.github.zhangyanwei.au.weather.service;

import com.github.zhangyanwei.au.weather.config.CityWeatherProperties;
import com.github.zhangyanwei.au.weather.model.City;
import com.github.zhangyanwei.au.weather.model.CityWeather;
import com.github.zhangyanwei.au.weather.provider.CityWeatherProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    private final CityWeatherProperties cityWeatherProperties;
    private final CityWeatherProvider cityWeatherProvider;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WeatherService(CityWeatherProperties cityWeatherProperties,
                          CityWeatherProvider cityWeatherProvider,
                          SimpMessagingTemplate messagingTemplate) {
        this.cityWeatherProperties = cityWeatherProperties;
        this.cityWeatherProvider = cityWeatherProvider;
        this.messagingTemplate = messagingTemplate;
    }

    public List<City> getCities() {
        return cityWeatherProperties.getCities();
    }

    public void sendWeather(String sessionId, String cityCode) {
        CityWeather cityWeather = cityWeatherProvider.get(cityCode);
        if (cityWeather != null) {
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/weather", cityWeather, createHeaders(sessionId));
        }
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
