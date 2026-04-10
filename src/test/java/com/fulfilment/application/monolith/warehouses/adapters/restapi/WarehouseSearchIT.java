package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import java.time.LocalDateTime;

@QuarkusTest
public class WarehouseSearchIT {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    public void setup() {
        em.createQuery("DELETE FROM DbWarehouse").executeUpdate();
        
        createDbWarehouse("SEARCH-001", "AMSTERDAM-001", 100, 50);
        createDbWarehouse("SEARCH-002", "AMSTERDAM-001", 200, 100);
        createDbWarehouse("SEARCH-003", "ZWOLLE-001", 150, 75);
    }

    private void createDbWarehouse(String buCode, String location, int capacity, int stock) {
        DbWarehouse w = new DbWarehouse();
        w.businessUnitCode = buCode;
        w.location = location;
        w.capacity = capacity;
        w.stock = stock;
        w.createdAt = LocalDateTime.now();
        em.persist(w);
    }

    @Test
    public void testSearchByLocation() {
        given()
            .queryParam("location", "AMSTERDAM-001")
            .when().get("/warehouse/search")
            .then()
            .statusCode(200)
            .body("$", hasSize(2));
    }

    @Test
    public void testSearchByCapacity() {
        given()
            .queryParam("minCapacity", 150)
            .when().get("/warehouse/search")
            .then()
            .statusCode(200)
            .body("$", hasSize(2));
    }

    @Test
    public void testSearchPagination() {
        given()
            .queryParam("pageSize", 1)
            .when().get("/warehouse/search")
            .then()
            .statusCode(200)
            .body("$", hasSize(1));
    }
}
