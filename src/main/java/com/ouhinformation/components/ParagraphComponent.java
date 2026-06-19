package com.ouhinformation.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextAlignment;

/**
 * Komponen paragraf — tampil sebagai teks biasa di user view.
 * Type: "paragraf"
 */
public class ParagraphComponent extends ContentComponent {

    private TextArea editor;

    public ParagraphComponent(String content) {
        super(content);
    }

    @Override
    public String getType() {
        return "paragraf";
    }

    @Override
    public String getLabel() {
        return "Paragraf";
    }

    @Override
    public Node renderEditor() {
        editor = new TextArea(content);
        editor.setPromptText("Masukkan paragraf...");
        editor.setWrapText(true);
        editor.setPrefRowCount(6);
        editor.setStyle(
            "-fx-font-size: 14px; -fx-padding: 8; " +
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
            "-fx-text-fill: " + color + "; " +
            "-fx-line-spacing: 5px;"
        );
        return label;
    }

    @Override
    public String getContentFromEditor() {
        return editor != null ? editor.getText() : content;
    }
}
