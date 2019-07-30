package com.github.zhangyanwei.au.weather.service;


import com.github.zhangyanwei.au.weather.model.City;
import com.github.zhangyanwei.au.weather.model.CityWeather;
import com.github.zhangyanwei.au.weather.provider.impl.OpenCityWeatherProvider;
import com.github.zhangyanwei.au.weather.provider.impl.OpenCityWeatherProvider.WeatherResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(OpenCityWeatherProvider.class)
@PowerMockIgnore({ "javax.*.*", "org.xml.*" })
@SpringBootTest
public class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetCities() {
        // Execute
        List<City> cities = weatherService.getCities();

        // Expectation
        assertThat(cities.size(), equalTo(3));
        assertThat(cities, contains(
                hasProperty("code", equalTo("2147714")),
                hasProperty("code", equalTo("7839805")),
                hasProperty("code", equalTo("7839791"))
        ));
    }

    @Test
    public void shouldSendWeather() throws Exception {
        // Given
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setMain(new WeatherResponse.Main());
        weatherResponse.setWeather(new WeatherResponse.Weather[]{new WeatherResponse.Weather()});
        weatherResponse.setWind(new WeatherResponse.Wind());
        weatherResponse.getWind().setSpeed(10F);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        whenNew(RestTemplate.class).withNoArguments().thenReturn(mockRestTemplate);
        given(mockRestTemplate.getForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(WeatherResponse.class)))
                .willReturn(ResponseEntity.ok(weatherResponse));

        // Then
        String sessionId = "correct session id";
        weatherService.sendWeather(sessionId, "correct city code");

        // Verify & Expectation
        ArgumentCaptor<CityWeather> weatherArgumentCaptor = ArgumentCaptor.forClass(CityWeather.class);
        ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(MessageHeaders.class);
        verify(messagingTemplate).convertAndSendToUser(eq(sessionId), eq("/queue/weather"), weatherArgumentCaptor.capture(), messageHeadersArgumentCaptor.capture());

        // Sample Expectation
        CityWeather cityWeather = weatherArgumentCaptor.getValue();
        assertThat(cityWeather.getWind(), equalTo(10F));
        MessageHeaders messageHeaders = messageHeadersArgumentCaptor.getValue();
        assertThat(messageHeaders.get(SESSION_ID_HEADER), equalTo(sessionId));
    }

}
