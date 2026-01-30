package com.ricardo.takehome.devices.dto;

import com.ricardo.takehome.devices.model.DeviceState;

public record UpdateDeviceRequest(
    String name,
    String brand,
    DeviceState state
) {}
