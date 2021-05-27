package ai.caria.historical.app;

import ai.caria.historical.buffer.DefaultHistoricalBufferInitializer;
import ai.caria.historical.core.HistoricalContext;
import ai.caria.historical.model.mysql.JdbiHelper;
import ai.caria.historical.model.mysql.HistoricalMySqlModelFactory;
import io.gridgo.utils.pojo.translator.ValueTranslators;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HistoricalAppInitializer {

    private final HistoricalAppConfig config;

    public HistoricalAppInitializer(HistoricalAppConfig config) {
        this.config = config;
    }

    public HistoricalContext init() {
        return initHistoricalApp();
    }

    private HistoricalContext initHistoricalApp() {
        ValueTranslators.getInstance().scan("ai.caria");
        var jdbi = JdbiHelper.createJdbiFromProperties(config.getDatabaseProperties());
        var modelFactory = new HistoricalMySqlModelFactory(jdbi);
        var bufferInitializer = DefaultHistoricalBufferInitializer.builder()
                .HistoricalModelFactory(modelFactory)
                .build();
        var HistoricalBuffer = bufferInitializer.init();
        return DefaultHistoricalContext.builder()
                .modelFactory(modelFactory)
                .historicalBuffer(HistoricalBuffer)
                .swagger(SwaggerStore.getContent(config.getSwaggerConfigFile()))
                .build();
    }
}
