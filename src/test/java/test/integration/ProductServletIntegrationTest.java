package test.integration;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServletIntegrationTest extends BaseIntegrationTest {

    private static Long createdProductId;

    @Test
    @Order(1)
    void createProduct_ShouldCreateNewProduct() throws Exception {
        String jsonBody = """
                {
                    "name": "Integration Test Product",
                    "category": "Electronics",
                    "brand": "TestBrand",
                    "price": 99.99,
                    "description": "A product for integration testing"
                }
                """;

        HttpResponse response = post("/api/products", jsonBody, "1", "testuser", "USER");

        assertEquals(201, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("Product created successfully"));
        assertTrue(response.containsText("Integration Test Product"));
    }

    @Test
    @Order(2)
    void getAllProducts_ShouldReturnProducts() throws Exception {
        HttpResponse response = get("/api/products/", "1", "testuser", "USER");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("\"products\""));
    }

    @Test
    @Order(3)
    void getProductById_ShouldReturnProduct() throws Exception {
        HttpResponse response = get("/api/products/1", "1", "testuser", "USER");

        if (response.statusCode() == 200) {
            assertTrue(response.containsText("\"success\":true"));
            assertTrue(response.containsText("\"product\""));
        } else {
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    void createProduct_ShouldReturnBadRequest_WhenMissingName() throws Exception {
        String jsonBody = """
                {
                    "category": "Electronics",
                    "brand": "TestBrand",
                    "price": 99.99
                }
                """;

        HttpResponse response = post("/api/products", jsonBody, "1", "testuser", "USER");

        assertEquals(400, response.statusCode());
        assertTrue(response.containsText("Product name is required"));
    }

    @Test
    void getProductCount_ShouldReturnCount() throws Exception {
        HttpResponse response = get("/api/products/count", "1", "testuser", "USER");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("\"totalCount\""));
    }

    @Test
    void getProduct_ShouldReturnNotFound_WhenInvalidPath() throws Exception {
        HttpResponse response = get("/api/products/invalid", "1", "testuser", "USER");

        assertEquals(404, response.statusCode());
    }
}
