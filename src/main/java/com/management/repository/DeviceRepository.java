package com.management.repository;

import com.management.model.Device;
import com.management.model.User;
import com.management.register.enums.DeviceStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    Optional<Device> findFirstByUserAndDeviceCode(User user, String deviceCode);

    Optional<Device> findFirstByUserAndDeviceNo(User user, String deviceNo);

    List<Device> findDevicesByUser(User user);

    Optional<Device> findDeviceByIdAndUserAndDeviceStatus(UUID id, User user, DeviceStatus deviceStatus);

    List<Device> findDevicesByUserAndDeviceStatus(User user, DeviceStatus deviceStatus);

    List<Device> findDevicesByUserAndDeviceStatusAndLastLoginDateIsNotNull(User user, DeviceStatus deviceStatus);

    Optional<Device> findById(String deviceId);

}
