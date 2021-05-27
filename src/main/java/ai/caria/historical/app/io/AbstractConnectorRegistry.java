package ai.caria.historical.app.io;

import ai.caria.historical.app.io.translator.ExceptionTranslator;
import ai.caria.historical.app.io.translator.RequestTranslator;
import ai.caria.historical.app.io.translator.ResponseTranslator;
import ai.caria.historical.core.handler.HistoricalHandler;
import ai.caria.historical.message.HistoricalRequest;
import io.gridgo.connector.Connector;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.framework.impl.NonameComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.helper.Loggable;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joo.promise4j.Deferred;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PROTECTED)
@Slf4j
public abstract class AbstractConnectorRegistry extends NonameComponentLifecycle implements Loggable {
    @NonNull
    private HistoricalHandler bjHandler;

    @NonNull
    private String httpEndpoint;

    private List<Connector> connectors;

    @NonNull
    private Class<? extends Connectors> connectorRegistry;

    @NonNull
    private RequestTranslator requestTranslator;

    @NonNull
    private ResponseTranslator responseTranslator;

    @NonNull
    private ExceptionTranslator exceptionTranslator;

    @Override
    protected void onStart() {
        var declaredMethods = connectorRegistry.getDeclaredMethods();
        var factory = new DefaultConnectorFactory();
        connectors = new ArrayList<>();
        for (var method : declaredMethods) {
            if (!isValidAnnotated(method)) continue;
            var annotation = method.getAnnotation(Endpoint.class);
            String endpoint = endPoint(httpEndpoint, annotation);
            getLogger().debug("Register endpoint: {}", endpoint);
            var connector = factory.createConnector(endpoint);
            connector.start();
            if (connector.getConsumer().isEmpty()) continue;

            var consumer = connector.getConsumer().get();
            consumer.subscribe(((message, deferred) -> onRequest(message, deferred, annotation.request())));
            connectors.add(connector);
        }
    }

    @Override
    protected void onStop() {
        connectors.forEach(Connector::stop);
    }

    private String endPoint(String httpEndpoint, Endpoint annotation) {
        var sb = new StringBuilder(httpEndpoint);
        sb.append("/[");
        sb.append(annotation.path());
        sb.append("]");
        sb.append("?method=");
        sb.append(annotation.method().asString());
        if (StringUtils.isNotBlank(annotation.prometheusPrefix())) {
            sb.append("&enablePrometheus=true&prometheusPrefix=");
            sb.append(annotation.prometheusPrefix());
        }

        return sb.toString();
    }

    private boolean isValidAnnotated(Method method) {
        var modifiers = method.getModifiers();
        return method.isAnnotationPresent(Endpoint.class)
                && !Modifier.isStatic(modifiers)
                && Modifier.isPublic(modifiers);
    }

    private void onRequest(Message message, Deferred<Message, Exception> deferred, Class<? extends HistoricalRequest> type) {
        var request = requestTranslator.translate(message, type);
        bjHandler.handle(request)
                .done(res -> deferred.resolve(responseTranslator.translate(res)))
                .fail(ex -> deferred.resolve(exceptionTranslator.translate(request, ex)));
    }
}
