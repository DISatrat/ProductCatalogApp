package main.util;

import java.io.*;

/**
 * Утилитарный класс для сериализации и десериализации объектов в файлы
 * Предоставляет методы для сохранения и загрузки состояния объектов между запусками приложения
 */
public class PersistenceUtil {

    /**
     * Сохраняет объект в файл с использованием сериализации
     *
     * @param obj объект для сохранения
     * @param filename имя файла для сохранения
     * @throws RuntimeException если произошла ошибка ввода-вывода при сохранении
     */
    public static void saveObject(Object obj, String filename) {
        String safeFilename = filename.startsWith("/") ? filename.substring(1) : filename;
        File file = new File(safeFilename);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);
            System.out.println("Данные сохранены в: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка сохранения " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    /**
     * Загружает объект из файла с использованием десериализации
     *
     * @param <T> тип загружаемого объекта
     * @param filename имя файла для загрузки
     * @param cls класс загружаемого объекта для проверки типа
     * @return загруженный объект или null если файл не существует или произошла ошибка
     * @throws RuntimeException если произошла ошибка ввода-вывода или класс не найден
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadObject(String filename, Class<T> cls) {
        File file = new File(filename);
        if (!file.exists()) return null;
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object object = ois.readObject();
            return (T) object;
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("Ошибка загрузки " + filename + ": " + exception.getMessage());
            return null;
        }
    }

}