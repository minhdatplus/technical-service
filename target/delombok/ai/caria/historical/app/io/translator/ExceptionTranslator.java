package ai.caria.historical.app.io.translator;

import ai.caria.historical.message.HistoricalRequest;
import io.gridgo.framework.support.Message;

public interface ExceptionTranslator {

    Message translate(HistoricalRequest request, Exception ex);

    static ExceptionTranslator newHttp() {
        return new HttpExceptionTranslator();
    }
}
