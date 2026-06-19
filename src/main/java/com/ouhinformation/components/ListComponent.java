package com.ouhinformation.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bson.Document;

/**
 * Komponen list — tampil sebagai bullet atau numbered list di user view.
 * Type: "list"
 */
public class ListComponent extends ContentComponent {

    private TextArea editor;
    private ToggleGroup listTypeGroup;
    private boolean isOrdered = false;

    public ListComponent(String content) {
        super(content);
    }

    public void setOrdered(boolean ordered) {
        this.isOrdered = ordered;
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
        VBox root = new VBox(8);
        
        HBox typeBox = new HBox(15);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        Label typeLabel = new Label("Tipe List:");
        typeLabel.setStyle("-fx-font-weight: bold;");
        
        listTypeGroup = new ToggleGroup();
        RadioButton bulletBtn = new RadioButton("Bullet (•)");
        bulletBtn.setToggleGroup(listTypeGroup);
        bulletBtn.setSelected(!isOrdered);
        bulletBtn.setUserData(false);
        
        RadioButton numberedBtn = new RadioButton("Numbered (1.)");
        numberedBtn.setToggleGroup(listTypeGroup);
        numberedBtn.setSelected(isOrdered);
        numberedBtn.setUserData(true);
        
        typeBox.getChildren().addAll(typeLabel, bulletBtn, numberedBtn);

        editor = new TextArea(content);
        editor.setPromptText("Masukkan list (satu item per baris)...");
        editor.setWrapText(true);
        editor.setPrefRowCount(5);
        editor.setStyle(
            "-fx-font-size: 14px; -fx-padding: 8; " +
            "-fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;"
        );
        
        root.getChildren().addAll(typeBox, editor);
        return root;
    }

    @Override
    public Node renderView() {
        VBox listContainer = new VBox(6);
        
        String align = textAlign != null ? textAlign : "LEFT";
        if ("CENTER".equals(align)) {
            listContainer.setAlignment(Pos.CENTER);
        } else if ("RIGHT".equals(align)) {
            listContainer.setAlignment(Pos.CENTER_RIGHT);
        } else {
            listContainer.setAlignment(Pos.CENTER_LEFT);
        }
        
        if (content != null && !content.isEmpty()) {
            String[] items = content.split("\n");
            int count = 1;
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    String prefix = isOrdered ? count + ". " : "•  ";
                    Label bullet = new Label(prefix + trimmed);
                    bullet.setWrapText(true);
                    bullet.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
                    bullet.setStyle(
                        "-fx-font-size: " + fontSize + "px; " +
                        "-fx-text-fill: " + color + "; " +
                        "-fx-padding: 2 0 2 15;"
                    );
                    listContainer.getChildren().add(bullet);
                    count++;
                }
            }
        }
        return listContainer;
    }

    @Override
    public String getContentFromEditor() {
        if (listTypeGroup != null && listTypeGroup.getSelectedToggle() != null) {
            isOrdered = (boolean) listTypeGroup.getSelectedToggle().getUserData();
        }
        return editor != null ? editor.getText() : content;
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("isOrdered", isOrdered);
    }

    public static ListComponent fromDoc(Document doc) {
        ListComponent comp = new ListComponent(doc.getString("content"));
        comp.setOrdered(doc.getBoolean("isOrdered", false));
        return comp;
    }
}
