package com.ricardo.takehome.devices.controller;

import com.ricardo.takehome.devices.dto.CreateDeviceRequest;
import com.ricardo.takehome.devices.dto.DeviceResponse;
import com.ricardo.takehome.devices.dto.ErrorResponse;
import com.ricardo.takehome.devices.dto.UpdateDeviceRequest;
import com.ricardo.takehome.devices.model.Device;
import com.ricardo.takehome.devices.model.DeviceState;
import com.ricardo.takehome.devices.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceResponse create(@Valid @RequestBody CreateDeviceRequest request) {
        Device device = deviceService.create(request);
        return toResponse(device);
    }

    @GetMapping("/{id}")
    public DeviceResponse getById(@PathVariable Long id) {
        Device device = deviceService.getById(id);
        return toResponse(device);
    }

    @GetMapping
    public List<DeviceResponse> getAll(@RequestParam(required = false) String brand, @RequestParam(required = false) DeviceState state) {
        List<Device> devices;
        if (brand != null) {
            devices = deviceService.getByBrand(brand);
        } else if (state != null) {
            devices = deviceService.getByState(state);
        } else {
            devices = deviceService.getAll();
        }
        return devices.stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}")
    public DeviceResponse update(
            @PathVariable Long id,
            @RequestBody UpdateDeviceRequest request) {
        Device device = deviceService.update(id, request);
        return toResponse(device);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deviceService.delete(id);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse error = new ErrorResponse("CONFLICT", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse("BAD_REQUEST", message);
        return ResponseEntity.badRequest().body(error);
    }


    private DeviceResponse toResponse(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getName(),
                device.getBrand(),
                device.getState(),
                device.getCreationTime()
        );
    }
}
