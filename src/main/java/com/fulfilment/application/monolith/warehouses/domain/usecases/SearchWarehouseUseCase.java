package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.SearchWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SearchWarehouseUseCase implements SearchWarehouseOperation {

  private static final Logger LOG = Logger.getLogger(SearchWarehouseUseCase.class);

  @Inject WarehouseStore warehouseStore;

  @Override
  public List<Warehouse> search(
      String location,
      Integer minCapacity,
      Integer maxCapacity,
      String sortBy,
      String sortOrder,
      int page,
      int pageSize) {
    
    LOG.infof("Searching warehouses with filters: location=%s, minCapacity=%d, maxCapacity=%d, sortBy=%s, sortOrder=%s, page=%d, pageSize=%d",
        location, minCapacity, maxCapacity, sortBy, sortOrder, page, pageSize);

    return warehouseStore.findFiltered(
        location, minCapacity, maxCapacity, sortBy, sortOrder, page, pageSize);
  }
}
