package com.github.zhangyanwei.au.weather.websocket;


import com.github.zhangyanwei.au.weather.model.CityWeather;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WeatherWebSocketTest {

    @LocalServerPort
    private int port;

    private String url;

    @Before
    public void setup1() {
        url = format("ws://127.0.0.1:%s/ws", port);
    }


    @Test
    public void shouldSubscribeWeather() throws InterruptedException, ExecutionException, TimeoutException {

        // Prepare Data
        String cityCode = "2147714";

        // Connect and subscribe (Extract it as a method if we want to write more tests)
        CompletableFuture<CityWeather> frameFuture = new CompletableFuture<>();
        WebSocketStompClient stompClient = createStompClient();
        StompSession session = stompClient
                .connect(url, createCorrectConnectionHeaders(cityCode), new StompSessionHandlerAdapter() {})
                .get(3, SECONDS);
        session.subscribe("/user/queue/weather", subscriptionFrameHandler(frameFuture));
        session.send("/app/weather", cityCode);
        CityWeather cityWeather = frameFuture.get(30, SECONDS);

        // Expectation
        assertThat(cityWeather, notNullValue());
    }

    private WebSocketStompClient createStompClient() {
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

    private WebSocketHttpHeaders createCorrectConnectionHeaders(String cityCode) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.set("cityCode", cityCode);
        return headers;
    }

    private StompFrameHandler subscriptionFrameHandler(CompletableFuture<CityWeather> frameFuture) {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return CityWeather.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                frameFuture.complete((CityWeather) payload);
            }
        };
    }
}
