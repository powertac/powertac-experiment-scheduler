package org.powertac.rachma.treatment;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.util.ID;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TreatmentFactoryImpl implements TreatmentFactory {

    @Override
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

    private List<Game> createGames(TreatmentSpec spec) {
        if (spec.getModifier() instanceof ReplaceBrokerModifier) {
            return applyReplaceBrokerModifier(spec.getBaseline(), (ReplaceBrokerModifier) spec.getModifier());
        } else if (spec.getModifier() instanceof ParameterSetModifier) {
            return applyParameterSetModifier(spec.getBaseline(), (ParameterSetModifier) spec.getModifier());
        } else {
            throw new NotImplementedException("unknown modifier of type " + spec.getModifier().getClass());
        }
    }

    private List<Game> applyReplaceBrokerModifier(Baseline baseline, ReplaceBrokerModifier modifier) {
        Map<BrokerSet, BrokerSet> brokerSetMap = new HashMap<>();
        baseline.getBrokerSets().stream()
            .peek(set -> brokerSetMap.put(set, new BrokerSet(ID.gen(), new HashSet<>(set.getBrokers()))))
            .map(brokerSetMap::get)
            .forEach(newSet -> {
                // that's the actual replacement
                // TODO : put this part in the Modifier, add reflection information (granularity, etc.), then abstract
                if (newSet.getBrokers().contains(modifier.getOriginal())) {
                    newSet.getBrokers().remove(modifier.getOriginal());
                    newSet.getBrokers().add(modifier.getReplacement());
                }
            });
        return baseline.getGames().stream()
            .map(original ->
                copyBuilder(original)
                    .brokerSet(brokerSetMap.get(original.getBrokerSet()))
                    .build())
            .collect(Collectors.toList());
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
            .bootstrap(new File(ID.gen(), FileRole.BOOTSTRAP, original))
            .seed(new File(ID.gen(), FileRole.SEED, original))
            .brokerSet(original.getBrokerSet())
            .weatherConfiguration(original.getWeatherConfiguration())
            .serverParameters(original.getServerParameters())
            .createdAt(Instant.now());
    }

}
