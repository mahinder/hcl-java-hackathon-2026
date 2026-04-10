package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  private static final org.jboss.logging.Logger LOG = org.jboss.logging.Logger.getLogger(WarehouseRepository.class);

  @Override
  public List<Warehouse> getAll() {
    LOG.info("Getting all non-archived warehouses");
    return this.find("archivedAt IS NULL").list().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    LOG.infof("Creating warehouse in DB: %s", warehouse.businessUnitCode);
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;
    
    this.persist(dbWarehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    LOG.infof("Updating warehouse in DB: %s", warehouse.businessUnitCode);
    DbWarehouse dbWarehouse = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    
    if (dbWarehouse != null) {
      dbWarehouse.location = warehouse.location;
      dbWarehouse.capacity = warehouse.capacity;
      dbWarehouse.stock = warehouse.stock;
      dbWarehouse.archivedAt = warehouse.archivedAt;
      
      this.persist(dbWarehouse);
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    LOG.infof("Removing warehouse from DB: %s", warehouse.businessUnitCode);
    DbWarehouse dbWarehouse = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (dbWarehouse != null) {
      this.delete(dbWarehouse);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = find("businessUnitCode", buCode).firstResult();
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }

  @Override
  public List<Warehouse> findFiltered(
      String location,
      Integer minCapacity,
      Integer maxCapacity,
      String sortBy,
      String sortOrder,
      int page,
      int pageSize) {
    StringBuilder query = new StringBuilder("archivedAt IS NULL");
    Map<String, Object> params = new HashMap<>();

    if (location != null && !location.isBlank()) {
      query.append(" AND location = :location");
      params.put("location", location);
    }
    if (minCapacity != null) {
      query.append(" AND capacity >= :minCap");
      params.put("minCap", minCapacity);
    }
    if (maxCapacity != null) {
      query.append(" AND capacity <= :maxCap");
      params.put("maxCap", maxCapacity);
    }

    String sortField = "createdAt";
    if ("capacity".equalsIgnoreCase(sortBy)) {
      sortField = "capacity";
    }

    Sort sort =
        "desc".equalsIgnoreCase(sortOrder) ? Sort.descending(sortField) : Sort.ascending(sortField);

    return find(query.toString(), sort, params)
        .page(Page.of(page, pageSize))
        .list()
        .stream()
        .map(DbWarehouse::toWarehouse)
        .toList();
  }
}
