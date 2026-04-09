package com.fulfilment.application.monolith.warehouses.adapters;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseSearchTest {

  @Test
  public void testSearchByLocation() {
    given()
        .queryParam("location", "AMSTERDAM-001")
        .when().get("/warehouse/search")
        .then()
        .statusCode(200)
        .body("$", not(empty()))
        .body("[0].location", is("AMSTERDAM-001"));
  }

  @Test
  public void testSearchByCapacityRange() {
    given()
        .queryParam("minCapacity", 40)
        .queryParam("maxCapacity", 60)
        .when().get("/warehouse/search")
        .then()
        .statusCode(200)
        .body("$", not(empty()))
        .body("every { it.capacity >= 40 && it.capacity <= 60 }", is(true));
  }

  @Test
  public void testSearchExcludesArchived() {
    // We know MWH.001 is in import.sql and not archived.
    // Let's archive it first (using ID from import.sql is risky, but for this test we'll assume state)
    
    // 1. Verify it's there
    given()
        .queryParam("location", "ZWOLLE-001")
        .when().get("/warehouse/search")
        .then()
        .statusCode(200)
        .body("businessUnitCode[0]", is("MWH.001"));

    // 2. Archive it
    given()
        .when().delete("/warehouse/MWH.001")
        .then()
        .statusCode(204); 

    // 3. Verify it's gone from search
    given()
        .queryParam("location", "ZWOLLE-001")
        .when().get("/warehouse/search")
        .then()
        .statusCode(200)
        .body("$", is(empty()));
  }
}
