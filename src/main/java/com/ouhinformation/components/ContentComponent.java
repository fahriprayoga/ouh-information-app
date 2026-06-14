package com.ouhinformation.components;

import javafx.scene.Node;
import org.bson.Document;

/**
 * Base abstract class untuk semua komponen konten CMS.
 * Setiap komponen bisa di-render sebagai editor (admin) atau tampilan (user).
 */
public abstract class ContentComponent {

    protected String content;

    public ContentComponent(String content) {
        this.content = content != null ? content : "";
    }

    public void setContent(String content) {
        this.content = content != null ? content : "";
    }

    /** Tipe komponen (heading, paragraf, list) */
    public abstract String getType();

    /** Label yang tampil di UI editor */
    public abstract String getLabel();

    /** Render sebagai editor (admin side) — TextField/TextArea */
    public abstract Node renderEditor();

    /** Render sebagai tampilan read-only (user side) */
    public abstract Node renderView();

    /** Ambil konten terbaru dari editor */
    public abstract String getContentFromEditor();

    /** Konversi ke BSON Document untuk simpan ke MongoDB */
    public Document toDocument() {
        return new Document("type", getType())
                .append("content", getContentFromEditor());
    }

    /** Factory method: buat komponen dari BSON Document */
    public static ContentComponent fromDocument(Document doc) {
        if (doc == null) return null;
        String type = doc.getString("type");
        String content = doc.getString("content");

        switch (type != null ? type : "") {
            case "heading":
                return new HeadingComponent(content);
            case "paragraf":
            case "text":
                return new ParagraphComponent(content);
            case "list":
                return new ListComponent(content);
            default:
                // Fallback: treat unknown types as paragraph
                return new ParagraphComponent(content);
        }
    }
}
