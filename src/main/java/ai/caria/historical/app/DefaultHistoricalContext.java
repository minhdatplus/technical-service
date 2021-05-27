package ai.caria.historical.app;

import ai.caria.historical.buffer.HistoricalBuffer;
import ai.caria.historical.core.HistoricalContext;
import ai.caria.historical.model.HistoricalModelFactory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class DefaultHistoricalContext implements HistoricalContext {

    private String swagger;

    private HistoricalModelFactory modelFactory;

    private HistoricalBuffer historicalBuffer;
}
