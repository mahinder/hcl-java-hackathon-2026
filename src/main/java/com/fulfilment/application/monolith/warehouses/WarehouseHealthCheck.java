package com.fulfilment.application.monolith.warehouses;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class WarehouseHealthCheck implements HealthCheck {

  @Inject
  WarehouseRepository warehouseRepository;

  @Override
  public HealthCheckResponse call() {
    long count = warehouseRepository.count();
    return HealthCheckResponse.named("Warehouse Database Connection Check")
        .up()
        .withData("warehouseCount", count)
        .build();
  }
}
