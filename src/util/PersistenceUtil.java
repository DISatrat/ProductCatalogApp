package util;

import java.io.*;

public class PersistenceUtil {
    public static void saveObject(Object obj, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения " + filename + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadObject(String filename, Class<T> cls) {
        File f = new File(filename);
        if (!f.exists()) return null;
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object o = ois.readObject();
            return (T) o;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки " + filename + ": " + e.getMessage());
            return null;
        }
    }
}
