package kz.app.appcore.utils;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.matcher.StringMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

/**
 * Утилиты для строк
 */
public class UtString {

    private static String LINEWRAPDELIM = " \t,!^)-=+;:./\\`~";

    /**
     * Кодировка utf-8, которая используеься везде по умолчанию
     */
    public static final String UTF8 = "utf-8"; //NON-NLS

    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * Вставить переводы строки \n в строку, что бы длина
     * каждой полученной строки не превышала width
     */
    public static String wrapLine(String text, int width) {
        if (text == null) {
            return "";
        }
        StringBuffer b = new StringBuffer();
        int cnt = text.length();
        int beg = 0;
        int end = 0;
        char c;
        for (int i = 0; i < cnt; i++) {
            c = text.charAt(i);
            if (c == '\n') {
                if (end == beg) {
                    end = i;
                }
                b.append(text.substring(beg, end + 1));
                beg = end + 1;
                end = beg;
            } else if (LINEWRAPDELIM.indexOf(c) != -1) {
                end = i;
            }
            if (i - beg + 1 >= width) {
                if (end == beg) {
                    end = i;
                }
                b.append(text.substring(beg, end + 1));
                b.append('\n');
                beg = end + 1;
                end = beg;
            }
        }

        if (beg < cnt) {
            b.append(text.substring(beg));
        }

        return b.toString();
    }

    /**
     * Проверка на пустую строку
     *
     * @param value строка
     * @return true, если value пустая строка или null
     */
    public static boolean empty(String value) {
        return (value == null) || (value.length() == 0);
    }

    /**
     * Первая буква прописная, остальные без изменений
     */
    public static String capFirst(String s) {
        if (s == null || s.length() == 0) {
            return "";
        } else {
            char c = s.charAt(0);
            c = Character.toUpperCase(c);
            String s1 = String.valueOf(c) + s.substring(1);
            return s1;
        }
    }

    /**
     * Первая буква прописная, остальные без изменений
     */
    public static String uncapFirst(String s) {
        if (s == null || s.length() == 0) {
            return "";
        } else {
            char c = s.charAt(0);
            c = Character.toLowerCase(c);
            String s1 = String.valueOf(c) + s.substring(1);
            return s1;
        }
    }

    /**
     * Замена символов на эквиваленты, используемые в XML
     *
     * @param xmlstring исходная строка
     * @return строка с замененными '<','>','&','"'
     */
    public static String xmlEscape(String xmlstring) {
        if (xmlstring == null) {
            return "";
        }
        String s = xmlstring;
        s = s.replace("&", "&amp;"); //NON-NLS
        s = s.replace("<", "&lt;"); //NON-NLS
        s = s.replace(">", "&gt;"); //NON-NLS
        s = s.replace("\"", "&quot;"); //NON-NLS
        return s;
    }

    //////

    /**
     * Является ли латинской буквой
     */
    public static boolean isLatChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    /**
     * Является ли цифрой
     */
    public static boolean isNumChar(char c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Является ли частью начала стандартного идентификатора
     */
    public static boolean isIdnStartChar(char c) {
        return isLatChar(c) || c == '_';
    }

    /**
     * Является ли частью стандартного идентификатора
     */
    public static boolean isIdnChar(char c) {
        return isLatChar(c) || isNumChar(c) || c == '_';
    }

    /**
     * Является ли символ частью пустого пространства
     */
    public static boolean isWhiteChar(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    /**
     * Является ли строка правильным xml-именем.
     * Ограничение: правильным считаются только латинские имена.
     *
     * @param name имя
     * @return true -  да
     */
    public static boolean isXmlName(String name) {
        if (name == null) {
            return false;
        }
        final int length = name.length();
        if (length == 0) {
            return false;
        }
        char ch = name.charAt(0);
        if (!UtString.isIdnStartChar(ch)) {
            return false;
        }
        for (int i = 1; i < length; ++i) {
            ch = name.charAt(i);
            if (!(UtString.isIdnChar(ch) || ch == '-' || ch == '.')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Убрать из строки s первую пустую строку, если считать разделителем строк перевод строки
     *
     * @param s откуда
     * @return строка без первой пустой
     */
    public static String removeFirstEmptyLine(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        int i = 0;
        int cnt = s.length();
        while (i < cnt) {
            char c = s.charAt(i);
            if (c == '\n') {
                s = s.substring(i + 1);
                return s;
            }
            if (!isWhiteChar(c)) {
                break;
            }
            i++;
        }
        return s;
    }


    /**
     * Возвращает true, если строка состоит только из пробелов или пустая
     *
     * @param s проверяемая строка
     * @return true, если пустая
     */
    public static boolean isWhite(CharSequence s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!isWhiteChar(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Возвращает true, если строка boolean ("true" или "false")
     */
    public static boolean isBoolean(String s) {
        if ("true".equals(s)) return true; //NON-NLS
        if ("false".equals(s)) return true; //NON-NLS
        return false;
    }

    /**
     * Возвращает true, если строка число (целое или десятичное)
     */
    public static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Возвращает true, если строка целое число
     */
    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Удалить из строки последние пробельные символы
     *
     * @param s строка
     * @return строка без последних пробельных символов
     */
    public static String trimLast(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        int i = s.length() - 1;
        if (!isWhiteChar(s.charAt(i))) {
            return s;  // нечего удалять
        }
        i--;
        while (i >= 0) {
            char c = s.charAt(i);
            if (!isWhiteChar(c)) {
                return s.substring(0, i + 1);
            }
            i--;
        }
        return "";
    }

    /**
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuilder buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /**
     * Converts a byte array to hex string
     *
     * @param block данные
     * @param delim разделитель. Если null, то без разделителя
     */
    public static String toHexString(byte[] block, String delim) {
        StringBuilder buf = new StringBuilder();

        int len = block.length;

        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (delim != null && i < len - 1) {
                buf.append(delim);
            }
        }
        return buf.toString();
    }

    /**
     * Converts a byte array to hex string
     *
     * @param block данные
     */
    public static String toHexString(byte[] block) {
        return toHexString(block, null);
    }

    /**
     * Converts a value to hex string
     *
     * @param value данные
     */
    public static String toHexString(long value) {
        return padLeft(String.format("%x", value).toUpperCase(), 16, "0");
    }

    /**
     * Конвертация tab в пробелы
     *
     * @param s        исходная строка
     * @param tabWidth ширина табуляции
     * @return строка без tab
     */
    public static String tabToSpaces(String s, int tabWidth) {
        if (s == null || s.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        int pos = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\t') {
                int cnt = tabWidth - (pos % tabWidth);
                for (int j = 0; j < cnt; j++) {
                    sb.append(' ');
                }
                pos = pos + cnt - 1;
            } else if (c == '\n') {
                pos = -1;
                sb.append(c);
            } else {
                sb.append(c);
            }
            pos++;
        }
        return sb.toString();
    }

    /**
     * Нормализация отступа. Для строки типа:
     * <pre>
     *     s1
     *       s2
     *     s3
     * </pre>
     * возвращает:
     * <pre>
     * s1
     *   s2
     * s3
     * </pre>
     *
     * @param source
     * @return
     */
    public static String normalizeIndent(String source) {
        if (source == null) {
            return "";
        }
        source = trimLast(source);
        if (source.length() == 0) {
            return "";
        }
        String[] ar = source.split("\n");
        if (ar.length == 1) {
            return ar[0].trim();
        }
        for (int i = 0; i < ar.length; i++) {
            ar[i] = tabToSpaces(ar[i], 4);
        }
        // ищем первую не пустую
        int fi = 0;
        while (true) {
            if (!isWhite(ar[fi])) {
                break;
            }
            fi++;
        }
        // сколько в начале пустого пространства
        int cntSpaces = 0;
        while (cntSpaces < ar[fi].length()) {
            if (!isWhiteChar(ar[fi].charAt(cntSpaces))) {
                break;
            }
            cntSpaces++;
        }
        // удаляем излишки
        if (cntSpaces > 0) {
            for (int i = fi; i < ar.length; i++) {
                if (ar[i].length() < cntSpaces) {
                    ar[i] = ar[i].trim(); // слишком короткая
                } else {
                    String s = ar[i].substring(0, cntSpaces);
                    if (isWhite(s)) {
                        ar[i] = trimLast(ar[i].substring(cntSpaces));
                    } else {
                        ar[i] = ar[i].trim(); // вылазит
                    }
                }
            }
        } else {
            // просто обрезаем концовки
            for (int i = fi; i < ar.length; i++) {
                ar[i] = trimLast(ar[i].substring(cntSpaces));
            }
        }
        // формируем резульат
        StringBuilder sb = new StringBuilder();
        for (int i = fi; i < ar.length; i++) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(ar[i]);
        }
        return sb.toString();
    }

    /**
     * Добавляет в начало каждой строки в многострочной src строку indent
     *
     * @param src    исходная строка
     * @param indent чем выравнивать
     * @return выровненная строка
     */
    public static String indent(String src, String indent) {
        if (empty(src)) {
            return indent;
        }
        String[] ar = src.split("\n");
        StringBuilder b = new StringBuilder();
        for (String s : ar) {
            if (b.length() > 0) {
                b.append('\n');
            }
            b.append(indent);
            b.append(s);
        }
        return b.toString();
    }

    /**
     * Добавляет в начало каждой строки в многострочной src indentSpaces пробелов
     *
     * @param src          исходная строка
     * @param indentSpaces количество пробелов
     * @return выровненная строка
     */
    public static String indent(String src, int indentSpaces) {
        return indent(src, repeat(" ", indentSpaces));
    }

    private static boolean existsInArray(String s, String[] ar) {
        for (String s1 : ar) {
            if (s1.trim().toLowerCase().equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка, компоненты строки с разделителями str имеют пересечения с
     * inStr
     * <pre>
     * b = isDelimitedIntersect("ins","del,ins,upd",",",true) // true
     * b = isDelimitedIntersect("ins,del","del",",",true)     // true
     * b = isDelimitedIntersect("ins,del","upd",",",true)     // false
     * </pre>
     *
     * @param src       исходная строка
     * @param inStr     где ищем. Если элемент строки равен '*', то true для любого проверяемого,
     *                  если элемент равен '-', то false для любого проверяемого.
     *                  Если элемент начинается с '-', то false, если встретился такой в src
     * @param delimiter разделитель. Если null, то используется ','
     * @param emptyTrue если true, то если src или inStr пустые или null,
     *                  то все равно возвращается true
     * @return true - если разрешен
     */
    public static boolean isDelimitedIntersect(String src, String inStr,
            String delimiter, boolean emptyTrue) {

        if (src == null || src.length() == 0) {
            return emptyTrue;
        }
        if (inStr == null || inStr.length() == 0) {
            return emptyTrue;
        }

        if (delimiter == null) {
            delimiter = ",";
        }

        String[] arSrc = src.split(delimiter);
        String[] arInStr = inStr.split(delimiter);

        boolean res = false;
        for (String m1 : arInStr) {
            String m = m1.trim().toLowerCase();
            if (m.equals("*")) {
                res = true;
            } else if (m.equals("-")) {
                res = false;
                break;
            } else if (existsInArray(m, arSrc)) {
                res = true;
            } else if (m.startsWith("-")) {
                String s1 = m.substring(1);
                if (existsInArray(s1, arSrc)) {
                    res = false;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * isDelimitedIntersect с разделителем ',' и emptyTrue=true
     */
    public static boolean isDelimitedIntersect(String src, String inStr) {
        return isDelimitedIntersect(src, inStr, ",", true);
    }

    /**
     * Перевод строки в camelCase (например MyClassName) в строку с разделителями delim
     * (например my_class_name)
     *
     * @param s     исходная строка
     * @param delim разделитель слов
     * @return строка в lower с разделителями delim
     */
    public static String unCamelCase(String s, char delim) {
        if (s == null || s.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean prevIsChar = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
                if (prevIsChar) {
                    sb.append(delim);
                }
            }
            prevIsChar = isIdnChar(c);
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * {@link UtString#unCamelCase(java.lang.String, char)},
     * где delim='_'
     */
    public static String unCamelCase(String s) {
        return unCamelCase(s, '_');
    }

    /**
     * Удаляет из строки префикс, если он есть.
     *
     * @param src    исходная строка
     * @param prefix префикс. Регистрозависимый.
     * @return Возвращает строку без префикса, если префикс найден. Иначе возвращает null.
     */
    public static String removePrefix(String src, String prefix) {
        if (empty(src) || empty(prefix)) {
            return null;
        }
        if (src.startsWith(prefix)) {
            return src.substring(prefix.length());
        }
        return null;
    }

    /**
     * Удаляет из строки суффикс, если он есть.
     *
     * @param src    исходная строка
     * @param suffix суффикс. Регистрозависимый.
     * @return Возвращает строку без суффикса, если суффикс найден. Иначе возвращает null.
     */
    public static String removeSuffix(String src, String suffix) {
        if (empty(src) || empty(suffix)) {
            return null;
        }
        if (src.endsWith(suffix)) {
            return src.substring(0, src.length() - suffix.length());
        }
        return null;
    }

    /**
     * Перевод списка в массив строк
     *
     * @param lst любой список
     * @return массив строк (toString() для каждого элемента). Для null-элементов в
     * массив записывается пустая строка
     */
    public static String[] toArray(List lst) {
        if (lst == null) {
            return new String[0];
        }
        String[] a = new String[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            Object ob = lst.get(i);
            if (ob == null) {
                a[i] = "";
            } else {
                a[i] = ob.toString();
            }
        }
        return a;
    }

    /**
     * Повторение строки
     *
     * @param str    какую строку повторять
     * @param repeat сколько раз
     */
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return "";
        }
        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                StringBuffer buf = new StringBuffer(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }


    /**
     * Выравнивание справа пробелами
     *
     * @param str  исходная строка
     * @param size до какого размера выравнивать
     */
    public static String padRight(String str, int size) {
        return padRight(str, size, ' ');
    }

    /**
     * Выравнивание справа указанным символом
     *
     * @param str     исходная строка
     * @param size    до какого размера выравнивать
     * @param padChar каким символом выравнивать
     */
    public static String padRight(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return padRight(str, size, String.valueOf(padChar));
        }
        return str.concat(padding(pads, padChar));
    }

    /**
     * Выравнивание справа указанной строкой
     *
     * @param str    исходная строка
     * @param size   до какого размера выравнивать
     * @param padStr какой строкой выравнивать
     */
    public static String padRight(String str, int size, String padStr) {
        if (str == null) {
            str = "";
        }
        if (empty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return padRight(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * Выравнивание слева пробелами
     *
     * @param str  исходная строка
     * @param size до какого размера выравнивать
     */
    public static String padLeft(String str, int size) {
        return padLeft(str, size, ' ');
    }

    /**
     * Выравнивание слева указанным символом
     *
     * @param str     исходная строка
     * @param size    до какого размера выравнивать
     * @param padChar каким символом выравнивать
     */
    public static String padLeft(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return padLeft(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }

    /**
     * Выравнивание слева указанной строкой
     *
     * @param str    исходная строка
     * @param size   до какого размера выравнивать
     * @param padStr какой строкой выравнивать
     */
    public static String padLeft(String str, int size, String padStr) {
        if (str == null) {
            str = "";
        }
        if (empty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return padLeft(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * Выравнивание по центру пробелами
     *
     * @param str  исходная строка
     * @param size до какого размера выравнивать
     */
    public static String padCenter(String str, int size) {
        return padCenter(str, size, ' ');
    }

    /**
     * Выравнивание по центру указанным символом
     *
     * @param str     исходная строка
     * @param size    до какого размера выравнивать
     * @param padChar каким символом выравнивать
     */
    public static String padCenter(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }
        if (size <= 0) {
            return str;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = padLeft(str, strLen + pads / 2, padChar);
        str = padRight(str, size, padChar);
        return str;
    }

    /**
     * Выравнивание по центру указанной строкой
     *
     * @param str    исходная строка
     * @param size   до какого размера выравнивать
     * @param padStr какой строкой выравнивать
     */
    public static String padCenter(String str, int size, String padStr) {
        if (str == null) {
            str = "";
        }
        if (size <= 0) {
            return str;
        }
        if (empty(padStr)) {
            padStr = " ";
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = padLeft(str, strLen + pads / 2, padStr);
        str = padRight(str, size, padStr);
        return str;
    }

    //////

    /**
     * Изготовления строки CamelCase из camel-case (или например camel.case)
     *
     * @param src произвольная строка
     */
    public static String camelCase(String src) {
        if (empty(src)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean flag = true;
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (flag) {
                c = Character.toUpperCase(c);
                flag = false;
            }
            if (isLatChar(c) || isNumChar(c)) {
                sb.append(c);
            } else {
                flag = true;
            }
        }
        return sb.toString();
    }

    ////// UtString

    /**
     * Преобразование объекта в список строк.
     * Синоним для {@link UtCnv#toList(java.lang.Object, java.lang.String)}
     */
    public static List<String> toList(Object source, String delimiter) {
        return UtCnv.toList(source, delimiter);
    }

    /**
     * Преобразование объекта в список строк.
     * Синоним для {@link UtCnv#toList(java.lang.Object)}
     */
    public static List<String> toList(Object source) {
        return UtCnv.toList(source);
    }

    /**
     * Перевод строки в crc32 в виде строки
     *
     * @param src исходная строка
     * @return
     */
    public static String crc32Str(String src) {
        CRC32 z = new CRC32();
        if (src != null) {
            z.update(src.getBytes());
        }
        long v = z.getValue();
        return padLeft(Long.toHexString(v), 8, "0");
    }

    /**
     * Объединениие коллекции через разделитель
     *
     * @param c     коллекция
     * @param delim разделитель
     * @return строка с раделителями
     */
    public static String join(Collection c, String delim) {
        StringBuilder sb = new StringBuilder();
        if (c != null) {
            for (Object o : c) {
                if (sb.length() != 0) {
                    sb.append(delim);
                }
                if (o != null) {
                    sb.append(o.toString());
                }
            }
        }
        return sb.toString();
    }

    /**
     * Шифрование строки в md5
     *
     * @param src исходная строка
     * @return md5 строка
     */
    public static String md5Str(String src) {
        try {
            if (src == null) {
                src = "";
            }
            MessageDigest md = MessageDigest.getInstance("MD5"); //NON-NLS
            md.reset();
            byte[] a = md.digest(src.getBytes(UTF8));
            return toHexString(a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Шифрование коллекции строк в md5
     *
     * @param src исходная коллекция строк, каждый элемент будет переведен в toString
     * @return md5 строка
     */
    public static String md5Str(Collection src) {
        try {
            if (src == null || src.size() == 0) {
                return md5Str("");
            }
            MessageDigest md = MessageDigest.getInstance("MD5"); //NON-NLS
            md.reset();
            for (Object a : src) {
                String s;
                if (a == null) {
                    s = "";
                } else {
                    s = a.toString();
                }
                md.update(s.getBytes(UTF8));
            }
            byte[] a = md.digest();
            return toHexString(a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Возвращает строки с указанным номер из строки. Разделитель строк '\n'
     *
     * @param s       откуда
     * @param lineNum номер строки (начиная с 0)
     * @return строка с указанным номером. Если номер строки за границами, возвращается пустая строка
     */
    public static String getLine(String s, int lineNum) {
        if (lineNum < 0) {
            return "";
        }
        if (empty(s)) {
            return "";
        }
        String[] ar = s.split("\n");
        if (ar.length <= lineNum) {
            return "";
        }
        return ar[lineNum];
    }

    /**
     * Кодировать бинарные данные в строку в формате BASE64
     *
     * @param data данные
     * @return строка в формате base64
     */
    public static String encodeBase64(byte[] data) {
        if (data == null) {
            return "";
        }
        return Base64.getMimeEncoder().encodeToString(data);
    }

    /**
     * Конвертировать строку в формате BASE64 в бинарные данные
     *
     * @param data строка в формате BASE64
     * @return бинарный эквивалент
     */
    public static byte[] decodeBase64(String data) {
        if (data == null) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(data);
    }

    /**
     * Простая конвертация в string
     */
    public static String toString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) return (String) value;
        return value.toString();
    }

    ////// substVar

    /**
     * Раскрытие подстановок ${x} обработчиком handler
     *
     * @param src      что раскрыть
     * @param startVar с чего начинаются переменные (например '${'). Должно быть 2 символа.
     * @param stopVar  чем заканчиваются переменные (например '}'). Должен быть 1 символ.
     * @param handler  чем раскрывать
     * @return строка с раскрытыми подстановками
     */
/*
    public static String substVar(String src, String startVar, String stopVar, ISubstVar handler) {
        if (src == null) {
            return "";
        }
        SubstVarParser p = new SubstVarParser(startVar.charAt(0), startVar.charAt(1),
                stopVar.charAt(0), handler);
        try {
            p.loadFrom(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return p.getResult();
    }
*/

    /**
     * Раскрытие подстановок ${x} обработчиком handler
     *
     * @return строка с раскрытыми подстановками
     * @see UtString#substVar(java.lang.String, java.lang.String, java.lang.String, ISubstVar)
     */
/*
    public static String substVar(String src, ISubstVar handler) {
        return substVar(src, "${", "}", handler);
    }
*/

    /**
     * Создание строки-разделителя вида '---[ message ]----------------'
     *
     * @param msg       текст в заголовке
     * @param len       длина строки
     * @param delimChar символ - разделитель
     */
    public static String delim(String msg, String delimChar, int len) {
        if (UtString.empty(msg)) {
            return UtString.repeat(delimChar, len);
        } else {
            msg = msg + " ]";
            return UtString.repeat(delimChar, 4) + "[ " + UtString.padRight(msg, len - 6, delimChar);
        }
    }

    /**
     * Найти номер строки по позиции
     *
     * @param src строк
     * @param pos позиция
     * @return номер строки с 0
     */
    public static int lineNum(String src, int pos) {
        if (src == null || src.length() == 0 || pos <= 0) {
            return 0;
        }
        if (pos >= src.length()) {
            pos = src.length() - 1;
        }
        int n = pos - 1; // если находимся на \n, то ее должны игнорировать
        int res = 0;
        while (n >= 0) {
            if (src.charAt(n) == '\n') {
                res++;
            }
            n--;
        }
        return res;
    }

    public static String format(String template, Map<String, Object> params) {
        // Создаем кастомный StringSubstitutor
        StringSubstitutor sub = new StringSubstitutor(params);

        // Поддерживаем разные типы скобок
        sub.setVariablePrefixMatcher(new MultiMatcher("$(", "${"));
        sub.setVariableSuffixMatcher(new MultiMatcher(")", "}"));

        //
        return sub.replace(template);
    }

    // Кастомный StringMatcher для работы с несколькими типами скобок
    static class MultiMatcher implements StringMatcher {
        private final String[] variants;

        public MultiMatcher(String... variants) {
            this.variants = variants;
        }

        @Override
        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            for (String variant : variants) {
                if (pos + variant.length() <= bufferEnd) {
                    boolean match = true;
                    for (int i = 0; i < variant.length(); i++) {
                        if (buffer[pos + i] != variant.charAt(i)) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return variant.length();
                    }
                }
            }
            return 0; // Нет совпадения
        }
    }

    public static String loadResource(String resourcePath) throws IOException {
        ClassLoader classLoader = UtString.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }

}

