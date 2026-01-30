package com.ricardo.takehome.devices.dto;

import com.ricardo.takehome.devices.model.DeviceState;
import java.time.Instant;

public record DeviceResponse(
    Long id, String name, String brand,
    DeviceState state, Instant creationTime
) {}
