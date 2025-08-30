package kz.kis.kisdbtools.repository;

import java.util.Map;

public interface SqlParamInterceptor {
    Map<String, Object> modifyParams(String sql, Map<String, Object> originalParams);
}
