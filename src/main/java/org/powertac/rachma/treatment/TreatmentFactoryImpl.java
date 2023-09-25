package org.powertac.rachma.treatment;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.generator.GameGeneratorConfig;
import org.powertac.rachma.game.generator.MultiplierGameGeneratorConfig;
import org.powertac.rachma.util.ID;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TreatmentFactoryImpl implements TreatmentFactory {

    @Override
    @Deprecated
    public Treatment createFrom(TreatmentSpec spec) {
        Treatment treatment = new Treatment(
            ID.gen(),
            spec.getName(),
            spec.getBaseline(),
            spec.getModifier(),
            Instant.now(),
            createGames(spec));
        for (int i = 0; i < treatment.getGames().size(); i++) {
            Game game = treatment.getGames().get(i);
            game.setName(getGameName(treatment.getName(), i, treatment.getGames().size()));
            game.setTreatment(treatment);
        }
        return treatment;
    }

    @Override
    public Treatment create(String name, Baseline baseline, Modifier modifier) {
        List<Game> games = createGames(baseline, modifier);
        Treatment treatment = new Treatment(
            ID.gen(),
            name,
            baseline,
            modifier,
            Instant.now(),
            games);
        for (int i = 0; i < treatment.getGames().size(); i++) {
            Game game = treatment.getGames().get(i);
            game.setName(getGameName(treatment.getName(), i, treatment.getGames().size()));
            game.setTreatment(treatment);
        }
        return treatment;
    }

    private List<Game> createGames(TreatmentSpec spec) {
        if (spec.getModifier() instanceof ReplaceBrokerModifier) {
            return applyReplaceBrokerModifier(spec.getBaseline(), (ReplaceBrokerModifier) spec.getModifier());
        } else if (spec.getModifier() instanceof ParameterSetModifier) {
            return applyParameterSetModifier(spec.getBaseline(), (ParameterSetModifier) spec.getModifier());
        } else {
            throw new NotImplementedException("unknown modifier of type " + spec.getModifier().getClass());
        }
    }

    private List<Game> createGames(Baseline baseline, Modifier modifier) {
        if (modifier instanceof ReplaceBrokerModifier) {
            return applyReplaceBrokerModifier(baseline, (ReplaceBrokerModifier) modifier);
        } else if (modifier instanceof ParameterSetModifier) {
            return applyParameterSetModifier(baseline, (ParameterSetModifier) modifier);
        } else {
            throw new NotImplementedException("unknown modifier of type " + modifier.getClass());
        }
    }

    // TODO : this has to be refactored ASAP (in the way that many baselines/treatments/games may linked to one or more game configs)
    private List<Game> applyReplaceBrokerModifier(Baseline baseline, ReplaceBrokerModifier modifier) {
        Map<BrokerSet, BrokerSet> brokerSetMap = new HashMap<>();
        GameGeneratorConfig config = baseline.getConfig();
        if (config instanceof MultiplierGameGeneratorConfig) {
            Stream.of(((MultiplierGameGeneratorConfig) config).getGameConfig().getBrokers())
                .peek(set -> brokerSetMap.put(set, new BrokerSet(ID.gen(), new HashSet<>(set.getBrokers()))))
                .map(brokerSetMap::get)
                .forEach(newSet -> {
                    modifier.getBrokerMapping().forEach((original, replacement) -> {
                        if (!original.equals(replacement) && newSet.getBrokers().contains(original)) {
                            newSet.getBrokers().remove(original);
                            newSet.getBrokers().add(replacement);
                        }
                    });
                });
            return baseline.getGames().stream()
                .map(original ->
                    copyBuilder(original)
                        .brokerSet(brokerSetMap.get(original.getBrokerSet()))
                        .build())
                .collect(Collectors.toList());
        } else {
            throw new RuntimeException("unknown game generator type");
        }
    }

    private String getGameName(String treatmentName, int gameIndex, int treatmentSize) {
        final int maxDigits = (int) (Math.log10(treatmentSize) + 1);
        return String.format("%s - %s",
            treatmentName,
            StringUtils.leftPad(String.valueOf(gameIndex + 1), maxDigits, "0"));
    }

    private List<Game> applyParameterSetModifier(Baseline baseline, ParameterSetModifier modifier) {
        return baseline.getGames().stream()
            .map(original ->
                copyBuilder(original)
                    .serverParameters(mergeServerParameters(original.getServerParameters(), modifier.getParameters()))
                    .build())
            .collect(Collectors.toList());
    }

    private Map<String, String> mergeServerParameters(Map<String, String> original, Map<String, String> update) {
        Map<String, String> merged = new HashMap<>();
        merged.putAll(original);
        merged.putAll(update);
        return merged;
    }

    private Game.GameBuilder copyBuilder(Game original) {
        return Game.builder()
            .id(ID.gen())
            .base(original)
            .bootstrap(new File(ID.gen(), FileRole.BOOTSTRAP, original, "", new HashSet<>()))
            .seed(new File(ID.gen(), FileRole.SEED, original, "", new HashSet<>()))
            .brokerSet(original.getBrokerSet())
            .weatherConfiguration(original.getWeatherConfiguration())
            .serverParameters(original.getServerParameters())
            .createdAt(Instant.now());
    }

}
