package com.fulfilment.application.monolith.products;

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
public class ProductResourceTest {

  private static Long productId;

  @Test
  @Order(1)
  public void testCreateProduct() {
    String productName = "TEST-PRODUCT-" + System.currentTimeMillis();
    String response = given()
        .contentType("application/json")
        .body("{\"name\": \"" + productName + "\", \"stock\": 10}")
        .when().post("/product")
        .then()
        .statusCode(201)
        .body("name", equalTo(productName))
        .extract().path("id").toString();
    
    productId = Long.parseLong(response);
  }

  @Test
  @Order(2)
  public void testGetProduct() {
    given()
        .when().get("/product/" + productId)
        .then()
        .statusCode(200)
        .body("id", equalTo(productId.intValue()));
  }

  @Test
  @Order(3)
  public void testListProducts() {
    given()
        .when().get("/product")
        .then()
        .statusCode(200)
        .body("[0]", notNullValue());
  }

  @Test
  @Order(4)
  public void testUpdateProduct() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"UPDATED-PRODUCT\", \"stock\": 15}")
        .when().put("/product/" + productId)
        .then()
        .statusCode(200)
        .body("name", equalTo("UPDATED-PRODUCT"))
        .body("stock", equalTo(15));
  }



  @Test
  @Order(6)
  public void testDeleteProduct() {
    given()
        .when().delete("/product/" + productId)
        .then()
        .statusCode(204);
  }

  @Test
  @Order(7)
  public void testNotFound() {
    given()
        .when().get("/product/999999")
        .then()
        .statusCode(404);
  }
}
