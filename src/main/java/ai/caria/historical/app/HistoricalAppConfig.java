package ai.caria.historical.app;

import ai.caria.cognac.config.ConfigField;
import ai.caria.cognac.config.ConfigPrefix;
import lombok.Getter;

import java.util.Properties;

@Getter
public class HistoricalAppConfig {

    @ConfigField("app.http.endpoint")
    private String httpEndpoint;

    @ConfigField("app.http.endpoint.statistics")
    private String statisticEndpoint;

    @ConfigField("app.http.endpoint.health.check")
    private String healthCheckEndpoint;

    @ConfigField("app.environment")
    private String appEnv;

    @ConfigField("app.name")
    private String appName;

    @ConfigPrefix("oracle.")
    private Properties databaseProperties;

    @ConfigField("app.swagger.config.file")
    private String swaggerConfigFile;
}
