package test;

import test.main.repository.ProductRepositoryTest;
import test.main.repository.UserRepositoryTest;
import test.service.product.ProductServiceTest;
import test.service.user.UserServiceTest;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("Starting all tests...\n");

        boolean allTestsPassed = true;

        try {
            System.out.println("=== USER REPOSITORY TESTS ===");
            UserRepositoryTest userRepoTest = new UserRepositoryTest();
            userRepoTest.runAllTests();
            System.out.println("User Repository tests passed!\n");

        } catch (AssertionError e) {
            System.err.println("User Repository test failed: " + e.getMessage());
            allTestsPassed = false;
        } catch (Exception e) {
            System.err.println("Unexpected error in User Repository tests: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        try {
            System.out.println("=== USER SERVICE TESTS ===");
            UserServiceTest userServiceTest = new UserServiceTest();
            userServiceTest.runAllTests();
            System.out.println("User Service tests passed!\n");

        } catch (AssertionError e) {
            System.err.println("User Service test failed: " + e.getMessage());
            allTestsPassed = false;
        } catch (Exception e) {
            System.err.println("Unexpected error in User Service tests: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        try {
            System.out.println("=== PRODUCT REPOSITORY TESTS ===");
            ProductRepositoryTest productRepoTest = new ProductRepositoryTest();
            productRepoTest.runAllTests();
            System.out.println("Product Repository tests passed!\n");

        } catch (AssertionError e) {
            System.err.println("Product Repository test failed: " + e.getMessage());
            allTestsPassed = false;
        } catch (Exception e) {
            System.err.println("Unexpected error in Product Repository tests: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        try {
            System.out.println("=== PRODUCT SERVICE TESTS ===");
            ProductServiceTest productServiceTest = new ProductServiceTest();
            productServiceTest.runAllTests();
            System.out.println("Product Service tests passed!\n");

        } catch (AssertionError e) {
            System.err.println("Product Service test failed: " + e.getMessage());
            allTestsPassed = false;
        } catch (Exception e) {
            System.err.println("Unexpected error in Product Service tests: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        if (allTestsPassed) {
            System.out.println("All tests passed successfully!");
        } else {
            System.out.println("Some tests failed!");
            System.exit(1);
        }
    }
}