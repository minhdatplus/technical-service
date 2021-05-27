package ai.caria.historical.app.io;

import ai.caria.historical.app.io.translator.ExceptionTranslator;
import ai.caria.historical.app.io.translator.RequestTranslator;
import ai.caria.historical.app.io.translator.ResponseTranslator;
import ai.caria.historical.core.handler.HistoricalHandler;
import io.gridgo.connector.Connector;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConnectorRegistry extends AbstractConnectorRegistry {

    @Builder
    private ConnectorRegistry(@NonNull HistoricalHandler HistoricalHandler,
                              @NonNull String httpEndpoint,
                              List<Connector> connectors,
                              @NonNull Class<? extends Connectors> connectorRegistry,
                              @NonNull RequestTranslator requestTranslator,
                              @NonNull ResponseTranslator responseTranslator,
                              @NonNull ExceptionTranslator exceptionTranslator) {
        super(HistoricalHandler, httpEndpoint, connectors, connectorRegistry, requestTranslator, responseTranslator, exceptionTranslator);
    }
}
