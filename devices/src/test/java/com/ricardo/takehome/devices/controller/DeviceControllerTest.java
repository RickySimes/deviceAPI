package com.ricardo.takehome.devices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricardo.takehome.devices.dto.CreateDeviceRequest;
import com.ricardo.takehome.devices.dto.UpdateDeviceRequest;
import com.ricardo.takehome.devices.model.Device;
import com.ricardo.takehome.devices.model.DeviceState;
import com.ricardo.takehome.devices.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DeviceController.class)
class DeviceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeviceService deviceService;

    private Device sampleDevice;

    @BeforeEach
    void setUp() {
        sampleDevice = new Device();
        sampleDevice.setId(1L);
        sampleDevice.setName("Test Device");
        sampleDevice.setBrand("Test Brand");
        sampleDevice.setState(DeviceState.AVAILABLE);
        sampleDevice.setCreationTime(Instant.now());
    }

    @Test
    void shouldCreateDevice() throws Exception {
        CreateDeviceRequest request = new CreateDeviceRequest("Test Device", "Test Brand", DeviceState.AVAILABLE);
        when(deviceService.create(any(CreateDeviceRequest.class))).thenReturn(sampleDevice);

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Device"))
                .andExpect(jsonPath("$.brand").value("Test Brand"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {
        CreateDeviceRequest request = new CreateDeviceRequest("", "", null);

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/devices/{id} - Should return device")
    void shouldReturnDevice() throws Exception {
        when(deviceService.getById(1L)).thenReturn(sampleDevice);

        mockMvc.perform(get("/api/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Device"));
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(deviceService.getById(99L)).thenThrow(new RuntimeException("Device not found"));

        mockMvc.perform(get("/api/devices/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllDevices() throws Exception {
        when(deviceService.getAll()).thenReturn(List.of(sampleDevice));

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldFilterByBrand() throws Exception {
        when(deviceService.getByBrand("Test Brand")).thenReturn(List.of(sampleDevice));

        mockMvc.perform(get("/api/devices").param("brand", "Test Brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Test Brand"));
    }

    @Test
    @DisplayName("GET /api/devices?state=AVAILABLE - Should filter by state")
    void shouldFilterByState() throws Exception {
        when(deviceService.getByState(DeviceState.AVAILABLE)).thenReturn(List.of(sampleDevice));

        mockMvc.perform(get("/api/devices").param("state", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("AVAILABLE"));
    }

    @Test
    void shouldUpdateDevice() throws Exception {
        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null, null);
        sampleDevice.setName("New Name");
        when(deviceService.update(eq(1L), any(UpdateDeviceRequest.class))).thenReturn(sampleDevice);

        mockMvc.perform(patch("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void shouldReturn409WhenDeviceInUse() throws Exception {
        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null, null);
        when(deviceService.update(eq(1L), any(UpdateDeviceRequest.class)))
                .thenThrow(new IllegalStateException("Cannot update name or brand of a device that is in use"));

        mockMvc.perform(patch("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldDeleteDevice() throws Exception {
        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn409WhenDeletingInUseDevice() throws Exception {
        doThrow(new IllegalStateException("Cannot delete a device that is in use"))
                .when(deviceService).delete(1L);

        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isConflict());
    }

}