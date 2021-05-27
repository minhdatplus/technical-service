package ai.caria.historical.app.io.translator;

import ai.caria.historical.message.HistoricalRequest;
import io.gridgo.framework.support.Message;

public interface RequestTranslator {
    HistoricalRequest translate(Message message, Class<? extends HistoricalRequest> target);
}
