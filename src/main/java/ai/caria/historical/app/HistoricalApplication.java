package ai.caria.historical.app;

import ai.caria.cognac.config.PropertiesConfigReader;
import ai.caria.historical.app.io.ConnectorRegistry;
import ai.caria.historical.app.io.HealthCheck;
import ai.caria.historical.app.io.HistoricalAppConnectors;
import ai.caria.historical.app.io.StatisticsHandler;
import ai.caria.historical.app.io.translator.HttpExceptionTranslator;
import ai.caria.historical.app.io.translator.app.HistoricalHttpRequestTranslator;
import ai.caria.historical.app.io.translator.app.HistoricalHttpResponseTranslator;
import ai.caria.historical.core.HistoricalContext;
import ai.caria.historical.core.handler.HistoricalHandler;
import io.gridgo.bean.BObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static io.gridgo.utils.ThreadUtils.registerShutdownTask;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor(access = PRIVATE)
@Builder
public class HistoricalApplication {
    private static final String APP_CONFIG_FILE_PATH = "config/application.properties";
    private static final String GROUP_ID = "group.id";

    private static HistoricalAppConfig loadConfig(String[] args) throws Exception {
        return PropertiesConfigReader //
                .forType(HistoricalAppConfig.class) //
                .readProperties(APP_CONFIG_FILE_PATH);
    }

    public static void main(String[] args) {
        var historicalApp = standalone();
        try {
            historicalApp.start(); // run the app...hope to have a happy life!!!
        } catch (Exception e) {
            log.error("Cannot start historicalApp application", e);
            historicalApp.stop();
            return;
        }

        registerShutdownTask(historicalApp::stop);
    }

    public static HistoricalApplication standalone(String... args) {
        HistoricalAppConfig config;
        try {
            config = loadConfig(args);
            log.debug(BObject.ofPojo(config).toJson());
        } catch (Exception e) {
            log.error("Error while loading config file", e);
            return null;
        }

        return HistoricalApplication.builder()
                .config(config)
                .build();
    }

    @NonNull
    private final HistoricalAppConfig config;

    private ConnectorRegistry connectorRegistry;

    public void start() throws IOException {
        var context = new HistoricalAppInitializer(config)
                .init();
        publishHttpEndpoint(context);
        publishStatisticEndpoint();
        publishHealthCheckEndpoint();
    }

    private void publishHttpEndpoint(HistoricalContext context) {
        var handler = HistoricalHandler.newInstance(context);
        connectorRegistry = ConnectorRegistry.builder()
                .HistoricalHandler(handler)
                .httpEndpoint(config.getHttpEndpoint())
                .connectorRegistry(HistoricalAppConnectors.class)
                .requestTranslator(new HistoricalHttpRequestTranslator())
                .responseTranslator(new HistoricalHttpResponseTranslator())
                .exceptionTranslator(new HttpExceptionTranslator())
                .build();
        connectorRegistry.start();
    }

    public void stop() {
        if (connectorRegistry != null)
            connectorRegistry.stop();
    }

    private void publishStatisticEndpoint() {
        if (config.getStatisticEndpoint() != null) {
            var statisticHandler = StatisticsHandler.of(config.getAppName(), config.getStatisticEndpoint());
            statisticHandler.start();
            registerShutdownTask(statisticHandler::stop);
        }
    }

    private void publishHealthCheckEndpoint() {
        if (config.getHealthCheckEndpoint() != null) {
            var healthCheckEndpoint = HealthCheck.of(config.getHealthCheckEndpoint());
            healthCheckEndpoint.start();
            registerShutdownTask(healthCheckEndpoint::stop);
            log.info("Health check API started");
        }
    }

}
