package com.ylitormatech.sensorserver.resources.tests.controller;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylitormatech.sensorserver.controller.SensorRestController;
import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.service.JmsService;
import com.ylitormatech.sensorserver.domain.service.RestService;
import com.ylitormatech.sensorserver.domain.service.SensorDatatypeService;

import com.ylitormatech.sensorserver.domain.service.SensorService;
import com.ylitormatech.sensorserver.resources.tests.utils.SensorDTOBuilder;
import com.ylitormatech.sensorserver.resources.tests.utils.SensorDataTypeDTOBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Perttu Vanharanta on 2.8.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"classpath*:**/testContext.xml"})
public class SensorRestControllerTest {

    private final String dummyHeaderValue = "DummyHeaderValue";
    private MockMvc mockMvc;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Mock
    private RestService restServiceMock;

    @Mock
    private JmsService jmsServiceMock;

    @Mock
    private SensorDatatypeService sensorDatatypeServiceMock;

    @Mock
    private SensorService sensorServiceMock;

    @InjectMocks
    private SensorRestController sensorRestController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.reset(sensorServiceMock);

        mockMvc = MockMvcBuilders.standaloneSetup(sensorRestController).build();
    }


    @Test
    public void getSensorById_ShouldGetSensorById() throws Exception{
        SensorDatatypeEntity firstDataType = new SensorDataTypeDTOBuilder()
                .id(1)
                .name("TMP")
                .labeltoken("sensor.type.checkbox.label.tmp")
                .build();
        SensorDatatypeEntity secondDataType = new SensorDataTypeDTOBuilder()
                .id(2)
                .name("GPS")
                .labeltoken("sensor.type.checkbox.label.gps")
                .build();
        Set<SensorDatatypeEntity> sensorDataTypeDTOs = new HashSet<SensorDatatypeEntity>();
        sensorDataTypeDTOs.add(firstDataType);
        sensorDataTypeDTOs.add(secondDataType);

        SensorEntity found = new SensorDTOBuilder()
                .id(1)
                .name("Sensor1")
                .lat(0)
                .lon(0)
                .sensorDataTypeDTOs(sensorDataTypeDTOs)
                .apikey("Apikey")
                .userid(1)
                .active(true)
                .onLogSend(true)
                .build();

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(1);
        when(sensorServiceMock.restFind(1,1)).thenReturn(found);
        mockMvc.perform(get("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(10)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Sensor1")))
                .andExpect(jsonPath("$.lat", is(0.0)))
                .andExpect(jsonPath("$.lon", is(0.0)))
                .andExpect(jsonPath("$.sensorDatatypeEntities", hasSize(2)))
                .andExpect(jsonPath("$.sensorDatatypeEntities[*].id", containsInAnyOrder(1,2)))
                .andExpect(jsonPath("$.sensorDatatypeEntities[*].name", containsInAnyOrder("TMP","GPS")))
                .andExpect(jsonPath("$.sensorDatatypeEntities[*].labeltoken", containsInAnyOrder("sensor.type.checkbox.label.tmp","sensor.type.checkbox.label.gps")))
                .andExpect(jsonPath("$.apikey", is("Apikey")))
                .andExpect(jsonPath("$.userid", is(1)))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.onLogSend", is(true)))
                .andExpect(jsonPath("$.new", is(false)));
    }

    @Test
    public void getSensorById_InvalidUserId() throws Exception {

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(-1);
        mockMvc.perform(get("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.get.error.invalid.user")));
    }

    @Test
    public void getSensorById_SensorNotFound() throws Exception {

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(1);
        when(sensorServiceMock.restFind(1,1)).thenReturn(null);
        mockMvc.perform(get("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isNoContent());
    }


    /* Not work correctly yet
    @Test
    public void getSensorById_JsonParseException() throws Exception {
        SensorDatatypeEntity firstDataType = new SensorDataTypeDTOBuilder()
                .id(1)
                .name("TMP")
                .labeltoken("sensor.type.checkbox.label.tmp")
                .build();
        SensorDatatypeEntity secondDataType = new SensorDataTypeDTOBuilder()
                .id(2)
                .name("GPS")
                .labeltoken("sensor.type.checkbox.label.gps")
                .build();
        Set<SensorDatatypeEntity> sensorDataTypeDTOs = new HashSet<SensorDatatypeEntity>();
        sensorDataTypeDTOs.add(firstDataType);
        sensorDataTypeDTOs.add(secondDataType);

        SensorEntity found = new SensorDTOBuilder()
                .id(1)
                .name("Sensor1")
                .lat(0)
                .lon(0)
                .sensorDataTypeDTOs(sensorDataTypeDTOs)
                .apikey("Apikey")
                .userid(1)
                .active(true)
                .onLogSend(true)
                .build();

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(1);
        when(sensorServiceMock.restFind(1,1)).thenReturn(found);
        thrown.expect(JsonProcessingException.class);
        ObjectMapper om = Mockito.spy( new ObjectMapper());
        Mockito.when( om.writeValueAsString(found)).thenThrow( new JsonProcessingException("") {});
        mockMvc.perform(get("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.get.error.json.parsing")));

    }*/


    /*
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
    }*/
}
