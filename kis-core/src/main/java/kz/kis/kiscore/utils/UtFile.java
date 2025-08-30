package kz.kis.kiscore.utils;

import org.apache.commons.io.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

/**
 * Утилиты для работы с файлами и их именами.
 * Многие методы реализуются через commons-io.
 */
public class UtFile {

    private static int BUFSIZE = 8192;

    /**
     * Признак Windows
     */
    private static boolean osWindows = false;

    private static String _homedir = null;
    private static File _workdirFile = null;

    static {
        osWindows = (File.separatorChar == '\\');
    }


    ////// from Ant!

    /**
     * Gets path from a <code>List</code> of <code>String</code>s.
     *
     * @param pathStack <code>List</code> of <code>String</code>s to be concated
     *                  as a path.
     * @return <code>String</code>, never <code>null</code>
     * @since Ant 1.7
     */
    private static String internal_getPath(List pathStack) {
        // can safely use '/' because Windows understands '/' as separator
        return internal_getPath(pathStack, '/');
    }

    /**
     * Gets path from a <code>List</code> of <code>String</code>s.
     *
     * @param pathStack     <code>List</code> of <code>String</code>s to be concated
     *                      as a path.
     * @param separatorChar <code>char</code> to be used as separator between names in
     *                      path
     * @return <code>String</code>, never <code>null</code>
     */
    private static String internal_getPath(final List pathStack, final char separatorChar) {
        final StringBuilder buffer = new StringBuilder();

        final Iterator iter = pathStack.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
        }

        while (iter.hasNext()) {
            buffer.append(separatorChar);
            buffer.append(iter.next());
        }

        return buffer.toString();
    }

    /**
     * Gets all names of the path as an array of <code>String</code>s.
     *
     * @param path to get names from
     * @return <code>String</code>s, never <code>null</code>
     */
    private static String[] internal_getPathStack(String path) {
        String normalizedPath = path.replace(File.separatorChar, '/');
        return normalizedPath.split("/");
    }


    /**
     * Получение относительного пути между файлами.
     *
     * @param fromFile для какого файла получаем относительный
     * @param toFile   относительно какого файла получаем относительный путь
     * @return относительный путь
     */
    public static String getRelativePath(File fromFile, File toFile) throws Exception {
        String fromPath = fromFile.getCanonicalPath();
        String toPath = toFile.getCanonicalPath();

        // build the path stack info to compare
        String[] fromPathStack = internal_getPathStack(fromPath);
        String[] toPathStack = internal_getPathStack(toPath);

        if (0 < toPathStack.length && 0 < fromPathStack.length) {
            if (!fromPathStack[0].equals(toPathStack[0])) {
                // not the same device (would be "" on Linux/Unix)
                return internal_getPath(Arrays.asList(toPathStack));
            }
        } else {
            // no comparison possible
            return internal_getPath(Arrays.asList(toPathStack));
        }

        int minLength = Math
                .min(fromPathStack.length, toPathStack.length);

        int same = 1;

        // get index of parts which are equal
        for (; same < minLength; same++) {
            if (!fromPathStack[same].equals(toPathStack[same])) {
                break;
            }
        }

        List<String> relativePathStack = new ArrayList<String>();

        // if "from" part is longer, fill it up with ".."
        // to reach path which is equal to both paths
        for (int i = same; i < fromPathStack.length; i++) {
            relativePathStack.add("..");
        }

        // fill it up path with parts which were not equal
        for (int i = same; i < toPathStack.length; i++) {
            relativePathStack.add(toPathStack[i]);
        }

        return internal_getPath(relativePathStack);
    }

    /**
     * Получение относительного пути между файлами.
     *
     * @param fromFile для какого файла получаем относительный
     * @param toFile   относительно какого файла получаем относительный путь
     * @return относительный путь
     */
    public static String getRelativePath(String fromFile, String toFile) throws Exception {
        return getRelativePath(new File(fromFile), new File(toFile));
    }

    //////

    /**
     * Преобразование в абсолютный путь
     */
    public static String abs(String file) {
        return unnormPath(new File(file).getAbsolutePath());
    }

    /**
     * Преобразование в абсолютный и каноничный путь
     */
    public static String absCanonical(String file) {
        try {
            return unnormPath(new File(file).getCanonicalPath());
        } catch (Exception e) {
            return abs(file);
        }
    }

    /**
     * Удалить расширение
     */
    public static String removeExt(String path) {
        return FilenameUtils.removeExtension(path);
    }

    /**
     * Существует ли файл
     *
     * @param file           имя файла
     * @param errorNotExists true-генерить ошибку, если файл не существует
     */
    public static boolean exists(String file, boolean errorNotExists) {
        File f = new File(file);
        boolean b = f.exists();
        if (!b && errorNotExists) {
            throw new RuntimeException("File [" + file + "] not found");
        }
        return b;
    }

    /**
     * Существует ли файл
     *
     * @param file имя файла
     */
    public static boolean exists(String file) {
        return exists(file, false);
    }

    /**
     * Существует ли файл
     *
     * @param file           файл
     * @param errorNotExists true-генерить ошибку, если файл не существует
     */
    public static boolean exists(File file, boolean errorNotExists) {
        boolean b = file.exists();
        if (!b && errorNotExists) {
            throw new RuntimeException("File [" + file + "] not found");
        }
        return b;
    }

    /**
     * Существует ли файл
     *
     * @param file файл
     */
    public static boolean exists(File file) {
        return exists(file, false);
    }

    /**
     * Присоеденить к пути имя файла или другой путь
     */
    public static String join(String dir, String... files) {
        if (dir.contains("://")) {
            // это vfs!
            for (String f : files) {
                if (!(dir.endsWith("\\") || dir.endsWith("/"))) {
                    dir = dir + "/";
                }
                String f1 = f;
                while (f1.startsWith("/") || f1.startsWith("\\")) {
                    f1 = f1.substring(1);
                }
                dir = dir + f1;
            }
        } else {
            for (String f : files) {
                dir = FilenameUtils.concat(dir, f);
            }
        }
        return dir;
    }

    /**
     * Присоеденить к пути имя файла или другой путь
     */
    public static String join(File dir, String file) {
        return FilenameUtils.concat(dir.getAbsolutePath(), file);
    }

    /**
     * Получить имя файла из пути (с расширением)
     */
    public static String fileName(String path) {
        return FilenameUtils.getName(path);
    }

    /**
     * Получить имя каталога в котором лежит файл.
     * Для пути 'c:\pf\dir1\file1.txt' возвращает 'dir1'
     */
    public static String dir(String path) {
        return FilenameUtils.getName(FilenameUtils.getPathNoEndSeparator(path));
    }

    /**
     * Базовое имя (имя файла без расширения)
     */
    public static String basename(String path) {
        return FilenameUtils.getBaseName(unnormPath(path));
    }

    /**
     * Расширение файла (без точки)
     */
    public static String ext(String path) {
        return FilenameUtils.getExtension(unnormPath(path));
    }

    /**
     * Путь. Без слеша в конце
     */
    public static String path(String path) {
        return unnormPath(FilenameUtils.getFullPath(unnormPath(path)));
    }

    /**
     * Удаление разделителя в конце пути, если он есть
     */
    public static String unnormPath(String path) {
        if (path == null) {
            return "";
        }
        if (path.endsWith("/") || path.endsWith("\\")) {
            return unnormPath(path.substring(0, path.length() - 1));
        }
        return path;
    }

    /**
     * true - если путь абсолютный
     */
    public static boolean isAbsolute(String path) {
        if (path.startsWith("res:")) { //NON-NLS
            return true;
        }
        if (isWindows() && path.startsWith("/")) {
            // случай: '/c:/temp/file.ext'
            path = path.substring(1);
        }
        String pfx = FilenameUtils.getPrefix(path);
        if (pfx == null) {
            return false;
        }
        int len = pfx.length();
        if (len > 0) {
            if (osWindows) {
                if (pfx.length() < 3) {
                    if (pfx.equals("\\\\")) {
                        return true;
                    }
                    return false;
                }
            }
            return true;
        } else {
            if (path.indexOf(':') != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * true - если path это файл
     */
    public static boolean isFile(String path) {
        File f = new File(path);
        return f.isFile();
    }

    /**
     * true - если path это каталог
     */
    public static boolean isDir(String path) {
        File f = new File(path);
        return f.isDirectory();
    }

    /////

    /**
     * Очищает файл. Файл удаляется. Каталог, в котором файл находится - создается.
     *
     * @param path
     */
    public static void cleanFile(String path) {
        if (UtString.empty(path)) {
            return;
        }
        cleanFile(new File(path));
    }

    /**
     * Очищает файл. Файл удаляется. Каталог, в котором файл находится - создается.
     *
     * @param f файл
     */
    public static void cleanFile(File f) {
        f = f.getAbsoluteFile();
        File fp = f.getParentFile();
        if (!fp.exists()) {
            fp.mkdirs();
        }
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * Очистить каталог. Каталог после выполнения функции существует и пустой
     *
     * @param f
     */
    public static void cleanDir(File f) {
        f = f.getAbsoluteFile();
        if (!f.exists()) {
            f.mkdirs();
        }
        try {
            FileUtils.cleanDirectory(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Очистить каталог. Каталог после выполнения функции существует и пустой
     *
     * @param path путь до каталога
     */
    public static void cleanDir(String path) {
        if (UtString.empty(path)) {
            return;
        }
        cleanDir(new File(path));
    }

    /**
     * Создат каталоги (включая вложенные)
     */
    public static void mkdirs(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    //////

    /**
     * Проверка, что имя файла совпадает с маской.
     * см. org.apache.commons.io.FilenameUtils#wildcardMatch(java.lang.String, java.lang.String)
     */
    public static boolean wildcardMatch(String fileName, String wildcardMatcher) {
        return FilenameUtils.wildcardMatch(fileName, wildcardMatcher);
    }

    /**
     * Проверка, что имя файла совпадает с маской.
     * см. org.apache.commons.io.FilenameUtils#wildcardMatch(java.lang.String, java.lang.String)
     */
    public static boolean wildcardMatch(String fileName, String wildcardMatcher, IOCase caseSensitivity) {
        return FilenameUtils.wildcardMatch(fileName, wildcardMatcher, caseSensitivity);
    }

    /**
     * Проверка, что имя файла совпадает с маской.
     * см. org.apache.commons.io.FilenameUtils#wildcardMatchOnSystem(java.lang.String, java.lang.String)
     */
    public static boolean wildcardMatchOnSystem(String fileName, String wildcardMatcher) {
        return FilenameUtils.wildcardMatchOnSystem(fileName, wildcardMatcher);
    }

    //////

    /**
     * Рабочий каталог (System.getProperty("user.dir"))
     */
    public static String getWorkdir() {
        return System.getProperty("user.dir");
    }

    /**
     * Возвращает домашний каталог пользователя.
     * Под Windows ипользует переменную среды USERPROFILE
     */
    public static String getHomedir() {
        if (_homedir == null) {
            String s = System.getProperty("user.home");
            if (isWindows()) {
                String s1 = System.getenv().get("USERPROFILE");
                if (s1 != null) {
                    File f = new File(s1);
                    if (f.exists() && f.isDirectory()) {
                        s = s1;
                    }
                }
            }
            _homedir = s;
        }
        return _homedir;
    }

    protected static File getWorkdirFile() {
        if (_workdirFile == null) {
            _workdirFile = new File(getWorkdir());
        }
        return _workdirFile;
    }

    /**
     * Системный временный каталог (System.getProperty("java.io.tmpdir"))
     */
    public static String getTempdir() {
        return unnormPath(System.getProperty("java.io.tmpdir"));
    }


    //////

    /**
     * Найти файл fileName начиная с fromPath и вверх. Если не найден, возвращает null.
     */
    public static String findFileUp(String fileName, String fromPath) {
        if (fromPath == null) {
            fromPath = "";
        }
        File f = new File(fromPath).getAbsoluteFile();
        while (f != null) {
            File f1 = new File(f, fileName);
            if (f1.exists()) {
                return f1.getAbsolutePath();
            }
            f = f.getParentFile();
        }
        return null;
    }

    //////

    /**
     * Кописрование потока в другой поток. Потоки после использования не закрываются.
     *
     * @param src исходный поток
     * @param dst куда копировать
     */
    public static void copyStream(InputStream src, OutputStream dst) throws Exception {
        BufferedInputStream fSrc = new BufferedInputStream(src);
        BufferedOutputStream fDst = new BufferedOutputStream(dst);
        byte[] b = new byte[BUFSIZE];
        int n;
        while ((n = fSrc.read(b)) > 0) {
            fDst.write(b, 0, n);
        }
        fDst.flush();
    }

    /**
     * Запись потока в файл
     *
     * @param src исходный поток (закрывается после окончания копирования)
     * @param dst куда копировать (закрывается после окончания копирования)
     */
    public static void copyStream(InputStream src, File dst) throws Exception {
        BufferedInputStream fSrc = null;
        BufferedOutputStream fDst = null;
        try {
            fSrc = new BufferedInputStream(src);
            fDst = new BufferedOutputStream(new FileOutputStream(dst));
            byte[] b = new byte[BUFSIZE];
            int n;
            while ((n = fSrc.read(b)) > 0) {
                fDst.write(b, 0, n);
            }
        } finally {
            if (fSrc != null) {
                fSrc.close();
            }
            if (fDst != null) {
                fDst.close();
            }
        }
    }

    ////// load string

    /**
     * Загрузить строку из файла в кодировке utf-8
     *
     * @param fileName откуда
     * @return загруженная строка
     */
    public static String loadString(String fileName) throws Exception {
        return loadString(fileName, Charset.defaultCharset().toString());
    }

    /**
     * Загрузить строку из файла в указанной кодировке
     *
     * @param fileName откуда
     * @param charset  кодировка
     * @return загруженная строка
     */
    public static String loadString(String fileName, String charset) throws Exception {
        return loadString(new File(fileName), charset);
    }

    /**
     * Загрузить строку из файла в указанной кодировке
     *
     * @param file    откуда
     * @param charset кодировка
     * @return загруженная строка
     */
    public static String loadString(File file, String charset) throws Exception {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, Charset.forName(charset));
    }

    /**
     * Загрузить строку из файла в кодировке utf-8
     *
     * @param file откуда
     * @return загруженная строка
     */
    public static String loadString(File file) throws Exception {
        return loadString(file, Charset.defaultCharset().toString());
    }

    ////// save string

    /**
     * Записать строку в файл в указанной кодировке
     *
     * @param s       что
     * @param file    куда
     * @param charset кодировка
     * @return загруженная строка
     */
    public static void saveString(String s, File file, String charset) throws Exception {
        mkdirs(file.getParent());
        //
        try (Writer wr = new FileWriter(file, Charset.forName(charset))) {
            wr.write(s);
        }
    }

    /**
     * Записать строку в файл
     *
     * @param s    что
     * @param file куда
     * @return загруженная строка
     */
    public static void saveString(String s, File file) throws Exception {
        saveString(s, file, Charset.defaultCharset().toString());
    }

    //////

    /**
     * true, если работаем на windows
     */
    public static boolean isWindows() {
        return osWindows;
    }

    ////// DirScanner

    /**
     * Разделить путь на путь и маску.
     * Например:
     * <pre>{@code
     * d:\t\*.java        -> path=d:\t, mask=*.java
     * d:\t\**\*.java     -> path=d:\t, mask=**\*.java
     * d:\t               -> path=d:\t, mask=DEFAULT
     * }</pre>
     * Перед выполнением очищается список include.
     *
     * @param path        путь,возможно с маской
     * @param defaultMask маска по умолчанию, если явно в path не определена
     * @return массив из 2-х элеменов. [0]-путь,[1]-маска
     */
    public static String[] splitPathAndMask(String path, String defaultMask) {
        if (path == null) {
            path = "";
        }
        String[] res = new String[2];
        res[0] = "";
        res[1] = defaultMask;

        int a = path.indexOf('*');
        int b = path.indexOf('?');
        if (a == -1 && b == -1) {
            res[0] = path;
        } else {
            if (a == -1) {
                a = b;
            } else if (b != -1) {
                a = Math.min(a, b);
            }
            // a - минимальная позиция ? или *
            if (a == 0) {
                res[1] = path;
            } else {
                b = path.lastIndexOf('/', a);
                a = path.lastIndexOf('\\', a);
                if (a == -1 && b == -1) {
                    res[1] = path;
                } else {
                    if (a == -1) {
                        a = b;
                    } else if (b != -1) {
                        a = Math.max(a, b);
                    }
                    res[0] = path.substring(0, a);
                    res[1] = path.substring(a + 1);
                }
            }
        }
        return res;
    }

    public static String getMd5File(File file) throws Exception {
        int buffSize = 1024 * 10;
        byte[] buffer = new byte[buffSize];

        try (
                FileInputStream inputStream = new FileInputStream(file)
        ) {
            MessageDigest md = MessageDigest.getInstance("MD5"); //NON-NLS

            //
            md.reset();
            //
            while (inputStream.available() > 0) {
                int n = inputStream.read(buffer);
                md.update(buffer, 0, n);
            }
            //
            byte[] a = md.digest();

            //
            return UtString.toHexString(a);
        }
    }

}

