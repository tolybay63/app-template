package kz.app.annotations.utils;

import com.fasterxml.jackson.databind.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

@Service
public class AspectUtils {
    public static Method findMethodByName(Method[] methods, String name) {
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }

        return null;
    }

    // Аргументы методов переводит в строку
    public static String argsToString(Object[] args) {

        StringBuilder strArgs = new StringBuilder();

        ObjectMapper mapper = new ObjectMapper();

        String argEnd = "---end---";

        for (Object arg : args) {
            try {
                if (arg == null) {
                    strArgs.append("<null>").append(argEnd);
                } else if (arg instanceof HttpServletResponse) {
                    strArgs.append(arg.getClass().getSimpleName()).append(argEnd);
                } else if (arg instanceof HttpServletRequest request) {
                    strArgs.append("HttpServletRequest body: ").append(request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))).append(argEnd);
                } else if (arg instanceof Map) {
                    String json = mapper.writeValueAsString(arg);
                    strArgs.append("Map: ").append(json).append(argEnd);
                } else if (arg instanceof Collection) {
                    String json = mapper.writeValueAsString(arg);
                    strArgs.append("Collection: ").append(json).append(argEnd);
                } else if (arg instanceof MultipartFile file) {
                    strArgs.append(arg.getClass().getSimpleName()).append(": ").append(file.getOriginalFilename()).append(argEnd);
                } else {
                    String json = mapper.writeValueAsString(arg);
                    strArgs.append(arg.getClass().getSimpleName()).append(": ").append(json).append(argEnd);
                }
            } catch (Exception e) {
                strArgs.append(arg.getClass().getSimpleName()).append(argEnd);
            }
        }

        return strArgs.toString().trim();
    }
}