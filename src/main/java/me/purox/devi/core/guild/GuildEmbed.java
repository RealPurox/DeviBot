package me.purox.devi.core.guild;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuildEmbed {

    //author
    private String authorName;
    private String authorURL;
    private String authorIconURL;

    //color
    private Integer colorRed;
    private Integer colorGreen;
    private Integer colorBlue;

    //Footer
    private String footerText;
    private String footerIconURL;

    //Description
    private String description;

    //Image
    private String imageURL;

    //Thumbnail
    private String thumbnailURL;

    //Title
    private String title;
    private String titleURL;

    //Fields
    private List<MessageEmbed.Field> fields = new ArrayList<>();

    private boolean isEmpty() {
        return authorName == null && authorURL == null && authorIconURL == null && colorRed == null && colorGreen == null && colorBlue == null && footerText == null && footerIconURL == null &&
                description == null && imageURL == null && thumbnailURL == null && title == null && titleURL == null && fields.isEmpty();
    }

    public MessageEmbed toEmbed() {
        if (isEmpty()) {
            return new EmbedBuilder().setColor(Color.RED).setDescription(":warning: Something went wrong while trying to create your message embed: `All variables are null`").build();
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(authorName, authorURL, authorIconURL);
        builder.setColor(new Color(colorRed == null ? 0 : colorRed, colorGreen == null ? 0 : colorGreen, colorBlue == null ? 0 : colorBlue));
        builder.setFooter(footerText, footerIconURL);
        builder.setDescription(description);
        builder.setImage(imageURL);
        builder.setThumbnail(thumbnailURL);
        builder.setTitle(title, titleURL);

        for(MessageEmbed.Field field : fields) {
            builder.addField(field);
        }

        return builder.build();
    }

    public Document toDocument() {
        Document document = new Document();
        document.put("authorName", authorName);
        document.put("authorURL", authorURL);
        document.put("authorIconURL", authorIconURL);
        document.put("colorRed", colorRed);
        document.put("colorGreen", colorGreen);
        document.put("colorBlue", colorBlue);
        document.put("footerText", footerText);
        document.put("footerIconURL", footerIconURL);
        document.put("description", description);
        document.put("imageURL", imageURL);
        document.put("thumbnailURL", thumbnailURL);
        document.put("title", title);
        document.put("titleURL", titleURL);

        List<Document> fieldDocument = new ArrayList<>();
        for (MessageEmbed.Field field : fields) {
            Document doc = new Document();
            doc.put("fieldName", field.getName());
            doc.put("fieldValue", field.getValue());
            doc.put("fieldInline", field.isInline());
            fieldDocument.add(doc);
        }

        document.put("fields", fieldDocument);
        return document;
    }

    public void setAuthorIconURL(String authorIconURL) {
        this.authorIconURL = authorIconURL;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorURL(String authorURL) {
        this.authorURL = authorURL;
    }

    public void setColorBlue(Integer colorBlue) {
        this.colorBlue = colorBlue;
    }

    public void setColorGreen(Integer colorGreen) {
        this.colorGreen = colorGreen;
    }

    public void setColorRed(Integer colorRed) {
        this.colorRed = colorRed;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFields(List<MessageEmbed.Field> fields) {
        this.fields = fields;
    }

    public void setFooterIconURL(String footerIconURL) {
        this.footerIconURL = footerIconURL;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleURL(String titleURL) {
        this.titleURL = titleURL;
    }
}
