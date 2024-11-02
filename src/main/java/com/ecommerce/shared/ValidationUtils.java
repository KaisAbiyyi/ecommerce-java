package com.ecommerce.shared;

import javafx.scene.control.TextField;
import java.util.regex.Pattern;

public class ValidationUtils {

    // Check if a TextField is empty
    public static boolean isFieldEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    // Validate email format
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    // Validate if a field contains only numbers
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    // Add other validation methods as needed
}
