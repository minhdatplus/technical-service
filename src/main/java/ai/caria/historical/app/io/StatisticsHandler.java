package ai.caria.historical.app.io;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.impl.resolvers.ClasspathConnectorResolver;
import io.gridgo.framework.impl.NonameComponentLifecycle;
import io.gridgo.framework.support.Message;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joo.promise4j.Deferred;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;

@RequiredArgsConstructor(staticName = "of")
public class StatisticsHandler extends NonameComponentLifecycle {
    private static final ConnectorResolver resolver = new ClasspathConnectorResolver("io.gridgo.connector");

    private final @NonNull String prefix;
    private final @NonNull String endpoint;
    private Connector connector;

    private final CollectorRegistry registry = CollectorRegistry.defaultRegistry;

    @Override
    protected void onStart() {
        connector = resolver.resolve(this.endpoint);
        connector.start();

        connector.getConsumer().orElseThrow().subscribe(this::onRequest);

        registerHotpotStatistics();
    }

    private void registerHotpotStatistics() {
        new StandardExports().register(registry);
        new MemoryPoolsExports().register(registry);
        new MemoryAllocationExports().register(registry);
        new BufferPoolsExports().register(registry);
        new GarbageCollectorExports().register(registry);
        new ThreadExports().register(registry);
        new ClassLoadingExports().register(registry);
        new VersionInfoExports().register(registry);
    }

    @Override
    protected void onStop() {
        connector.stop();
    }

    private void onRequest(Message request, Deferred<Message, Exception> deferred) {
        try (var writer = new StringWriter()) {
            write(writer);
            deferred.resolve(Message.ofAny(writer.toString()));
        } catch (IOException e) {
            deferred.reject(e);
        }
    }

    protected void write(StringWriter writer) throws IOException {
        write004(writer, registry.filteredMetricFamilySamples(new HashSet<>()));
    }

    /**
     * Write out the text version 0.0.4 of the given MetricFamilySamples.
     */
    protected void write004(Writer writer, Enumeration<Collector.MetricFamilySamples> mfs) throws IOException {
        /*
         * See http://prometheus.io/docs/instrumenting/exposition_formats/ for the
         * output format specification.
         */
        while (mfs.hasMoreElements()) {
            Collector.MetricFamilySamples metricFamilySamples = mfs.nextElement();
            writer.write("# HELP ");
            writer.write(prefix + "_");
            writer.write(metricFamilySamples.name);
            writer.write(' ');
            writeEscapedHelp(writer, metricFamilySamples.help);
            writer.write('\n');

            writer.write("# TYPE ");
            writer.write(prefix + "_");
            writer.write(metricFamilySamples.name);
            writer.write(' ');
            writer.write(typeString(metricFamilySamples.type));
            writer.write('\n');

            for (Collector.MetricFamilySamples.Sample sample : metricFamilySamples.samples) {
                writer.write(prefix + "_");
                writer.write(sample.name);
                if (!sample.labelNames.isEmpty()) {
                    writer.write('{');
                    for (int i = 0; i < sample.labelNames.size(); ++i) {
                        writer.write(prefix + "_");
                        writer.write(sample.labelNames.get(i));
                        writer.write("=\"");
                        writeEscapedLabelValue(writer, sample.labelValues.get(i));
                        writer.write("\",");
                    }
                    writer.write('}');
                }
                writer.write(' ');
                writer.write(Collector.doubleToGoString(sample.value));
                if (sample.timestampMs != null) {
                    writer.write(' ');
                    writer.write(sample.timestampMs.toString());
                }
                writer.write('\n');
            }
        }
    }

    private static void writeEscapedLabelValue(Writer writer, String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    writer.append("\\\\");
                    break;
                case '\"':
                    writer.append("\\\"");
                    break;
                case '\n':
                    writer.append("\\n");
                    break;
                default:
                    writer.append(c);
            }
        }
    }

    private static void writeEscapedHelp(Writer writer, String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    writer.append("\\\\");
                    break;
                case '\n':
                    writer.append("\\n");
                    break;
                default:
                    writer.append(c);
            }
        }
    }

    private static String typeString(Collector.Type t) {
        switch (t) {
            case GAUGE:
                return "gauge";
            case COUNTER:
                return "counter";
            case SUMMARY:
                return "summary";
            case HISTOGRAM:
                return "histogram";
            default:
                return "untyped";
        }
    }
}
