// Generated by delombok at Wed May 26 23:25:48 ICT 2021
package ai.caria.historical.app.io;

import ai.caria.historical.app.io.translator.ExceptionTranslator;
import ai.caria.historical.app.io.translator.RequestTranslator;
import ai.caria.historical.app.io.translator.ResponseTranslator;
import ai.caria.historical.core.handler.HistoricalHandler;
import io.gridgo.connector.Connector;
import lombok.NonNull;
import java.util.List;

public class ConnectorRegistry extends AbstractConnectorRegistry {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConnectorRegistry.class);

    private ConnectorRegistry(@NonNull HistoricalHandler HistoricalHandler, @NonNull String httpEndpoint, List<Connector> connectors, @NonNull Class<? extends Connectors> connectorRegistry, @NonNull RequestTranslator requestTranslator, @NonNull ResponseTranslator responseTranslator, @NonNull ExceptionTranslator exceptionTranslator) {
        super(HistoricalHandler, httpEndpoint, connectors, connectorRegistry, requestTranslator, responseTranslator, exceptionTranslator);
        if (HistoricalHandler == null) {
            throw new java.lang.NullPointerException("HistoricalHandler is marked @NonNull but is null");
        }
        if (httpEndpoint == null) {
            throw new java.lang.NullPointerException("httpEndpoint is marked @NonNull but is null");
        }
        if (connectorRegistry == null) {
            throw new java.lang.NullPointerException("connectorRegistry is marked @NonNull but is null");
        }
        if (requestTranslator == null) {
            throw new java.lang.NullPointerException("requestTranslator is marked @NonNull but is null");
        }
        if (responseTranslator == null) {
            throw new java.lang.NullPointerException("responseTranslator is marked @NonNull but is null");
        }
        if (exceptionTranslator == null) {
            throw new java.lang.NullPointerException("exceptionTranslator is marked @NonNull but is null");
        }
    }


    @java.lang.SuppressWarnings("all")
    public static class ConnectorRegistryBuilder {
        @java.lang.SuppressWarnings("all")
        private HistoricalHandler HistoricalHandler;
        @java.lang.SuppressWarnings("all")
        private String httpEndpoint;
        @java.lang.SuppressWarnings("all")
        private List<Connector> connectors;
        @java.lang.SuppressWarnings("all")
        private Class<? extends Connectors> connectorRegistry;
        @java.lang.SuppressWarnings("all")
        private RequestTranslator requestTranslator;
        @java.lang.SuppressWarnings("all")
        private ResponseTranslator responseTranslator;
        @java.lang.SuppressWarnings("all")
        private ExceptionTranslator exceptionTranslator;

        @java.lang.SuppressWarnings("all")
        ConnectorRegistryBuilder() {
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistryBuilder HistoricalHandler(final HistoricalHandler HistoricalHandler) {
            this.HistoricalHandler = HistoricalHandler;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistryBuilder httpEndpoint(final String httpEndpoint) {
            this.httpEndpoint = httpEndpoint;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistryBuilder connectors(final List<Connector> connectors) {
            this.connectors = connectors;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistryBuilder connectorRegistry(final Class<? extends Connectors> connectorRegistry) {
            this.connectorRegistry = connectorRegistry;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistryBuilder requestTranslator(final RequestTranslator requestTranslator) {
            this.requestTranslator = requestTranslator;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistryBuilder responseTranslator(final ResponseTranslator responseTranslator) {
            this.responseTranslator = responseTranslator;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistryBuilder exceptionTranslator(final ExceptionTranslator exceptionTranslator) {
            this.exceptionTranslator = exceptionTranslator;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConnectorRegistry build() {
            return new ConnectorRegistry(HistoricalHandler, httpEndpoint, connectors, connectorRegistry, requestTranslator, responseTranslator, exceptionTranslator);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "ConnectorRegistry.ConnectorRegistryBuilder(HistoricalHandler=" + this.HistoricalHandler + ", httpEndpoint=" + this.httpEndpoint + ", connectors=" + this.connectors + ", connectorRegistry=" + this.connectorRegistry + ", requestTranslator=" + this.requestTranslator + ", responseTranslator=" + this.responseTranslator + ", exceptionTranslator=" + this.exceptionTranslator + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static ConnectorRegistryBuilder builder() {
        return new ConnectorRegistryBuilder();
    }
}
