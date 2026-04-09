package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreResourceTest {

  private static Long storeId;

  @Test
  @Order(1)
  public void testCreateStore() {
    String storeName = "TEST-STORE-" + System.currentTimeMillis();
    String response = given()
        .contentType("application/json")
        .body("{\"name\": \"" + storeName + "\", \"quantityProductsInStock\": 10}")
        .when().post("/store")
        .then()
        .statusCode(201)
        .body("name", equalTo(storeName))
        .extract().path("id").toString();
    
    storeId = Long.parseLong(response);
  }

  @Test
  @Order(2)
  public void testGetStore() {
    given()
        .when().get("/store/" + storeId)
        .then()
        .statusCode(200)
        .body("id", equalTo(storeId.intValue()));
  }

  @Test
  @Order(3)
  public void testListStores() {
    given()
        .when().get("/store")
        .then()
        .statusCode(200)
        .body("[0]", notNullValue());
  }

  @Test
  @Order(4)
  public void testUpdateStore() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"UPDATED-STORE\", \"quantityProductsInStock\": 15}")
        .when().put("/store/" + storeId)
        .then()
        .statusCode(200)
        .body("name", equalTo("UPDATED-STORE"))
        .body("quantityProductsInStock", equalTo(15));
  }

  @Test
  @Order(5)
  public void testPatchStore() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"PATCHED-STORE\", \"quantityProductsInStock\": 25}")
        .when().patch("/store/" + storeId)
        .then()
        .statusCode(200)
        .body("name", equalTo("PATCHED-STORE"))
        .body("quantityProductsInStock", equalTo(25));
  }

  @Test
  @Order(6)
  public void testDeleteStore() {
    given()
        .when().delete("/store/" + storeId)
        .then()
        .statusCode(204);
  }

  @Test
  @Order(7)
  public void testNotFound() {
    given()
        .when().get("/store/999999")
        .then()
        .statusCode(404);
  }
}
