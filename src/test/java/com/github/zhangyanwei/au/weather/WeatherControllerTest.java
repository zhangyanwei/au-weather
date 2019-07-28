package com.github.zhangyanwei.au.weather;

import com.github.zhangyanwei.au.weather.model.City;
import com.github.zhangyanwei.au.weather.service.WeatherService;
import com.github.zhangyanwei.au.weather.web.WeatherController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * There are multiple strategies to write the unit test cases, here we choose the 'MockMVC with WebApplicationContext'
 * strategy.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    public void canRetrieveByIdWhenExists() throws Exception {
        // Given
        City city = new City();
        city.setCode("2147714");
        city.setName("Sydney");
        given(weatherService.getCities()).willReturn(newArrayList(city));

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/")
                        .accept(MediaType.TEXT_HTML))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(), containsString(
                "data-value=\"2147714\""
        ));
    }

}
