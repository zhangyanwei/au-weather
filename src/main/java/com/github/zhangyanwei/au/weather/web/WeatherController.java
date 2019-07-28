package com.github.zhangyanwei.au.weather.web;

import com.github.zhangyanwei.au.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;

    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("cities", weatherService.getCities());
        return "index";
    }

    @MessageMapping("/weather")
    public void updateWeather(@Payload String cityCode, SimpMessageHeaderAccessor accessor) {
        weatherService.sendWeather(accessor.getSessionId(), cityCode);
    }

}
