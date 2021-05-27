package ai.caria.historical.app.io;

import ai.caria.historical.message.HistoricalRequest;
import org.eclipse.jetty.http.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Endpoint {

    String path();

    HttpMethod method();

    Class<? extends HistoricalRequest> request();

    String prometheusPrefix() default "";
}
