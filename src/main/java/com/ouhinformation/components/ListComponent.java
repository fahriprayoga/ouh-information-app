package com.ouhinformation.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Komponen list — tampil sebagai bullet list di user view.
 * Type: "list"
 * Content disimpan satu item per baris (split \n).
 */
public class ListComponent extends ContentComponent {

    private TextArea editor;

    public ListComponent(String content) {
        super(content);
    }

    @Override
    public String getType() {
        return "list";
    }

    @Override
    public String getLabel() {
        return "List";
    }

    @Override
    public Node renderEditor() {
        editor = new TextArea(content);
        editor.setPromptText("Masukkan list (satu item per baris)...");
        editor.setWrapText(true);
        editor.setPrefRowCount(7);
        editor.setStyle(
            "-fx-font-size: 14px; -fx-padding: 8; " +
            "-fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;"
        );
        return editor;
    }

    @Override
    public Node renderView() {
        VBox listContainer = new VBox(4);
        if (content != null && !content.isEmpty()) {
            String[] items = content.split("\n");
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    Label bullet = new Label("•  " + trimmed);
                    bullet.setWrapText(true);
                    bullet.setStyle(
                        "-fx-font-size: 14px; -fx-text-fill: #475569; -fx-padding: 2 0 2 15;"
                    );
                    listContainer.getChildren().add(bullet);
                }
            }
        }
        return listContainer;
    }

    @Override
    public String getContentFromEditor() {
        return editor != null ? editor.getText() : content;
    }
}
