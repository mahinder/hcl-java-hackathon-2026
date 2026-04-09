package com.fulfilment.application.monolith.warehouses.adapters;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigInteger;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WarehouseResourceTest {

  private static final String TEST_BU_CODE = "REST-TEST-001";

  @Test
  @Order(1)
  public void testCreateWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.setBusinessUnitCode(TEST_BU_CODE);
    warehouse.setLocation("AMSTERDAM-001");
    warehouse.setCapacity(100);
    warehouse.setStock(50);

    given()
        .contentType("application/json")
        .body(warehouse)
        .when().post("/warehouse")
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo(TEST_BU_CODE));
  }

  @Test
  @Order(2)
  public void testGetWarehouse() {
    given()
        .when().get("/warehouse/" + TEST_BU_CODE)
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo(TEST_BU_CODE))
        .body("location", equalTo("AMSTERDAM-001"));
  }

  @Test
  @Order(3)
  public void testListAllWarehouses() {
    given()
        .when().get("/warehouse")
        .then()
        .statusCode(200)
        .body("[0]", notNullValue());
  }

  @Test
  @Order(4)
  public void testReplaceWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.setLocation("ZWOLLE-001");
    warehouse.setCapacity(40);
    warehouse.setStock(20);

    given()
        .contentType("application/json")
        .body(warehouse)
        .when().post("/warehouse/" + TEST_BU_CODE + "/replacement")
        .then()
        .statusCode(200)
        .body("location", equalTo("ZWOLLE-001"))
        .body("capacity", equalTo(40));
  }

  @Test
  @Order(5)
  public void testArchiveWarehouse() {
    given()
        .when().delete("/warehouse/" + TEST_BU_CODE)
        .then()
        .statusCode(204);
  }

  @Test
  @Order(6)
  public void testGetNonExistentWarehouse() {
    given()
        .when().get("/warehouse/NON-EXISTENT")
        .then()
        .statusCode(404);
  }

  @Test
  @Order(7)
  public void testCreateWarehouseValidationError() {
    Warehouse warehouse = new Warehouse();
    warehouse.setBusinessUnitCode("INVALID-TEST");
    warehouse.setLocation("NON-EXISTENT-LOCATION");
    warehouse.setCapacity(100);
    warehouse.setStock(50);

    given()
        .contentType("application/json")
        .body(warehouse)
        .when().post("/warehouse")
        .then()
        .statusCode(400);
  }
}
