package kz.kis.kisocr;

import kz.kis.kiscore.model.*;

import java.io.*;
import java.util.*;

public interface IOCRProcessor {

    List<FileData> processFile(File file) throws Exception;

    void close();

}
