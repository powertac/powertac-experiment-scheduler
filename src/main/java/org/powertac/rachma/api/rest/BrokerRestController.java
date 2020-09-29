package org.powertac.rachma.api.rest;

import lombok.Getter;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.broker.BrokerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("brokers")
public class BrokerRestController {

    private final BrokerTypeRepository brokerTypeRepository;

    @Autowired
    public BrokerRestController(BrokerTypeRepository brokerTypeRepository) {
        this.brokerTypeRepository = brokerTypeRepository;
    }

    @GetMapping("/types")
    public Object brokers() {
        return new Object() {
            @Getter boolean success = true;
            @Getter List<BrokerType> payload = new ArrayList<>(brokerTypeRepository.findAll().values());
        };
    }

}
