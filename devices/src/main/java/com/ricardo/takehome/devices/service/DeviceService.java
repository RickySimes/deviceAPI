package com.ricardo.takehome.devices.service;

import com.ricardo.takehome.devices.dto.CreateDeviceRequest;
import com.ricardo.takehome.devices.dto.UpdateDeviceRequest;
import com.ricardo.takehome.devices.model.Device;
import com.ricardo.takehome.devices.model.DeviceState;
import com.ricardo.takehome.devices.repository.DeviceRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {
  private final DeviceRepository deviceRepository;

  public Device create(CreateDeviceRequest request){
    Device device = new Device();
    device.setName(request.name());
    device.setBrand(request.brand());
    device.setState(request.state());
    device.setCreationTime(Instant.now());
    return deviceRepository.save(device);
  }

  @Transactional(readOnly = true)
  public Device getById(Long id) {
    return deviceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Device not Found"));
  }

  @Transactional(readOnly = true)
  public List<Device> getAll() {
    return deviceRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Device> getByBrand(String brand) {
    return deviceRepository.getDeviceByBrand(brand);
  }

  @Transactional(readOnly = true)
  public List<Device> getByState(DeviceState state) {
    return deviceRepository.getDeviceByState(state);
  }

  public Device update(Long id, UpdateDeviceRequest request) {
    Device device = getById(id);

    if (!device.canUpdateNameAndBrand()) {
      boolean nameChanging = request.name() != null && !device.getName().equals(request.name());
      boolean brandChanging = request.brand() != null && !device.getBrand().equals(request.brand());

      if (nameChanging || brandChanging) {
        throw new IllegalStateException("Cannot update name or brand of a device that is in use");
      }
    }

    if (request.name() != null){
      device.setName(request.name());
    }
    if (request.brand() != null){
      device.setBrand(request.brand());
    }
    if (request.state() != null){
      device.setState(request.state());
    }

    return deviceRepository.save(device);
  }

  public void delete(Long id){
    Device device = getById(id);
    if (device.getState() == DeviceState.IN_USE) {
      throw new IllegalStateException("Cannot delete a device that is in use");
    }
    deviceRepository.deleteById(id);
  }
}
