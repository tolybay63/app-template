package kz.app.appcore.model;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileData {

    private Location location;
    private String text;
    private String source;

    public FileData() {
    }

    public FileData(Location location, String text, String source) {
        this.location = location;
        this.text = text;
        this.source = source;
    }

    public FileData(String text, Location location, String source) {
        this.location = location;
        this.text = text;
        this.source = source;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty() {
        return getText() == null || getText().trim().isEmpty();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void addText(String text) {
        if (this.text == null) {
            this.text = text;
            return;
        }

        if (this.text.length() != 0) {
            this.text = this.text + " ";
        }
        this.text = this.text + text.trim();
    }

    public String toString() {
        return "location: " + location + ", " +
                "text: " + text + ", " +
                "source: " + source;
    }

}

