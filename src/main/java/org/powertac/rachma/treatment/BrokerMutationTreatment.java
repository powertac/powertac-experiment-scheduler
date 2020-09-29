package org.powertac.rachma.treatment;

import lombok.Getter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.instance.Instance;

public class BrokerMutationTreatment implements Treatment {

    public enum Action {
        ADD,
        REMOVE
    }

    @Getter
    private Action action;

    @Getter
    private Broker broker;

    public BrokerMutationTreatment(Action action, Broker broker) {
        this.action = action;
        this.broker = broker;
    }

    @Override
    public Instance mutate(Instance instanceCopy) {
        switch (action) {
            case ADD:
                instanceCopy.getBrokers().add(broker);
                break;
            case REMOVE:
                instanceCopy.getBrokers().remove(broker);
                break;
        }
        return instanceCopy;
    }
}
