package com.ricardo.takehome.devices.dto;

import com.ricardo.takehome.devices.model.DeviceState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDeviceRequest(
    @NotBlank String name,
    @NotBlank String brand,
    @NotNull DeviceState state
) {}
