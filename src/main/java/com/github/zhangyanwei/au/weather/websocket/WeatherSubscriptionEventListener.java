package com.github.zhangyanwei.au.weather.websocket;

import com.github.zhangyanwei.au.weather.service.WeatherService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.messaging.support.MessageHeaderAccessor.getAccessor;


@Slf4j
@Component
public class WeatherSubscriptionEventListener {

    private Map<String, String> cityWeatherSubscriptions = new ConcurrentHashMap<>();

    private final WeatherService weatherService;

    public WeatherSubscriptionEventListener(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * In this example, I pull the weather information in a schedule because of the service provider not provide
     * a method to notice the consumers the weather updated. </br>
     * And, this also will reduce the requests to the service provider if the APIs not free.
     */
    @Scheduled(cron = "${city-weather.scheduler.cron:0 */10 * ? * *}")
    public void sendWeatherToSubscribers() {
        log.info("[Scheduled] Sending weather to subscribers.");
        cityWeatherSubscriptions.forEach(weatherService::sendWeather);
    }

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        log.info("Connected...");
        SimpMessageHeaderAccessor accessor = getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        if (accessor != null) {
            String sessionId = accessor.getSessionId();
            String cityCode = accessor.getFirstNativeHeader("cityCode");
            if (!Strings.isNullOrEmpty(cityCode)) {
                cityWeatherSubscriptions.put(sessionId, cityCode);
            }
        }
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        log.info("Disconnect...");
        SimpMessageHeaderAccessor accessor = getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        if (accessor != null) {
            String sessionId = accessor.getSessionId();
            cityWeatherSubscriptions.remove(sessionId);
        }
    }

}
