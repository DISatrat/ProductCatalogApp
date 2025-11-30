package test.main.servlet;

import controller.ProductController;
import dto.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import servlets.ProductServlet;
import servlets.test.TestableProductServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ProductController productController;

    private TestableProductServlet productServlet;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        productServlet = new TestableProductServlet();

        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        injectController();
    }

    @Test
    void doGet_ShouldReturnAllProducts_WhenRootPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/");

        List<ProductResponseDTO> mockProducts = Arrays.asList(
                ProductResponseDTO.builder().id(1L).name("Product1").build(),
                ProductResponseDTO.builder().id(2L).name("Product2").build()
        );
        when(productController.getAllProducts()).thenReturn(mockProducts);

        productServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("Product1"));
        assertTrue(responseContent.contains("Product2"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldReturnProductById_WhenValidId() throws Exception {
        when(request.getPathInfo()).thenReturn("/123");

        ProductResponseDTO mockProduct = ProductResponseDTO.builder()
                .id(123L)
                .name("Test Product")
                .category("Electronics")
                .brand("Test Brand")
                .price(99.99)
                .description("Test description")
                .userId(1L)
                .build();
        when(productController.getProductById(123L)).thenReturn(Optional.of(mockProduct));

        productServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("Test Product"));
        verify(productController).getProductById(123L);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldHandleSearch_WhenSearchPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/search");
        when(request.getParameter("query")).thenReturn("laptop");

        List<ProductResponseDTO> mockProducts = Arrays.asList(
                ProductResponseDTO.builder().id(1L).name("Gaming Laptop").build()
        );
        when(productController.searchProducts("laptop",null,null,null,1.0,10.0)).thenReturn(mockProducts);

        productServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("Gaming Laptop"));
        verify(productController).searchProducts("laptop",null,null,null,1.0,10.0);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    private void injectController() {
        try {
            var controllerField = ProductServlet.class.getDeclaredField("productController");
            controllerField.setAccessible(true);
            controllerField.set(productServlet, productController);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject controller", e);
        }
    }
}