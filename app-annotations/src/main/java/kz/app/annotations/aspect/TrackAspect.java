package kz.app.annotations.aspect;

import kz.app.annotations.constant.*;
import kz.app.annotations.constant.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.sql.*;
import java.time.*;
import java.util.*;

import static kz.app.annotations.utils.AspectUtils.*;

@Aspect
@Component
public class TrackAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger("app.track");

    @Around("@annotation(kz.app.annotations.annotation.Track)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        String className = joinPoint.getTarget().getClass().getName();

        String stackPoint = className + "." + methodName;

        long fullTime = 0L;

        String traceId;
        if (MDC.get(MDCConstant.TRACE_ID) == null) {

            traceId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);

            MDC.put(MDCConstant.TRACE_ID, traceId);

            MDC.put(MDCConstant.STACK_START, stackPoint);

            LOGGER.info("{} >>>>>>>>>>>> stack start", traceId);
        } else {

            traceId = MDC.get(MDCConstant.TRACE_ID);
        }

        Object proceed;

        try {

            LocalDateTime start = LocalDateTime.now();

            Object[] args = joinPoint.getArgs();

            String strArguments = argsToString(args);

            LOGGER.info("{} started {}", traceId, stackPoint);

            for (String arg : strArguments.split("---end---")) {
                LOGGER.info("{}    arg: {}", traceId, arg);
            }

            proceed = joinPoint.proceed();

            LocalDateTime end = LocalDateTime.now();

            fullTime = Timestamp.valueOf(end).getTime() - Timestamp.valueOf(start).getTime();

            LOGGER.info("{} finished {} in {} ms", traceId, stackPoint, fullTime);
        } finally {
            String stackStart = MDC.get(MDCConstant.STACK_START);

            if (stackStart.equals(stackPoint)) {
                LOGGER.info("{} ------------ stack end, total duration: {} ms", traceId, fullTime);

                MDC.remove(MDCConstant.TRACE_ID);

                MDC.remove(MDCConstant.STACK_START);
            }
        }

        return proceed;
    }
}