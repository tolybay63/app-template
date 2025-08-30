package kz.kis.kiscore.model;

import java.util.Objects;

public class Location {

    private Integer orderNumber;
    private Integer rowNumber;
    private Integer columnNumber;
    private Integer sheetNumber;
    private Integer pageNumber;
    private Integer paragraphNumber;
    private Float y1;
    private Float y2;
    private Float x1;
    private Float x2;

    public Location() {
    }

    public Location(Integer paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
        this.y1 = null;
        this.y2 = null;
        this.x1 = null;
        this.x2 = null;
    }

    public Location(Integer orderNumber, Integer rowNumber, Integer columnNumber, Integer sheetNumber, Integer pageNumber, Integer paragraphNumber, Float y1, Float y2, Float x1, Float x2) {
        this.orderNumber = orderNumber;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.sheetNumber = sheetNumber;
        this.pageNumber = pageNumber;
        this.paragraphNumber = paragraphNumber;
        this.y1 = y1;
        this.y2 = y2;
        this.x1 = x1;
        this.x2 = x2;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public Integer getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(Integer sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setXY(float y1, float y2, float x1, float x2) {
        this.y1 = y1;
        this.y2 = y2;
        this.x1 = x1;
        this.x2 = x2;
    }

    public void setXY(double y1, double y2, double x1, double x2) {
        this.y1 = (float) y1;
        this.y2 = (float) y2;
        this.x1 = (float) x1;
        this.x2 = (float) x2;
    }

    public Integer getParagraphNumber() {
        return paragraphNumber;
    }

    public void setParagraphNumber(Integer paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }

    public Float getY1() {
        return y1;
    }

    public Float getY2() {
        return y2;
    }

    public Float getX1() {
        return x1;
    }

    public Float getX2() {
        return x2;
    }

    public void mergeX1(float x1) {
        if (this.x1 == null) {
            this.x1 = x1;
        } else {
            if (x1 < this.x1) {
                this.x1 = x1;
            }
        }
    }

    public void mergeX2(float x2) {
        if (this.x2 == null) {
            this.x2 = x2;
        } else {
            if (x2 > this.x2) {
                this.x2 = x2;
            }
        }
    }

    public void mergeY1(float y1) {
        if (this.y1 == null) {
            this.y1 = y1;
        } else {
            if (y1 < this.y1) {
                this.y1 = y1;
            }
        }
    }

    public void mergeY2(float y2) {
        if (this.y2 == null) {
            this.y2 = y2;
        } else {
            if (y2 > this.y2) {
                this.y2 = y2;
            }
        }
    }

    public String toString() {
        return "x1: " + x1 + ", " +
                "y1: " + y1 + ", " +
                "x2: " + x2 + ", " +
                "y2: " + y2 + ", " +
                "orderNumber: " + orderNumber + ", " +
                "rowNumber: " + rowNumber + ", " +
                "columnNumber: " + columnNumber + ", " +
                "pageNumber: " + pageNumber + ", " +
                "sheetNumber: " + sheetNumber + ", " +
                "paragraphNumber: " + paragraphNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(orderNumber, location.orderNumber) && Objects.equals(rowNumber, location.rowNumber) && Objects.equals(columnNumber, location.columnNumber) && Objects.equals(sheetNumber, location.sheetNumber) && Objects.equals(pageNumber, location.pageNumber) && Objects.equals(paragraphNumber, location.paragraphNumber) && Objects.equals(y1, location.y1) && Objects.equals(y2, location.y2) && Objects.equals(x1, location.x1) && Objects.equals(x2, location.x2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, rowNumber, columnNumber, sheetNumber, pageNumber, paragraphNumber, y1, y2, x1, x2);
    }
}

