package com.management.logging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.management.util.IdGenerator;
import com.management.util.LogUtil;
import com.management.util.LoggingUtil;
import java.util.Objects;
import java.util.StringJoiner;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * This class contains methods that executes logging process at some points of a program.
 * Main purpose of this class is to have some generalized solutions for
 * logging at some places that have common patterns.
 * Frequently used annotations, exceptions can be an example for these patterns.
 */
@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class LoggingAspect {

    /**
     * Reference to the object mapper defined during instantiation.
     */
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapperForLogging();

    /**
     * Creates a new instance of <code>ObjectMapper</code> with modified
     * configuration.
     *
     * @return <code>ObjectMapper</code> that modified according to requirements
     */
    public static ObjectMapper createObjectMapperForLogging() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new LoggingJacksonAnnotationIntrospector());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }

    /**
     * Pointcut method for all controller methods.
     */
    @Pointcut("@within(io.swagger.annotations.Api)")
    protected void apiMethods() {
        // Pointcut for all controller methods
    }

    /**
     * Pointcut method for all service layer methods.
     */
    @Pointcut("@within(org.springframework.stereotype.Service)")
    protected void allServiceMethods() {
        // This point cut is for all service layer methods
    }

    /**
     * Pointcut method for all repository layer methods.
     */
    @Pointcut("@within(org.springframework.stereotype.Repository)")
    protected void allRepositoryMethods() {
        // This point cut is for all repository layer methods
    }

    /**
     * This method logs around service or repository methods whenever they throw an exception.
     * It logs exception based on stack trace of the thrown exception and method details.
     *
     * @param joinPoint point of execution
     * @param throwable that was thrown
     */
    @AfterThrowing(pointcut = "allServiceMethods() || allRepositoryMethods()", throwing = "throwable")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable throwable) {

        throwable = Objects.nonNull(throwable.getCause()) ? throwable.getCause() : throwable;

        StackTraceElement stackTraceElement = null;

        if (Objects.nonNull(throwable.getStackTrace())
                && throwable.getStackTrace().length > 0) {
            stackTraceElement = throwable.getStackTrace()[0];
        }

        StringJoiner result = new StringJoiner(",");
        joinPoint.getClass();
        result.add("{\"method_name\":" + "\"" + joinPoint.getSignature().getName() + "\"")
                .add("\"exception_type\":" + "\"" + throwable.getClass() + "\"")
                .add("\"message\":" + "\"" + throwable.getMessage() + "\"");

        if (Objects.nonNull(stackTraceElement)) {
            result.add("\"line_number\":" + "\"" + stackTraceElement.getLineNumber() + "\"")
                    .add("\"class_name\":" + "\"" + stackTraceElement.getClassName() + "\"" + "}")
                    .add("\"method_name\":" + "\"" + stackTraceElement.getMethodName() + "\"" + "}");
        }
        log.error(result.toString());
    }

    /**
     * This method logs around methods which have <code>Api</code> or relevant annotation.
     * It uses user credentials, method request and response for creating log message.
     *
     * @param joinPoint point of execution
     * @return response returned by the method
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("apiMethods()")
    public Object logAroundApiMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        MDC.put("GUID", IdGenerator.generateOid());
        try {
            return executeJoinPoint(joinPoint);
        } finally {
            MDC.clear();
        }

    }

    private Object executeJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String methodName = codeSignature.getName();

        StringJoiner generatedLog = new StringJoiner(" , ");
        generatedLog.add("{\"request\":\"" + methodName + "\"}");

        Object[] methodArguments = joinPoint.getArgs();

        for (int i = 0; i < methodArguments.length; i++) {

            if (methodArguments[i] == null) {
                continue;
            }

            Class<?> argumentClass = methodArguments[i].getClass();

            if (LoggingUtil.isWrapperType(argumentClass)) {

                String argumentName = codeSignature.getParameterNames()[i];

                if (!LogUtil.EXCLUSIONS.contains(argumentName)) {
                    generatedLog.add("{\"" + argumentName + "\":" + methodArguments[i] + "}");
                }

            } else {

                Log logAnnotation = LogUtil.getLogAnnotationFromClass(argumentClass);

                if (Objects.nonNull(logAnnotation)) {

                    String argumentAsJson = OBJECT_MAPPER.writeValueAsString(methodArguments[i]);
                    generatedLog.add(argumentAsJson);

                }
            }
        }

        log.info(generatedLog.toString());

        Object response = joinPoint.proceed();

        generatedLog = new StringJoiner(" , ");
        generatedLog.add("{\"response\":\"" + methodName + "\"}");

        generatedLog.add(OBJECT_MAPPER.writeValueAsString(response));
        log.info(generatedLog.toString());
        return response;
    }


}
