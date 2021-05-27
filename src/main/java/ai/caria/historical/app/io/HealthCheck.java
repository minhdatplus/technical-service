package ai.caria.historical.app.io;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.impl.resolvers.ClasspathConnectorResolver;
import io.gridgo.framework.impl.NonameComponentLifecycle;
import io.gridgo.framework.support.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joo.promise4j.Deferred;

import java.io.IOException;
import java.io.StringWriter;

@RequiredArgsConstructor(staticName = "of")
public class HealthCheck extends NonameComponentLifecycle {
    private static final ConnectorResolver resolver = new ClasspathConnectorResolver("io.gridgo.connector");

    @NonNull
    private final String endpoint;
    private Connector connector;

    private void onRequest(Message message, Deferred<Message, Exception> deferred) {
        try (var writer = new StringWriter()) {
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
}
