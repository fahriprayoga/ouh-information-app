package com.ouhinformation.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

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
        label.setStyle(
            "-fx-font-size: 15px; -fx-text-fill: #475569; -fx-line-spacing: 5px;"
        );
        return label;
    }

    @Override
    public String getContentFromEditor() {
        return editor != null ? editor.getText() : content;
    }
}
