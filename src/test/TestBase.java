package test;

public class TestBase {

    protected void assertEquals(Object expected, Object actual, String message) {
        if (!objectsEqual(expected, actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", but was: " + actual);
        }
    }

    // Для double с delta
    protected void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " - Expected: " + expected + ", but was: " + actual);
        }
    }

    // Для float с delta
    protected void assertEquals(float expected, float actual, float delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " - Expected: " + expected + ", but was: " + actual);
        }
    }

    // Для long
    protected void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", but was: " + actual);
        }
    }

    // Для int
    protected void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", but was: " + actual);
        }
    }

    // Для boolean
    protected void assertEquals(boolean expected, boolean actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", but was: " + actual);
        }
    }

    protected void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    protected void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    protected void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    protected void assertThrows(Class<? extends Exception> expectedException, Runnable code, String message) {
        try {
            code.run();
            throw new AssertionError(message + " - Expected exception: " + expectedException.getSimpleName());
        } catch (Exception e) {
            if (!expectedException.isInstance(e)) {
                throw new AssertionError(message + " - Expected " + expectedException.getSimpleName() + " but got " + e.getClass().getSimpleName());
            }
        }
    }

    private boolean objectsEqual(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}