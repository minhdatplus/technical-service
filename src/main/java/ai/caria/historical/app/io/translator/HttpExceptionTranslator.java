package ai.caria.historical.app.io.translator;

import ai.caria.historical.commons.exceptions.HistoricalException;
import ai.caria.historical.message.HistoricalRequest;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.pojo.helper.BiFunctionAccessor;
import io.gridgo.utils.pojo.helper.MethodAccessors;
import lombok.extern.slf4j.Slf4j;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static io.gridgo.connector.httpcommon.HttpCommonConstants.HEADER_STATUS;

@Slf4j
public class HttpExceptionTranslator implements ExceptionTranslator {
    private Map<Class<? extends Throwable>, BiFunctionAccessor> factory = new NonBlockingHashMap<>();

    public HttpExceptionTranslator() {
        var methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!isValidAnnotatedMethod(method)) continue;

            var annotation = method.getAnnotation(ExceptionHandler.class);
            var functionAccessor = MethodAccessors.forStaticTwoParamsFunction(method);
            var values = annotation.values();
            for (Class<? extends Throwable> value : values) {
                factory.put(value, functionAccessor);
            }
        }
    }

    private boolean isValidAnnotatedMethod(Method method) {
        var modifiers = method.getModifiers();
        return method.isAnnotationPresent(ExceptionHandler.class)
                && Modifier.isStatic(modifiers)
                && Modifier.isPublic(modifiers);
    }

    @ExceptionHandler(values = {HistoricalException.class})
    public static Message HistoricalException(HistoricalRequest request, HistoricalException exception) {
        int code = exception.getCode();
        String exMessage = exception.getMessage();

        Message message = Message.ofAny(buildErrorResponse(code, exMessage));
        message.getPayload().addHeader(HEADER_STATUS, 500);

        return message;
    }

    private static BElement buildErrorResponse(int code, String message) {
        return BObject.ofSequence("code", code, "message", message);
    }

    @Override
    public Message translate(HistoricalRequest request, Exception ex) {
        log.debug("Exception handling request", ex);
        var handler = factory.get(ex.getClass());
        if (handler == null) {
            var cause = ex.getCause();
            if (cause != null) {
                handler = factory.get(cause.getClass());
            }
        }

        if (handler == null) handler = factory.get(Exception.class);

        return (Message) handler.apply(request, ex);
    }
}
