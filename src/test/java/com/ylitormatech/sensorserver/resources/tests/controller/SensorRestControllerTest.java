package com.ylitormatech.sensorserver.resources.tests.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Perttu Vanharanta on 2.8.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/root.xml","classpath*:**/sensor-servlet.xml"})
@WebAppConfiguration
public class SensorRestControllerTest {
    private MockMvc mockMvc;
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RestOperations restOperations;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
    }


    @Test
    public void getDataTypeList_ShouldGetDataTypeList() throws Exception {

        expectRestCallSuccess();
        mockMvc.perform(get("/api/sensors/datatypelist").header("Authorization","123"))
                .andExpect(status().isOk())

                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("TMP")))
                .andExpect(jsonPath("$[0].labeltoken", is("sensor.type.checkbox.label.tmp")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("GPS")))
                .andExpect(jsonPath("$[1].labeltoken", is("sensor.type.checkbox.label.gps")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("FLAG")))
                .andExpect(jsonPath("$[2].labeltoken", is("sensor.type.checkbox.label.flag")))
                .andExpect(jsonPath("$[3].id", is(4)))
                .andExpect(jsonPath("$[3].name", is("HUM")))
                .andExpect(jsonPath("$[3].labeltoken", is("sensor.type.checkbox.label.hum")))
                .andExpect(jsonPath("$[4].id", is(5)))
                .andExpect(jsonPath("$[4].name", is("LUX")))
                .andExpect(jsonPath("$[4].labeltoken", is("sensor.type.checkbox.label.lux")));

    }
    private void expectRestCallSuccess() {
        mockRestServiceServer.expect(
                requestTo("http://localhost:8080/user/id"))
                .andExpect(method(GET))
                .andRespond(withSuccess("{\"id\": \"1\"}", APPLICATION_JSON));
    }
}
