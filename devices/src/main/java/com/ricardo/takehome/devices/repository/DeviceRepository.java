package com.ricardo.takehome.devices.repository;

import com.ricardo.takehome.devices.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device,Long> {

}
