package com.ouhinformation.components;

import javafx.scene.Node;
import org.bson.Document;

/**
 * Base abstract class untuk semua komponen konten CMS.
 * Setiap komponen bisa di-render sebagai editor (admin) atau tampilan (user).
 */
public abstract class ContentComponent {

    protected String content;
    protected String color;
    protected Integer fontSize;
    protected String textAlign;

    public ContentComponent(String content) {
        this.content = content != null ? content : "";
        this.color = "#475569"; // default
        this.fontSize = 14;    // default
        this.textAlign = "LEFT"; // default
    }

    public String getColor() { return color; }
    public void setColor(String color) { if (color != null) this.color = color; }

    public Integer getFontSize() { return fontSize; }
    public void setFontSize(Integer fontSize) { if (fontSize != null) this.fontSize = fontSize; }

    public String getTextAlign() { return textAlign; }
    public void setTextAlign(String textAlign) { if (textAlign != null) this.textAlign = textAlign; }

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
                .append("content", getContentFromEditor())
                .append("color", color)
                .append("fontSize", fontSize)
                .append("textAlign", textAlign);
    }

    /** Factory method: buat komponen dari BSON Document */
    public static ContentComponent fromDocument(Document doc) {
        if (doc == null) return null;
        String type = doc.getString("type");
        String content = doc.getString("content");

        ContentComponent comp;
        switch (type != null ? type : "") {
            case "heading":
                comp = new HeadingComponent(content);
                break;
            case "image":
                comp = ImageComponent.fromDoc(doc);
                break;
            case "list":
                comp = ListComponent.fromDoc(doc);
                break;
            case "paragraf":
            case "text":
            default:
                comp = new ParagraphComponent(content);
                break;
        }

        if (comp != null) {
            comp.setColor(doc.getString("color"));
            comp.setFontSize(doc.getInteger("fontSize"));
            comp.setTextAlign(doc.getString("textAlign"));
        }
        return comp;
    }
}
