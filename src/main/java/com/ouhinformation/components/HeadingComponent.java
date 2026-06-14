package com.ouhinformation.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Komponen heading — tampil sebagai judul besar di user view.
 * Type: "heading"
 */
public class HeadingComponent extends ContentComponent {

    private TextField editor;

    public HeadingComponent(String content) {
        super(content);
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
        label.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #1e293b;"
        );
        return label;
    }

    @Override
    public String getContentFromEditor() {
        return editor != null ? editor.getText() : content;
    }
}
