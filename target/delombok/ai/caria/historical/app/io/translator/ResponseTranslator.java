package ai.caria.historical.app.io.translator;

import ai.caria.historical.message.HistoricalResponse;
import io.gridgo.framework.support.Message;

public interface ResponseTranslator {

    Message translate(HistoricalResponse response);
}
