package ai.caria.historical.app.io.translator;

import ai.caria.historical.message.HistoricalResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Response {
    Class<? extends HistoricalResponse> value();
}
