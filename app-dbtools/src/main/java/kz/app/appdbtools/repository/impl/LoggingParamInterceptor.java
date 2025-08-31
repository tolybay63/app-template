package kz.app.appdbtools.repository.impl;

import kz.app.appdbtools.repository.SqlParamInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoggingParamInterceptor implements SqlParamInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingParamInterceptor.class);
    @Override
    public Map<String, Object> modifyParams(String sql, Map<String, Object> originalParams) {
        logger.debug("SQL: {}", sql);
        logger.debug("Params: {}", originalParams);
        return originalParams;
    }
}