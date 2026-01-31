package com.ricardo.takehome.devices.service;

import com.ricardo.takehome.devices.dto.CreateDeviceRequest;
import com.ricardo.takehome.devices.dto.UpdateDeviceRequest;
import com.ricardo.takehome.devices.model.Device;
import com.ricardo.takehome.devices.model.DeviceState;
import com.ricardo.takehome.devices.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {
    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
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
    void shouldSaveDevice() {
        CreateDeviceRequest request = new CreateDeviceRequest("Test Device", "Test Brand", DeviceState.AVAILABLE);
        when(deviceRepository.save(any(Device.class))).thenReturn(sampleDevice);

        Device result = deviceService.create(request);

        assertThat(result.getName()).isEqualTo("Test Device");
        assertThat(result.getBrand()).isEqualTo("Test Brand");
        assertThat(result.getState()).isEqualTo(DeviceState.AVAILABLE);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void shouldReturnDeviceWhenFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(sampleDevice));

        Device result = deviceService.getById(1L);

        assertThat(result).isEqualTo(sampleDevice);
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not Found");
    }

    @Test
    void shouldReturnAllDevices() {
        when(deviceRepository.findAll()).thenReturn(List.of(sampleDevice));

        List<Device> result = deviceService.getAll();

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnDevicesByBrand() {
        when(deviceRepository.getDeviceByBrand("Test Brand")).thenReturn(List.of(sampleDevice));

        List<Device> result = deviceService.getByBrand("Test Brand");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Test Brand");
    }

    @Test
    void shouldReturnDevicesByState() {
        when(deviceRepository.getDeviceByState(DeviceState.AVAILABLE)).thenReturn(List.of(sampleDevice));

        List<Device> result = deviceService.getByState(DeviceState.AVAILABLE);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getState()).isEqualTo(DeviceState.AVAILABLE);
    }

    @Test
    void shouldUpdateWhenNotInUse() {
        sampleDevice.setState(DeviceState.AVAILABLE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(sampleDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(sampleDevice);

        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", "New Brand", DeviceState.INACTIVE);
        Device result = deviceService.update(1L, request);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getBrand()).isEqualTo("New Brand");
        assertThat(result.getState()).isEqualTo(DeviceState.INACTIVE);
    }

    @Test
    void shouldAllowStateUpdateWhenInUse() {
        sampleDevice.setState(DeviceState.IN_USE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(sampleDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(sampleDevice);

        UpdateDeviceRequest request = new UpdateDeviceRequest(null, null, DeviceState.AVAILABLE);
        Device result = deviceService.update(1L, request);

        assertThat(result.getState()).isEqualTo(DeviceState.AVAILABLE);
    }

    @Test
    void shouldThrowWhenUpdatingNameOfInUseDevice() {
        sampleDevice.setState(DeviceState.IN_USE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(sampleDevice));

        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null, null);

        assertThatThrownBy(() -> deviceService.update(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("in use");
    }

    @Test
    void shouldThrowWhenUpdatingBrandOfInUseDevice() {
        sampleDevice.setState(DeviceState.IN_USE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(sampleDevice));

        UpdateDeviceRequest request = new UpdateDeviceRequest(null, "New Brand", null);

        assertThatThrownBy(() -> deviceService.update(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("in use");
    }

    @Test
    void shouldDeleteWhenNotInUse() {
        sampleDevice.setState(DeviceState.AVAILABLE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(sampleDevice));

        deviceService.delete(1L);

        verify(deviceRepository).deleteById(sampleDevice.getId());
    }

    @Test
    void shouldThrowWhenDeletingInUseDevice() {
        sampleDevice.setState(DeviceState.IN_USE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(sampleDevice));

        assertThatThrownBy(() -> deviceService.delete(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("in use");

        verify(deviceRepository, never()).delete(any());
    }
}