package org.powertac.rachma.api.rest;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerRepository;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.game.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private final BrokerRepository brokers;
    private final GameRepository games;
    private final GameValidator validator;
    private final GameFileManager files;

    public GameController(BrokerRepository brokers, GameRepository games, GameValidator validator, GameFileManager files) {
        this.brokers = brokers;
        this.games = games;
        this.validator = validator;
        this.files = files;
    }

    @GetMapping("/")
    public ResponseEntity<Collection<Game>> getGames() {
        Collection<Game> games = this.games.findAll();
        return ResponseEntity.ok().body(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable("id") String id) {
        Game game = this.games.findById(id);
        if (null != game) {
            return ResponseEntity.ok().body(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<Map<FileRole, String>> getGameFiles(@PathVariable("id") String id) {
        Game game = this.games.findById(id);
        if (null != game) {
            return ResponseEntity.ok().body(files.getFiles(game));
        } else {
            return ResponseEntity.notFound().build();
        }
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
