package org.powertac.orchestrator.baseline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.orchestrator.broker.*;
import org.powertac.orchestrator.game.generator.MultiplierGameGenerator;
import org.powertac.orchestrator.validation.SimulationParameterValidator;
import org.powertac.orchestrator.validation.exception.ValidationException;
import org.powertac.orchestrator.weather.WeatherConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaselineFactoryTests {

    @Test
    void stdCreateFromSpecTest() throws ValidationException {
        // Spec
        Map<String, String> properties = new HashMap<>();
        properties.put("some.value", "some value");
        properties.put("some.number", "959");
        Broker brokerOne = Mockito.mock(Broker.class);
        BrokerSet setOne = new BrokerSet(null, Stream.of(brokerOne).collect(Collectors.toSet()));
        Broker brokerTwo = Mockito.mock(Broker.class);
        BrokerSet setTwo = new BrokerSet(null, Stream.of(brokerTwo).collect(Collectors.toSet()));
        List<BrokerSet> brokerSets = Stream.of(setOne, setTwo).collect(Collectors.toList());
        WeatherConfiguration weatherOne = new WeatherConfiguration("rotterdam", Instant.parse("2011-12-01T00:00:00Z"));
        WeatherConfiguration weatherTwo = new WeatherConfiguration("cheyenne", Instant.parse("2013-01-01T00:00:00Z"));
        List<WeatherConfiguration> weatherConfigs = Stream.of(weatherOne, weatherTwo).collect(Collectors.toList());
        BaselineSpec spec = new BaselineSpec("Spec Name", properties, brokerSets, weatherConfigs);

        // Integration
        SimulationParameterValidator validator = Mockito.mock(SimulationParameterValidator.class);
        BrokerSetFactory brokerSetFactory = Mockito.mock(BrokerSetFactory.class);
        Mockito.when(brokerSetFactory.create(setOne.getBrokers())).thenReturn(setOne);
        Mockito.when(brokerSetFactory.create(setTwo.getBrokers())).thenReturn(setTwo);
        MultiplierGameGenerator multiplierGameGenerator = Mockito.mock(MultiplierGameGenerator.class);
        BaselineFactory factory = new BaselineFactoryImpl(validator, brokerSetFactory, multiplierGameGenerator);

        // Baseline
        Baseline baseline = factory.createFromSpec(spec);
        Assertions.assertEquals(36, baseline.getId().length());
        Assertions.assertEquals("Spec Name", baseline.getName());
        Assertions.assertEquals(2, baseline.getCommonParameters().size());
        Assertions.assertEquals("some value", baseline.getCommonParameters().get("some.value"));
        Assertions.assertEquals("959", baseline.getCommonParameters().get("some.number"));
        Assertions.assertEquals(2, baseline.getBrokerSets().size());
        Assertions.assertTrue(baseline.getBrokerSets().contains(setOne));
        Assertions.assertTrue(baseline.getBrokerSets().contains(setTwo));
        Assertions.assertEquals(2, baseline.getWeatherConfigurations().size());
        Assertions.assertTrue(baseline.getWeatherConfigurations().contains(weatherOne));
        Assertions.assertTrue(baseline.getWeatherConfigurations().contains(weatherTwo));
        Assertions.assertEquals(0, baseline.getGames().size());
        // FIXME : there's probably a more deterministic way to test this
        Assertions.assertTrue(baseline.getCreatedAt().isBefore(Instant.now())
            && baseline.getCreatedAt().isAfter(Instant.now().minus(1, ChronoUnit.SECONDS)));
    }

    // TODO : add test case for failed parameter validation

    // TODO : add test case for newly creating broker sets

}
