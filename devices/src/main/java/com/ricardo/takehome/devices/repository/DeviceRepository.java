package com.ricardo.takehome.devices.repository;

import com.ricardo.takehome.devices.model.Device;
import com.ricardo.takehome.devices.model.DeviceState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device,Long> {

  List<Device> getDeviceByBrand(String brand);

  List<Device> getDeviceByState(DeviceState state);
}
