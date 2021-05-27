package ai.caria.historical.app.io.translator.app;

import ai.caria.historical.app.io.translator.Request;
import ai.caria.historical.app.io.translator.RequestTranslator;
import ai.caria.historical.commons.datetime.DateTimeUtils;
import ai.caria.historical.message.HistoricalRequest;
import ai.caria.historical.message.impl.AdjustedRateRequest;
import ai.caria.historical.message.impl.StockDataRequest;
import ai.caria.historical.message.impl.SwaggerRequest;
import io.gridgo.bean.BObject;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.pojo.helper.FunctionAccessor;
import io.gridgo.utils.pojo.helper.MethodAccessors;
import lombok.extern.slf4j.Slf4j;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

@Slf4j
public class HistoricalHttpRequestTranslator implements RequestTranslator {

    private Map<Class<? extends HistoricalRequest>, FunctionAccessor> factory = new NonBlockingHashMap<>();

    public HistoricalHttpRequestTranslator() {
        var methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!isValidAnnotatedMethod(method)) continue;

            var annotation = method.getAnnotation(Request.class);
            var functionAccessor = MethodAccessors.forStaticSingleParamFunction(method);
            factory.put(annotation.value(), functionAccessor);
        }
    }

    private boolean isValidAnnotatedMethod(Method method) {
        return method.isAnnotationPresent(Request.class)
                && Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers());
    }

    @Request(AdjustedRateRequest.class)
    public static HistoricalRequest adjustedRequest(Message message) {
        var body = body(message);

        return AdjustedRateRequest.builder()
                .fromDate(DateTimeUtils.date(body.getString("from_date")))
                .toDate(DateTimeUtils.date(body.getString("to_date")))
                .build();
    }
// interval > 24*60
    @Request(StockDataRequest.class)
    public static HistoricalRequest dailyStockInfo(Message message) {
        var body = body(message);

        return StockDataRequest.builder()
                .symbol(body.getString("symbol"))
                .from(body.getString("from"))
                .to(body.getString("to"))
                .interval(Double.parseDouble(body.getString("interval")))
                .build();
    }

    @Override
    public HistoricalRequest translate(Message message, Class<? extends HistoricalRequest> target) {
        return (HistoricalRequest) factory.get(target).apply(message);
    }

    @Request(SwaggerRequest.class)
    public static HistoricalRequest swagger(Message message) {
        return new SwaggerRequest();
    }

    private static BObject body(Message message) {
        return message.body().asObject();
    }
}

