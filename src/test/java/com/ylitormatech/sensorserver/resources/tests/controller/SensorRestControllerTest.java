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
import com.ylitormatech.sensorserver.resources.tests.utils.*;

import com.ylitormatech.sensorserver.web.SensorDataTypeForm;
import com.ylitormatech.sensorserver.web.SensorForm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Perttu Vanharanta on 2.8.2016.
 *
 *  JsonParseException Test's not work correctly
 *  Not correct exception created.
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
        Mockito.reset(sensorDatatypeServiceMock);
        Mockito.reset(jmsServiceMock);
        Mockito.reset(restServiceMock);

        mockMvc = MockMvcBuilders.standaloneSetup(sensorRestController).build();
    }

    /*
    *
    * Sensor create test's
    *
    */

    @Test
    public void createSensor_ShouldCreateSensor() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        SensorEntity found = sensorEntityBuild(userId,sensorId,sensorName);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(true);
        when(sensorServiceMock.restAdd(eq(sensorName),anyList(),eq(userId))).thenReturn(found);
        when(jmsServiceMock.newSensor(found)).thenReturn(null);
        doNothing().when(sensorServiceMock).restUpdateOnlog(found);
        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verify(sensorServiceMock,times(1)).restAdd(eq(sensorName),anyList(),eq(userId));
        verify(sensorServiceMock,times(1)).restUpdateOnlog(found);
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock, times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verify(jmsServiceMock,times(1)).newSensor(found);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    @Test
    public void createSensor_ValidationSensorNameIsNull() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = null;

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.create.error.missing.value")));

        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);

    }


    @Test
    public void createSensor_ValidationSensorDataTypeNameIsNull() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name(null)
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.create.error.missing.value")));

        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);

    }


    @Test
    public void createSensor_ValidationSensorDataTypeMissing() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(null)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.create.error.missing.value")));

        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);

    }


    @Test
    public void createSensor_InvalidUserId() throws Exception{

        int userId=-1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);

        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verify(restServiceMock,times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void createSensor_SensorNameExist() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(false);
        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.create.error.sensor.name.exist")));

        verify(restServiceMock,times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void createSensor_InvalidDataType() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(false);
        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.create.error.invalid.datatype")));

        verify(restServiceMock,times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void createSensor_JmsJsonParseFailure() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        SensorEntity found = sensorEntityBuild(userId,sensorId,sensorName);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("{\"error\":\"sensor.create.error.json.parsing\"}");

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(true);
        when(sensorServiceMock.restAdd(eq(sensorName),anyList(),eq(userId))).thenReturn(found);
        when(jmsServiceMock.newSensor(found)).thenReturn(responseEntity);
        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.create.error.json.parsing")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verify(sensorServiceMock,times(1)).restAdd(eq(sensorName),anyList(),eq(userId));
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock, times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verify(jmsServiceMock,times(1)).newSensor(found);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    @Test
    public void createSensor_JmsOnLogUnavailable() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        SensorEntity found = sensorEntityBuild(userId,sensorId,sensorName);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(("{\"error\":\"sensor.create.error.onlog.unavailable\"}"));

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(true);
        when(sensorServiceMock.restAdd(eq(sensorName),anyList(),eq(userId))).thenReturn(found);
        when(jmsServiceMock.newSensor(found)).thenReturn(responseEntity);
        mockMvc.perform(put("/api/sensors/create")
                .header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.create.error.onlog.unavailable")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verify(sensorServiceMock,times(1)).restAdd(eq(sensorName),anyList(),eq(userId));
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock, times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verify(jmsServiceMock,times(1)).newSensor(found);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    /*
    *
    * Sensor get test's
    *
    */


    @Test
    public void getSensorById_ShouldGetSensorById() throws Exception{

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

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
                .id(sensorId)
                .name(sensorName)
                .lat(0)
                .lon(0)
                .sensorDataTypeDTOs(sensorDataTypeDTOs)
                .apikey("Apikey")
                .userid(userId)
                .active(true)
                .onLogSend(true)
                .build();

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFind(sensorId,userId)).thenReturn(found);
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


        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFind(sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorById_InvalidUserId() throws Exception {

        int userId=-1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        mockMvc.perform(get("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.get.error.invalid.user")));


        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);


    }


    @Test
    public void getSensorById_SensorNotFound() throws Exception {

        int userId=1;
        int sensorId = 1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFind(sensorId,userId)).thenReturn(null);
        mockMvc.perform(get("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isNoContent());


        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFind(sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);

    }


    @Test
    public void getSensorById_JsonParseException() throws Exception {

        int userId=1;
        int sensorId = 1;
        String sensorName = "Sensor1";

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
                .id(sensorId)
                .name(sensorName)
                .lat(0)
                .lon(0)
                .sensorDataTypeDTOs(sensorDataTypeDTOs)
                .apikey("Apikey")
                .userid(userId)
                .active(true)
                .onLogSend(true)
                .build();

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFind(sensorId,userId)).thenReturn(found);

        /*Should be modified use to JsonProcessingException*/
        thrown.expect(MockitoException.class);
        ObjectMapper om = Mockito.mock(ObjectMapper.class);
        Mockito.when( om.writeValueAsString(found)).thenThrow(new Exception());

        mockMvc.perform(get("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.get.error.json.parsing")));



        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFind(sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    /*
    *
    * Sensor delete test's
    *
    */
    @Test
    public void deleteSensorById_ShouldDeleteSensorByIdWhenSentOnLog() throws Exception {

        int sensorId=1;
        int userId=1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(true);
        when(sensorServiceMock.restIsSentToOnLog(sensorId,userId)).thenReturn(true);
        doNothing().when(jmsServiceMock).removeSensor(sensorId);
        doNothing().when(sensorServiceMock).removeMySensor(sensorId,userId);
        mockMvc.perform(delete("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isOk());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verify(sensorServiceMock,times(1)).restIsSentToOnLog(sensorId,userId);
        verify(sensorServiceMock,times(1)).removeMySensor(sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verify(jmsServiceMock,times(1)).removeSensor(sensorId);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    @Test
    public void deleteSensorById_ShouldDeleteSensorByIdWhenNotSentOnLog() throws Exception {

        int sensorId=1;
        int userId=1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(true);
        when(sensorServiceMock.restIsSentToOnLog(sensorId,userId)).thenReturn(false);
        doNothing().when(sensorServiceMock).removeMySensor(sensorId,userId);
        mockMvc.perform(delete("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isOk());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verify(sensorServiceMock,times(1)).restIsSentToOnLog(sensorId,userId);
        verify(sensorServiceMock,times(1)).removeMySensor(sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    @Test
    public void deleteSensorById_NotFoundSensor() throws Exception {

        int sensorId=1;
        int userId=1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(false);

        mockMvc.perform(delete("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isNoContent());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    @Test
    public void deleteSensorById_InvalidUser() throws Exception {

        int userId=-1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);

        mockMvc.perform(delete("/api/sensors/1").header("Authorization",dummyHeaderValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.delete.error.invalid.user")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    /*
    *
    *   Sensor update(post) test's
    *
    */

    @Test
    public void updateSensorById_ShouldUpdateSensorNameEquals() throws Exception {

        int sensorId=1;
        int userId=1;
        String sensorName="Sensor1";
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);


        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(true);
        when(sensorServiceMock.restFindIsSensorNameEquals(sensorName,sensorId,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(true);
        doNothing().when(sensorServiceMock).update(eq(sensorName),anyList(),eq(sensorId),eq(userId));

        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameEquals(sensorName,sensorId,userId);
        verify(sensorServiceMock,times(1)).update(eq(sensorName),anyList(),eq(sensorId),eq(userId));
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_ShouldUpdateSensorNameChanged() throws Exception {

        int sensorId=1;
        int userId=1;
        String sensorName="Sensor1";
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);


        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(true);
        when(sensorServiceMock.restFindIsSensorNameEquals(sensorName,sensorId,userId)).thenReturn(false);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(true);
        doNothing().when(sensorServiceMock).update(eq(sensorName),anyList(),eq(sensorId),eq(userId));

        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameEquals(sensorName,sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verify(sensorServiceMock,times(1)).update(eq(sensorName),anyList(),eq(sensorId),eq(userId));
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_SensorNameChangedAndItExist() throws Exception {

        int sensorId=1;
        int userId=1;
        String sensorName="Sensor1";
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);


        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(true);
        when(sensorServiceMock.restFindIsSensorNameEquals(sensorName,sensorId,userId)).thenReturn(false);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(false);

        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.update.error.sensor.name.exist")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameEquals(sensorName,sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_SensorNameChangedAndDataTypesIsInvalid() throws Exception {

        int sensorId=1;
        int userId=1;
        String sensorName="Sensor1";
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);


        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(true);
        when(sensorServiceMock.restFindIsSensorNameEquals(sensorName,sensorId,userId)).thenReturn(false);
        when(sensorServiceMock.restFindIsSensorNameExist(sensorName,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(false);
        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.update.error.invalid.datatype")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameEquals(sensorName,sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameExist(sensorName,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_SensorNameEquilsAndDataTypesIsInvalid() throws Exception {

        int sensorId=1;
        int userId=1;
        String sensorName="Sensor1";
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);


        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(true);
        when(sensorServiceMock.restFindIsSensorNameEquals(sensorName,sensorId,userId)).thenReturn(true);
        when(sensorDatatypeServiceMock.isDatatypesValid(anyList())).thenReturn(false);
        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.update.error.invalid.datatype")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verify(sensorServiceMock,times(1)).restFindIsSensorNameEquals(sensorName,sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).isDatatypesValid(anyList());
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }



    @Test
    public void updateSensorById_SensorIdNotFound() throws Exception {

        int sensorId=1;
        int userId=1;
        String sensorName="Sensor1";
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);


        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindExist(sensorId,userId)).thenReturn(false);
        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNoContent());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindExist(sensorId,userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_InvalidUser() throws Exception {

        int sensorId=1;
        int userId=-1;
        String sensorName="Sensor1";
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);


        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.update.error.invalid.user")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_ValidatationSensorNameIsNull() throws Exception {

        int sensorId=1;
        int userId=1;
        String sensorName=null;
        SensorForm sensorForm = sensorFormBuild(sensorName);
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.update.error.missing.value")));

        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_ValidatationSensorDataTypeNameIsNull() throws Exception{
        String sensorName="Sensor1";

        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name(null)
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.update.error.missing.value")));

        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void updateSensorById_ValidatationSensorDataTypeIsEmpty() throws Exception{
        String sensorName="Sensor1";

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(null)
                .build();
        byte[] json = TestUtil.convertObjectToJsonBytes(sensorForm);

        mockMvc.perform(post("/api/sensors/1").header("Authorization",dummyHeaderValue)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.update.error.missing.value")));

        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }

    /*
    *
    *   Sensor List test's
    *
    **/


    @Test
    public void getSensorsList_ShouldGetSensorsList() throws Exception{

        int userId=1;
        int sensorId1 = 1;
        String sensorName1 = "Sensor1";

        int sensorId2 = 2;
        String sensorName2 = "Sensor2";

        SensorEntity sensorEntityFirst = sensorEntityBuild(userId,sensorId1,sensorName1);
        SensorEntity sensorEntitySecond = sensorEntityBuild(userId,sensorId2,sensorName2);

        List<SensorEntity> found = Arrays.asList(sensorEntityFirst,sensorEntitySecond);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindAll(userId)).thenReturn(found);
        mockMvc.perform(get("/api/sensors/list").header("Authorization",dummyHeaderValue))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(20)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Sensor1")))
                .andExpect(jsonPath("$[0].lat", is(0.0)))
                .andExpect(jsonPath("$[0].lon", is(0.0)))
                .andExpect(jsonPath("$[0].sensorDatatypeEntities", hasSize(2)))
                .andExpect(jsonPath("$[0].sensorDatatypeEntities[*].id", containsInAnyOrder(1,2)))
                .andExpect(jsonPath("$[0].sensorDatatypeEntities[*].name", containsInAnyOrder("TMP","GPS")))
                .andExpect(jsonPath("$[0].sensorDatatypeEntities[*].labeltoken", containsInAnyOrder("sensor.type.checkbox.label.tmp","sensor.type.checkbox.label.gps")))
                .andExpect(jsonPath("$[0].apikey", is("Apikey")))
                .andExpect(jsonPath("$[0].userid", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)))
                .andExpect(jsonPath("$[0].onLogSend", is(false)))
                .andExpect(jsonPath("$[0].new", is(false)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Sensor2")))
                .andExpect(jsonPath("$[1].lat", is(0.0)))
                .andExpect(jsonPath("$[1].lon", is(0.0)))
                .andExpect(jsonPath("$[1].sensorDatatypeEntities", hasSize(2)))
                .andExpect(jsonPath("$[1].sensorDatatypeEntities[*].id", containsInAnyOrder(1,2)))
                .andExpect(jsonPath("$[1].sensorDatatypeEntities[*].name", containsInAnyOrder("TMP","GPS")))
                .andExpect(jsonPath("$[1].sensorDatatypeEntities[*].labeltoken", containsInAnyOrder("sensor.type.checkbox.label.tmp","sensor.type.checkbox.label.gps")))
                .andExpect(jsonPath("$[1].apikey", is("Apikey")))
                .andExpect(jsonPath("$[1].userid", is(1)))
                .andExpect(jsonPath("$[1].active", is(true)))
                .andExpect(jsonPath("$[1].onLogSend", is(false)))
                .andExpect(jsonPath("$[1].new", is(false)));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindAll(userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorsList_JsonParseException() throws Exception{

        int userId=1;
        int sensorId1 = 1;
        String sensorName1 = "Sensor1";

        int sensorId2 = 2;
        String sensorName2 = "Sensor2";

        SensorEntity sensorEntityFirst = sensorEntityBuild(userId,sensorId1,sensorName1);
        SensorEntity sensorEntitySecond = sensorEntityBuild(userId,sensorId2,sensorName2);

        List<SensorEntity> found = Arrays.asList(sensorEntityFirst,sensorEntitySecond);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindAll(userId)).thenReturn(found);

         /*Should be modified use to JsonProcessingException*/
        thrown.expect(MockitoException.class);
        ObjectMapper om = Mockito.mock(ObjectMapper.class);
        Mockito.when( om.writeValueAsString(found)).thenThrow(new Exception());

        mockMvc.perform(get("/api/sensors/list").header("Authorization",dummyHeaderValue))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.list.error.json.parsing")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindAll(userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorsList_ReturnListIsEmpty() throws Exception{

        int userId=1;

        List<SensorEntity> found = Arrays.asList();

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorServiceMock.restFindAll(userId)).thenReturn(found);

        mockMvc.perform(get("/api/sensors/list").header("Authorization",dummyHeaderValue))
                .andExpect(status().isNoContent());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verify(sensorServiceMock,times(1)).restFindAll(userId);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorsList_InvalidUser() throws Exception{

        int userId=-1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);

        mockMvc.perform(get("/api/sensors/list").header("Authorization",dummyHeaderValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.list.error.invalid.user")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorsDataTypeList_ShouldGetSensorsDataTypeList() throws Exception{

        int userId=1;

        SensorDatatypeEntity sensorDatatypeEntity1 = new SensorDataTypeDTOBuilder()
                .id(1)
                .name("TMP")
                .labeltoken("sensor.type.checkbox.label.tmp")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity2 = new SensorDataTypeDTOBuilder()
                .id(2)
                .name("GPS")
                .labeltoken("sensor.type.checkbox.label.gps")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity3 = new SensorDataTypeDTOBuilder()
                .id(3)
                .name("FLAG")
                .labeltoken("sensor.type.checkbox.label.flag")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity4 = new SensorDataTypeDTOBuilder()
                .id(4)
                .name("HUM")
                .labeltoken("sensor.type.checkbox.label.hum")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity5 = new SensorDataTypeDTOBuilder()
                .id(5)
                .name("LUX")
                .labeltoken("sensor.type.checkbox.label.lux")
                .build();

        List<SensorDatatypeEntity> found = Arrays.asList(sensorDatatypeEntity1,sensorDatatypeEntity2,sensorDatatypeEntity3,sensorDatatypeEntity4,sensorDatatypeEntity5);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorDatatypeServiceMock.findAll()).thenReturn(found);

        mockMvc.perform(get("/api/sensors/datatypelist").header("Authorization",dummyHeaderValue))
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

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).findAll();
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorsDataTypeList_JsonParseException() throws Exception{

        int userId=1;

        SensorDatatypeEntity sensorDatatypeEntity1 = new SensorDataTypeDTOBuilder()
                .id(1)
                .name("TMP")
                .labeltoken("sensor.type.checkbox.label.tmp")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity2 = new SensorDataTypeDTOBuilder()
                .id(2)
                .name("GPS")
                .labeltoken("sensor.type.checkbox.label.gps")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity3 = new SensorDataTypeDTOBuilder()
                .id(3)
                .name("FLAG")
                .labeltoken("sensor.type.checkbox.label.flag")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity4 = new SensorDataTypeDTOBuilder()
                .id(4)
                .name("HUM")
                .labeltoken("sensor.type.checkbox.label.hum")
                .build();
        SensorDatatypeEntity sensorDatatypeEntity5 = new SensorDataTypeDTOBuilder()
                .id(5)
                .name("LUX")
                .labeltoken("sensor.type.checkbox.label.lux")
                .build();

        List<SensorDatatypeEntity> found = Arrays.asList(sensorDatatypeEntity1,sensorDatatypeEntity2,sensorDatatypeEntity3,sensorDatatypeEntity4,sensorDatatypeEntity5);

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorDatatypeServiceMock.findAll()).thenReturn(found);


                 /*Should be modified use to JsonProcessingException*/
        thrown.expect(MockitoException.class);
        ObjectMapper om = Mockito.mock(ObjectMapper.class);
        Mockito.when( om.writeValueAsString(found)).thenThrow(new Exception());

        mockMvc.perform(get("/api/sensors/datatypelist").header("Authorization",dummyHeaderValue))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.datatypelist.error.json.parsing")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).findAll();
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorsDataTypeList_SensorsDataTypeListisEmpty() throws Exception{

        int userId=1;

        List<SensorDatatypeEntity> found = Arrays.asList();

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);
        when(sensorDatatypeServiceMock.findAll()).thenReturn(found);

        mockMvc.perform(get("/api/sensors/datatypelist").header("Authorization",dummyHeaderValue))
                .andExpect(status().isNoContent());

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verify(sensorDatatypeServiceMock,times(1)).findAll();
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }


    @Test
    public void getSensorsDataTypeList_InvalidUser() throws Exception{

        int userId=-1;

        when(restServiceMock.getUserInfo(dummyHeaderValue)).thenReturn(userId);

        mockMvc.perform(get("/api/sensors/datatypelist").header("Authorization",dummyHeaderValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error", is("sensor.datatypelist.error.invalid.user")));

        verify(restServiceMock, times(1)).getUserInfo(dummyHeaderValue);
        verifyNoMoreInteractions(restServiceMock);
        verifyNoMoreInteractions(sensorServiceMock);
        verifyNoMoreInteractions(sensorDatatypeServiceMock);
        verifyNoMoreInteractions(jmsServiceMock);
    }



    private SensorForm sensorFormBuild(String sensorName){
        SensorDataTypeForm firstDataType = new SensorDataTypeFormDTOBuilder()
                .name("TMP")
                .build();
        SensorDataTypeForm secondDataType = new SensorDataTypeFormDTOBuilder()
                .name("GPS")
                .build();
        List<SensorDataTypeForm> sensorDataTypeFormsDTO = Arrays.asList(firstDataType,secondDataType);

        SensorForm sensorForm = new SensorFormDTOBuilder()
                .name(sensorName)
                .sensorFormDataTypesDTO(sensorDataTypeFormsDTO)
                .build();
        return sensorForm;
    }

    private SensorEntity sensorEntityBuild(int userId, int sensorId, String sensorName){
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
                .id(sensorId)
                .name(sensorName)
                .lat(0)
                .lon(0)
                .sensorDataTypeDTOs(sensorDataTypeDTOs)
                .apikey("Apikey")
                .userid(userId)
                .active(true)
                .onLogSend(false)
                .build();
        return found;
    }


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
