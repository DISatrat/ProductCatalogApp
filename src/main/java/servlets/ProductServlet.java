package servlets;

import config.BaseServlet;
import controller.ProductController;
import dto.ProductRequestDTO;
import dto.ProductResponseDTO;
import dto.ProductSearchDTO;
import dto.ProductUpdateDTO;
import mapper.ProductMapper;
import util.ApplicationContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/products/*")
public class ProductServlet extends BaseServlet {
    private final ProductController productController;
    private final ProductMapper productMapper;

    public ProductServlet() {
        this.productController = new ProductController(
                ApplicationContext.getProductService()
        );
        this.productMapper = ProductMapper.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/products
                handleGetAllProducts(request, response);
            } else if (pathInfo.matches("/\\d+")) {
                // GET /api/products/{id}
                handleGetProductById(request, response);
            } else if (pathInfo.equals("/search")) {
                // GET /api/products/search
                handleSearchProducts(request, response);
            } else if (pathInfo.equals("/count")) {
                // GET /api/products/count
                handleGetTotalCount(request, response);
            } else {
                sendError(response, "Not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                handleCreateProduct(request, response);
            } else {
                sendError(response, "Not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                handleUpdateProduct(request, response);
            } else {
                sendError(response, "Product ID required", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                handleDeleteProduct(request, response);
            } else {
                sendError(response, "Product ID required", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetAllProducts(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<ProductResponseDTO> products = productController.getAllProducts();

        Map<String, Object> result = Map.of(
                "success", true,
                "products", products,
                "count", products.size()
        );

        sendJsonResponse(response, result, HttpServletResponse.SC_OK);
    }

    private void handleGetProductById(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long productId = extractIdFromPath(request);

        try {
            Optional<ProductResponseDTO> product = productController.getProductById(productId);

            if (product.isPresent()) {

                Map<String, Object> result = Map.of(
                        "success", true,
                        "product", product.get()
                );
                sendJsonResponse(response, result, HttpServletResponse.SC_OK);
            } else {
                sendError(response, "Product not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleSearchProducts(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = getUsernameFromRequest(request);
        ProductSearchDTO searchDTO = parseJsonBody(request, ProductSearchDTO.class);

        try {
            List<ProductResponseDTO> products = productController.searchProducts(
                    username,
                    searchDTO.getNameSubstr(),
                    searchDTO.getCategory(),
                    searchDTO.getBrand(),
                    searchDTO.getPriceMin(),
                    searchDTO.getPriceMax()
            );


            Map<String, Object> result = Map.of(
                    "success", true,
                    "products", products,
                    "count", products.size()
            );
            sendJsonResponse(response, result, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendError(response, "Search failed: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetTotalCount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int count = productController.getTotalProductsCount();

        Map<String, Object> result = Map.of(
                "success", true,
                "totalCount", count
        );
        sendJsonResponse(response, result, HttpServletResponse.SC_OK);
    }

    private void handleCreateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = getUsernameFromRequest(request);
        ProductRequestDTO productDTO = parseJsonBody(request, ProductRequestDTO.class);

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            sendError(response, "Product name is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Long userId = getUserIdFromRequest(request);

            ProductResponseDTO product = productController.createProduct(
                    username,
                    productDTO.getName(),
                    productDTO.getCategory(),
                    productDTO.getBrand(),
                    productDTO.getPrice(),
                    productDTO.getDescription(),
                    userId
            );

            Map<String, Object> result = Map.of(
                    "success", true,
                    "message", "Product created successfully",
                    "product", product
            );
            sendJsonResponse(response, result, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException | NullPointerException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = getUsernameFromRequest(request);
        long productId = extractIdFromPath(request);
        ProductUpdateDTO updateDTO = parseJsonBody(request, ProductUpdateDTO.class);

        try {
            ProductResponseDTO product = productController.updateProduct(
                    username,
                    productId,
                    updateDTO.getName(),
                    updateDTO.getCategory(),
                    updateDTO.getBrand(),
                    updateDTO.getPrice(),
                    updateDTO.getDescription()
            );

            if (product != null) {

                Map<String, Object> result = Map.of(
                        "success", true,
                        "message", "Product updated successfully",
                        "product", product
                );
                sendJsonResponse(response, result, HttpServletResponse.SC_OK);
            } else {
                sendError(response, "Product not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleDeleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = getUsernameFromRequest(request);
        long productId = extractIdFromPath(request);

        try {
            boolean deleted = productController.deleteProduct(username, productId);

            if (deleted) {
                Map<String, Object> result = Map.of(
                        "success", true,
                        "message", "Product deleted successfully"
                );
                sendJsonResponse(response, result, HttpServletResponse.SC_OK);
            } else {
                sendError(response, "Product not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private long extractIdFromPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return Long.parseLong(pathInfo.substring(1));
    }

    private String getUsernameFromRequest(HttpServletRequest request) {
        return request.getHeader("X-Username");
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        return userIdHeader != null ? Long.parseLong(userIdHeader) : 1L;
    }
}