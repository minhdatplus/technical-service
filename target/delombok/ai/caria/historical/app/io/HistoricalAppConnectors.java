package ai.caria.historical.app.io;

import ai.caria.historical.message.impl.AdjustedRateRequest;
import ai.caria.historical.message.impl.StockDataRequest;
import io.gridgo.framework.support.Message;
import org.joo.promise4j.Deferred;

import static org.eclipse.jetty.http.HttpMethod.POST;

public interface HistoricalAppConnectors extends Connectors {

    @Endpoint(path = "trading/adjustedRate", method = POST, request = AdjustedRateRequest.class, prometheusPrefix = "adjusted_rate")
    void adjustedRate(Message message, Deferred<Message, Exception> deferred);

    @Endpoint(path = "trading/amibroker", method = POST, request = StockDataRequest.class, prometheusPrefix = "daily_stock_data")
    void amibroker(Message message, Deferred<Message, Exception> deferred);
}
