package com.ecommerce.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * Meng-enkripsi password menggunakan BCrypt.
     *
     * @param plainPassword Password dalam bentuk teks biasa
     * @return Password yang telah di-hash
     */
    public static String encrypt(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Memverifikasi password dengan hash yang tersimpan.
     *
     * @param plainPassword Password dalam bentuk teks biasa
     * @param hashedPassword Hash password yang tersimpan
     * @return true jika password cocok, false jika tidak
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Mengecek apakah string tertentu sudah merupakan hash BCrypt.
     *
     * @param password String yang akan diperiksa
     * @return true jika string adalah hash BCrypt, false jika tidak
     */
    public static boolean isHashed(String password) {
        // Hash BCrypt selalu memiliki 60 karakter dan diawali dengan $2a$, $2b$, atau $2y$
        return password != null && password.length() == 60 && password.startsWith("$2");
    }

    // Optional: Metode untuk debugging (jika diperlukan)
    public static void main(String[] args) {
        String password = "adminpass";
        String hashed = encrypt(password);

        System.out.println("Password asli: " + password);
        System.out.println("Password hash: " + hashed);
        System.out.println("Verifikasi: " + verify(password, hashed));
        System.out.println("Apakah sudah hash: " + isHashed(hashed));
        System.out.println("Apakah sudah hash (password asli): " + isHashed(password));
    }
}
