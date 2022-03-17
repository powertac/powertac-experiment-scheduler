package org.powertac.rachma.baseline;

import org.powertac.rachma.broker.BrokerSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BaselineSpecFactoryImpl implements BaselineSpecFactory {

    @Override
    public BaselineSpec createFrom(Baseline baseline) {
        BaselineSpec spec = new BaselineSpec();
        spec.setName(baseline.getName());
        spec.setCommonParameters(new HashMap<>(baseline.getCommonParameters()));
        spec.setBrokerSets(copyBrokerSetList(baseline.getBrokerSets()));
        spec.setWeatherConfigurations(new ArrayList<>(baseline.getWeatherConfigurations()));
        return spec;
    }

    private List<BrokerSet> copyBrokerSetList(List<BrokerSet> original) {
        return original.stream()
            .map(this::copyBrokerSet)
            .collect(Collectors.toList());
    }

    private BrokerSet copyBrokerSet(BrokerSet original) {
        return new BrokerSet(
            UUID.randomUUID().toString(),
            original.getBrokers());
    }

}
