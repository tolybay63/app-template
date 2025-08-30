package kz.kis.kiscore.utils;

import kz.kis.kiscore.model.*;

import java.util.*;

public class UtOutTable {

    List<DbRec> res;
    List<String> cols = new ArrayList<>();
    Map<String, String> colType = new HashMap<>();

    String NULL_STRING_VALUE = "<null>";

    public UtOutTable(List<DbRec> res) {
        this.res = res;
        cols.clear();
        colType.clear();
        calcCols();
    }

    public void saveTo(StringBuilder writer, int limit) {
        int countRows = getCountRows();
        int countCols = getCountCols();
        int[] maxWidths = new int[countCols];
        boolean[] alignRight = new boolean[countCols];

        // размер заголовков и выравнивание
        for (int col = 0; col < countCols; col++) {
            maxWidths[col] = getColTitle(col).length();
            alignRight[col] = getDataTypeAlignRight(col);
        }

        // размеры данных
        for (int row = 0; row < countRows; row++) {
            if (limit > 0 && row >= limit) {
                break;
            }
            for (int col = 0; col < countCols; col++) {
                String v = getCellText(row, col);
                maxWidths[col] = Math.max(maxWidths[col], v.length());
            }
        }

        // общая ширина
        int widthAll = 2;
        for (int col = 0; col < countCols; col++) {
            widthAll = widthAll + maxWidths[col] + 1;
        }

        // разделитель линий
        StringBuilder linedelim = new StringBuilder();
        linedelim.append("+");
        for (int col = 0; col < countCols; col++) {
            linedelim.append(UtString.repeat("-", maxWidths[col])).append("+");
        }
        linedelim.append("\n");

        writer.append(linedelim);

        // заголовки
        writer.append("|");
        for (int col = 0; col < countCols; col++) {
            writer.append(UtString.padCenter(cols.get(col), maxWidths[col])).append("|");
        }
        writer.append("\n");


        //
        writer.append(linedelim);

        int numrec = 0;
        boolean limited = false;
        // данные
        for (int row = 0; row < countRows; row++) {
            if (limit > 0 && numrec >= limit) {
                limited = true;
                break;
            }
            numrec++;
            writer.append("|");
            for (int col = 0; col < countCols; col++) {
                String val = getCellText(row, col);

                if (alignRight[col]) {
                    val = UtString.padLeft(val, maxWidths[col]);
                } else {
                    val = UtString.padRight(val, maxWidths[col]);
                }

                writer.append(val).append("|");
            }
            writer.append("\n");
        }

        //
        writer.append(linedelim);
        writer.append("records: ").append(UtCnv.toString(countRows));
        if (limited) {
            writer.append(", print records: ").append(UtCnv.toString(numrec));
        }

    }

    private boolean getDataTypeAlignRight(int col) {
        String fieldName = cols.get(col);
        String dataType = colType.get(fieldName);

        if (dataType == null) {
            return true;
        }

        switch (dataType) {
            case "java.lang.Integer", "java.lang.Long", "java.lang.Double": {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private String getCellText(int row, int col) {
        Map<String, Object> rec = res.get(row);
        Object value = rec.get(cols.get(col));
        if (value == null) {
            return NULL_STRING_VALUE;
        } else {
            return UtCnv.toString(value);
        }
    }

    private String getColTitle(int col) {
        return cols.get(col);
    }

    private int getCountCols() {
        return cols.size();
    }

    private int getCountRows() {
        return res.size();
    }

    private void calcCols() {
        for (int row = 0; row < getCountRows(); row++) {
            Map<String, Object> rec = res.get(row);
            if (rec != null) {

                for (String fieldName : rec.keySet()) {
                    if (!cols.contains(fieldName)) {
                        cols.add(fieldName);
                        //
                        Object value = rec.get(fieldName);
                        if (value != null) {
                            colType.put(fieldName, value.getClass().getName());
                        }
                    }
                }
            }
        }

    }


}
