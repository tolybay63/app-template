package kz.kis.kisfilestorage.utils;

import kz.kis.kisfilestorage.exceptions.*;
import org.springframework.stereotype.*;

@Component
public class ValidatorFileStorage {
    public static void validateName(String name, int length) {
        if (name.length() > length) {
            throw new ValidateException("Наименование не должно превышать 255 символов");
        }
    }
}