package com.ecommerce.components;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CardBase extends VBox {
    public CardBase(double width, double height, String backgroundColor, String shadowEffect) {
        setPrefWidth(width);
        setPrefHeight(height);
        setStyle(String.format("""
                -fx-background-color: %s;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-effect: %s;
                """, backgroundColor, shadowEffect));
        setSpacing(10);
        setPadding(new javafx.geometry.Insets(15));
    }

    public void addTitle(String title) {
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        getChildren().add(titleText);
    }
}
