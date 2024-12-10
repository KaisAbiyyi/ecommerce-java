package com.ecommerce.utils;

import java.io.*;
import java.util.Properties;

public class LocalStorageUtils {

    private static final String FILE_NAME = "local_storage.properties";
    private static Properties properties = new Properties();

    static {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    properties.load(fis);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simpan data ke local storage.
     *
     * @param key   Kunci data
     * @param value Nilai data
     */
    public static void set(String key, String value) {
        properties.setProperty(key, value);
        saveToFile();
    }

    /**
     * Ambil data dari local storage.
     *
     * @param key Kunci data
     * @return Nilai data atau null jika tidak ditemukan
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Hapus data dari local storage.
     *
     * @param key Kunci data yang ingin dihapus
     */
    public static void remove(String key) {
        properties.remove(key);
        saveToFile();
    }

    /**
     * Simpan perubahan ke file.
     */
    private static void saveToFile() {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
            properties.store(fos, "Local Storage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
