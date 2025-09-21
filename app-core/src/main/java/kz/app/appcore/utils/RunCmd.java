package kz.app.appcore.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Запуск консольной команды.
 * Возможен захват вывода.
 * Использование: присвойте параметры (set-методы), выполните run, получите результат
 * (get-методы).
 */
public class RunCmd {

    private List<String> cmd = new ArrayList<>();
    private String dir;
    private String charset;
    private boolean showout = true;
    private boolean saveout = false;

    private int exitCode;
    private List<String> out = new ArrayList<>();
    private List<String> params = new ArrayList<>();
    private Map<String, String> env = new LinkedHashMap<>();

    /**
     * Установить команду для выполнения в виде списка параметром.
     */
    public void setCmd(List<String> cmd) {
        this.cmd.clear();
        if (cmd != null) {
            this.cmd.addAll(cmd);
        }
    }

    /**
     * Установить команду для выполнения в виде строки, разделенной пробелами
     */
    public void setCmd(String cmd) {
        if (cmd == null) {
            return;
        }
        cmd = cmd.trim();
        if (UtString.empty(cmd)) {
            return;
        }
        String[] ar = cmd.split("\\s+");
        setCmd(Arrays.asList(ar));
    }

    public List<String> getCmd() {
        return cmd;
    }

    /**
     * Каталог запуска. Если не указан, используется текущий рабочий каталог.
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    /**
     * Кодировка консоли.
     * По умолчанию совпадает с той, в которой запущено java-приложение.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    /**
     * Показывать ли вывод на консоль.
     * По умолчанию - true.
     */
    public void setShowout(boolean showout) {
        this.showout = showout;
    }

    public boolean isShowout() {
        return showout;
    }

    /**
     * Сохранять ли вывод на консоль.
     * По умолчанию - false.
     */
    public void setSaveout(boolean saveout) {
        this.saveout = saveout;
    }

    public boolean isSaveout() {
        return saveout;
    }

    //////

    /**
     * Код возврата.
     * Доступно после выполнения.
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Захваченный вывод консоли, если setShowout установлено в true.
     * Доступно после выполнения.
     */
    public List<String> getOut() {
        return out;
    }

    /**
     * Фактические параметры запуска процесса.
     * Например для windows cmd будет предварятся 'cmd.exe /c'.
     * Доступно после выполнения.
     */
    public List<String> getParams() {
        return params;
    }

    /**
     * Переменные среды для перекрытия.
     */
    public Map<String, String> getEnv() {
        return env;
    }

    //////

    public void run() throws Exception {
        this.out = new ArrayList<>();
        this.params = new ArrayList<>();
        this.exitCode = 0;

        //
        if (this.cmd.size() == 0) {
            throw new Exception("cmd не задана");
        }

        //
        if (UtFile.isWindows()) {
            this.params.add("cmd.exe");
            this.params.add("/C");
        }
        this.params.addAll(this.cmd);

        //
        String dir = this.dir;
        if (UtString.empty(dir)) {
            dir = UtFile.getWorkdir();
        }

        //
        ProcessBuilder pb = new ProcessBuilder(this.params);
        pb.directory(new File(dir));
        if (this.env.size() > 0) {
            pb.environment().putAll(this.env);
        }

        Process pr;

        if (this.showout && !this.saveout) {
            // особый случай, как в обычной консоли
            pb.inheritIO();
            pr = pb.start();

        } else {
            String charset = UtString.empty(this.charset) ? getConsoleCharset() : this.charset;
            pb.redirectErrorStream(true);
            pr = pb.start();
            BufferedReader inr = new BufferedReader(new InputStreamReader(pr.getInputStream(), charset));
            String line = inr.readLine();
            while (line != null) {
                if (this.showout) {
                    System.out.println(line);
                }
                if (saveout) {
                    this.out.add(line);
                }
                line = inr.readLine();
            }
        }

        pr.waitFor();
        this.exitCode = pr.exitValue();
    }

    /**
     * Текущая кодировка консоли.
     * Если сейчас вывод перенаправлен в файл, возвращает правильную кодировку.
     */
    public static String getConsoleCharset() {
        String enc = System.getProperty("sun.stdout.encoding");
        if (UtString.empty(enc)) {
            enc = Charset.defaultCharset().name();
        }
        return enc;
    }

}
