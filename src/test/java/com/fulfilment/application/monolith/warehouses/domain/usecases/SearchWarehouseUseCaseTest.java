package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class SearchWarehouseUseCaseTest {

  @Inject SearchWarehouseUseCase searchWarehouseUseCase;

  @InjectMock WarehouseStore warehouseStore;

  @Test
  public void testSearchWarehouses() {
    // Arrange
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "BU001";
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 100;
    warehouse.stock = 50;
    
    List<Warehouse> expectedList = Collections.singletonList(warehouse);
    
    when(warehouseStore.findFiltered(
        eq("AMSTERDAM-001"), eq(50), eq(150), eq("capacity"), eq("desc"), eq(0), eq(10)))
        .thenReturn(expectedList);

    // Act
    List<Warehouse> result = searchWarehouseUseCase.search(
        "AMSTERDAM-001", 50, 150, "capacity", "desc", 0, 10);

    // Assert
    assertEquals(1, result.size());
    assertEquals("BU001", result.get(0).businessUnitCode);
    verify(warehouseStore, times(1)).findFiltered(any(), any(), any(), any(), any(), anyInt(), anyInt());
  }
}
