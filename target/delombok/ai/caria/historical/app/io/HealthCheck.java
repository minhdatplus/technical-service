// Generated by delombok at Wed May 26 23:25:48 ICT 2021
package ai.caria.historical.app.io;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.impl.resolvers.ClasspathConnectorResolver;
import io.gridgo.framework.impl.NonameComponentLifecycle;
import io.gridgo.framework.support.Message;
import lombok.NonNull;
import org.joo.promise4j.Deferred;
import java.io.IOException;
import java.io.StringWriter;

public class HealthCheck extends NonameComponentLifecycle {
    private static final ConnectorResolver resolver = new ClasspathConnectorResolver("io.gridgo.connector");
    @NonNull
    private final String endpoint;
    private Connector connector;

    private void onRequest(Message message, Deferred<Message, Exception> deferred) {
        try (java.io.StringWriter writer = new StringWriter()) {
            writer.write("{\"status\": \"UP\"}");
            deferred.resolve(Message.ofAny(writer.toString()));
        } catch (IOException e) {
            deferred.reject(e);
        }
    }

    @Override
    protected void onStart() {
        connector = resolver.resolve(this.endpoint);
        connector.start();
        connector.getConsumer().orElseThrow().subscribe(this::onRequest);
    }

    @Override
    protected void onStop() {
        connector.stop();
    }

    @java.lang.SuppressWarnings("all")
    private HealthCheck(@NonNull final String endpoint) {
        if (endpoint == null) {
            throw new java.lang.NullPointerException("endpoint is marked @NonNull but is null");
        }
        this.endpoint = endpoint;
    }

    @java.lang.SuppressWarnings("all")
    public static HealthCheck of(@NonNull final String endpoint) {
        return new HealthCheck(endpoint);
    }
}