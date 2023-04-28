package org.powertac.rachma.file;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.util.schema.Field;
import org.powertac.rachma.util.schema.Schema;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class GameGroupManifestBuilderImpl implements GameGroupManifestBuilder {

    public final static String defaultDelimiter = ", "; // TODO : make configurable via properties

    private final DateTimeFormatter weatherDateFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault());

    @Override
    public String buildManifest(List<Game> games, String hostUri, String delimiter) {
        Set<Broker> brokers = collectBrokers(games);
        Schema<Game> schema = buildSchema(hostUri, brokers, delimiter);
        return parseCsv(schema, games);
    }

    private Schema<Game> buildSchema(String hostUri, Set<Broker> brokers, String delimiter) {
        Schema<Game> schema = Schema.create(
            Field.create("gameId", Game::getId),
            Field.create("gameName", Game::getName),
            Field.create("status", game -> "game_completed"), // TODO : @Govert/@Erik what are the available game status codes?
            Field.create("gameSize", game -> ((Integer) game.getBrokers().size()).toString()),
            Field.create("gameLength", game -> ""), // TODO : extract game length from logs
            Field.create("lastTick", game -> ""), // TODO : extract last tick from logs
            Field.create("weatherLocation", game -> game.getWeatherConfiguration().getLocation()),
            Field.create("weatherDate", game -> weatherDateFormatter.format(game.getWeatherConfiguration().getStartTime())),
            Field.create("DOI", game -> ""), // TODO : parse DOI
            Field.create("logUrl", game -> String.format("%s%s.game.tar.gz", hostUri, game.getId())));
        for (Broker broker : brokers) {
            schema.add(Field.create(broker.getName(), game -> "")); // TODO : extract final cash positions from logs
        }
        schema.setDelimiter(delimiter);
        return schema;
    }

    private Set<Broker> collectBrokers(List<Game> games) {
        Set<Broker> brokers = new HashSet<>();
        for (Game game : games) {
            brokers.addAll(game.getBrokers());
        }
        return brokers;
    }

    private String parseCsv(Schema<Game> schema, List<Game> games) {
        StringBuilder builder = new StringBuilder();
        builder.append(schema.header()).append("\n"); // TODO : should use system default
        for (Game game : games) {
            builder.append(schema.format(game)).append("\n"); // TODO : should use system default
        }
        return builder.toString();
    }

}
