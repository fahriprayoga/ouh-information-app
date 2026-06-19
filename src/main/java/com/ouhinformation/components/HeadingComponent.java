package com.ouhinformation.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;

/**
 * Komponen heading — tampil sebagai judul besar di user view.
 * Type: "heading"
 */
public class HeadingComponent extends ContentComponent {

    private TextField editor;

    public HeadingComponent(String content) {
        super(content);
        this.fontSize = 20; // Default heading size
    }

    @Override
    public String getType() {
        return "heading";
    }

    @Override
    public String getLabel() {
        return "Heading";
    }

    @Override
    public Node renderEditor() {
        editor = new TextField(content);
        editor.setPromptText("Masukkan heading...");
        editor.setPrefHeight(50);
        editor.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10; " +
            "-fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;"
        );
        return editor;
    }

    @Override
    public Node renderView() {
        Label label = new Label(content);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        
        String align = textAlign != null ? textAlign : "LEFT";
        if ("CENTER".equals(align)) {
            label.setAlignment(Pos.CENTER);
            label.setTextAlignment(TextAlignment.CENTER);
        } else if ("RIGHT".equals(align)) {
            label.setAlignment(Pos.CENTER_RIGHT);
            label.setTextAlignment(TextAlignment.RIGHT);
        } else {
            label.setAlignment(Pos.CENTER_LEFT);
            label.setTextAlignment(TextAlignment.LEFT);
        }
        
        label.setStyle(
            "-fx-font-size: " + fontSize + "px; " +
            "-fx-font-weight: 800; " +
            "-fx-text-fill: " + color + ";"
        );
        return label;
    }

    @Override
    public String getContentFromEditor() {
        return editor != null ? editor.getText() : content;
    }
}
