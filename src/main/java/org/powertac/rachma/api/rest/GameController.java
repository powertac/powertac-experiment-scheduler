package org.powertac.rachma.api.rest;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerRepository;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;
import org.powertac.rachma.game.GameValidationException;
import org.powertac.rachma.game.GameValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/games")
public class GameController {

    private final BrokerRepository brokers;
    private final GameRepository games;
    private final GameValidator validator;

    public GameController(BrokerRepository brokers, GameRepository games, GameValidator validator) {
        this.brokers = brokers;
        this.games = games;
        this.validator = validator;
    }

    @GetMapping("/")
    public ResponseEntity<Collection<Game>> getGames() {
        return ResponseEntity.ok().body(games.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<?> queueInstance(@RequestBody Game game) {
        try {
            resolveBrokerSet(game);
            validator.validate(game);
            games.save(game);
            return ResponseEntity.ok().build();
        } catch (GameValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void resolveBrokerSet(Game game) throws GameValidationException {
        Set<Broker> resolvedBrokers = new HashSet<>();
        for (Broker broker : game.getBrokers()) {
            Broker resolved = this.brokers.findByName(broker.getName());
            if (null == resolved) {
                throw new GameValidationException(String.format("could not resolve broker '%s'", broker.getName()));
            }
            resolvedBrokers.add(resolved);
        }
        game.setBrokers(resolvedBrokers);
    }

}
