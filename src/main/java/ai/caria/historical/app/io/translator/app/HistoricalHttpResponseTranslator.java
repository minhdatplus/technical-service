package ai.caria.historical.app.io.translator.app;

import ai.caria.historical.app.io.translator.Response;
import ai.caria.historical.app.io.translator.ResponseTranslator;
import ai.caria.historical.commons.datetime.DateTimeUtils;
import ai.caria.historical.commons.proto.AdjustedRateProto;
import ai.caria.historical.commons.proto.ChartObjectProto;
import ai.caria.historical.commons.proto.DailyStockInfoProto;
import ai.caria.historical.message.HistoricalResponse;
import ai.caria.historical.message.impl.AdjustedRateResponse;
import ai.caria.historical.message.impl.StockDataResponse;
import ai.caria.historical.message.impl.SwaggerResponse;
import io.gridgo.bean.BElement;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.serialization.protobuf.ProtobufMultiSchemaSerializer;
import io.gridgo.connector.httpcommon.HttpContentType;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.pojo.helper.FunctionAccessor;
import io.gridgo.utils.pojo.helper.MethodAccessors;
import lombok.extern.slf4j.Slf4j;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static io.gridgo.connector.httpcommon.HttpCommonConstants.CONTENT_TYPE;
import static io.gridgo.connector.httpcommon.HttpCommonConstants.HEADER_STATUS;

@Slf4j
public class HistoricalHttpResponseTranslator implements ResponseTranslator {

    private static final String APPLICATION_JSON_HEADER = HttpContentType.APPLICATION_JSON.getMime() + ";charset=utf-8";
    private Map<Class<? extends HistoricalResponse>, FunctionAccessor> factory = new NonBlockingHashMap<>();
    private static final ProtobufMultiSchemaSerializer PROTOBUF_MULTI_SCHEMA_SERIALIZER = BFactory.DEFAULT.getSerializerRegistry().lookup(ProtobufMultiSchemaSerializer.NAME);

    public HistoricalHttpResponseTranslator() {
        var methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!isValidAnnotatedMethod(method)) continue;

            var annotation = method.getAnnotation(Response.class);
            var functionAccessor = MethodAccessors.forStaticSingleParamFunction(method);
            factory.put(annotation.value(), functionAccessor);
        }

        PROTOBUF_MULTI_SCHEMA_SERIALIZER.registerSchema(AdjustedRateProto.List_AdjustedRate.class, 1);
        PROTOBUF_MULTI_SCHEMA_SERIALIZER.registerSchema(DailyStockInfoProto.List_DailyStockInfo.class, 2);
        PROTOBUF_MULTI_SCHEMA_SERIALIZER.registerSchema(ChartObjectProto.AmiObject.class, 3);
    }

    @Response(SwaggerResponse.class)
    public static Message swagger(SwaggerResponse response) {
        return Message.ofAny(response.getContent());
    }

    @Response(AdjustedRateResponse.class)
    public static Message adjustedRate(AdjustedRateResponse response) {
        var listBuilder = AdjustedRateProto.List_AdjustedRate.newBuilder();
        response.getData().stream()
                .map(rate -> {
                            var builder = AdjustedRateProto.AdjustedRate.newBuilder()
                                    .setAdjrate(rate.getAdjrate())
                                    .setApplied(rate.getApplied())
                                    .setExchange(rate.getExchange())
                                    .setId(rate.getId())
                                    .setSymbol(rate.getSymbol())
                                    .setTradingdate(DateTimeUtils.format(rate.getTradingdate()))
                                    .setUpdateddate(DateTimeUtils.format(rate.getUpdateddate()));
                            if (rate.getCastatus() != null) {
                                builder.setCastatus(rate.getCastatus());
                            }
                            if (rate.getFlag() != null) {
                                builder.setFlag(rate.getFlag());
                            }

                            return builder
                                    .build();
                        }
                )
                .forEach(listBuilder::addItems);
        var list = listBuilder.build();
        var bytes = BElement.ofAny(list).toBytes(ProtobufMultiSchemaSerializer.NAME);
        return Message.ofAny(bytes);
    }

    @Response(StockDataResponse.class)
    public static Message dailyStockInfo(StockDataResponse response) {
//        var listBuilder = DailyStockInfoProto.List_DailyStockInfo.newBuilder();
//        response.getData().stream()
//                .map(rate -> {
//                            var builder = DailyStockInfoProto.DailyStockInfo.newBuilder()
//                                    .setAvgprice(rate.getAvgprice())
//                                    .setBidcount(rate.getBidcount())
//                                    .setBuytotalqtty(rate.getBuytotalqtty())
//                                    .setBuytotaltrade(rate.getBuytotaltrade())
//                                    .setCeilingprice(rate.getCeilingprice())
//                                    .setExchange(rate.getExchange())
//                                    .setCloseprice(rate.getCloseprice())
//                                    .setId(rate.getId())
//                                    .setClosepriceAdjusted(rate.getCloseprice_adjusted())
//                                    .setFloorprice(rate.getFloorprice())
//                                    .setHighprice(rate.getHighprice())
//                                    .setHighpriceAdjusted(rate.getHighprice_adjusted())
//                                    .setHighpriceAdjustedIds(rate.getHighprice_adjusted_ids())
//                                    .setLowprice(rate.getLowprice())
//                                    .setLowpriceAdjusted(rate.getLowprice_adjusted())
//                                    .setLowpriceAdjustedIds(rate.getLowprice_adjusted_ids())
//                                    .setOffercount(rate.getOffercount())
//                                    .setOpenprice(rate.getOffercount())
//                                    .setOpenpriceAdjusted(rate.getOpenprice_adjusted())
//                                    .setOpenpriceAdjustedIds(rate.getOpenprice_adjusted_ids())
//                                    .setPricechange(rate.getPricechange())
//                                    .setPricechangepercentage(rate.getPricechangepercentage())
//                                    .setPriorprice(rate.getPriorprice())
//                                    .setRefprice(rate.getRefprice())
//                                    .setSelltotalqtty(rate.getSelltotalqtty())
//                                    .setSelltotaltrade(rate.getSelltotaltrade())
//                                    .setStocksymbol(rate.getStocksymbol())
//                                    .setTotalbidqtty(rate.getTotalbidqtty())
//                                    .setTotalofferqtty(rate.getTotalofferqtty())
//                                    .setTotalqtty(rate.getTotalqtty())
//                                    .setTotalvalue(rate.getTotalvalue())
//                                    .setTotalvaluetraded(rate.getTotalvaluetraded())
//                                    .setTotalvolumetraded(rate.getTotalvolumetraded())
//                                    .setTotalvolumetraded(rate.getTotalvolumetraded())
//                                    .setTotalvalue(rate.getTotalvalue());
//
//                            return builder
//                                    .build();
//                        }
//                )
//                .forEach(listBuilder::addItems);
//        var list = listBuilder.build();
//        var bytes = BElement.ofAny(list).toBytes(ProtobufMultiSchemaSerializer.NAME);
        var listBuilder = ChartObjectProto.AmiObject.newBuilder();
        response.getData().stream()
                .map(rate -> {
                            var builder = ChartObjectProto.ChartObject.newBuilder()
                                    .setOpenn(String.valueOf(rate.getOpen()))
                                    .setHigh(String.valueOf(rate.getHighest()))
                                    .setLow(String.valueOf(rate.getLowest()))
                                    .setClose(String.valueOf(rate.getClose()))
                                    .setVolume(String.valueOf(rate.getVolume()))
                                    .setTradingdate(DateTimeUtils.date2String(rate.getTradingdate()))
                                    .setSymbol(rate.getSymbol());

                            return builder
                                    .build();
                        }
                )
                .forEach(listBuilder::addItems);
        var list = listBuilder.build();
        var bytes = BElement.ofAny(list).toBytes(ProtobufMultiSchemaSerializer.NAME);
        return Message.ofAny(response);
    }

    private boolean isValidAnnotatedMethod(Method method) {
        var modifiers = method.getModifiers();
        return method.isAnnotationPresent(Response.class)
                && Modifier.isStatic(modifiers)
                && Modifier.isPublic(modifiers);
    }


    @Override
    public Message translate(HistoricalResponse response) {
        var type = response.getClass();
        var message = (Message) factory.get(type).apply(response);
        enrichHeaders(message);

        return message;
    }

    private static void enrichHeaders(Message message) {
        if (message != null) {
            message.getPayload().addHeader(HEADER_STATUS, 200);
            message.getPayload().addHeader(CONTENT_TYPE, APPLICATION_JSON_HEADER);
            message.getPayload().addHeader("x-correlation-id", "historical");
        }
    }
}
