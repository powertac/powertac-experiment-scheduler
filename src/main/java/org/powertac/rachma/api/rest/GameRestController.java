package org.powertac.rachma.api.rest;

import org.powertac.rachma.game.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/games")
public class GameRestController {

    private final GameRepository games;
    private final GameFactory gameFactory;
    private final GameValidator validator;

    public GameRestController(GameRepository games, GameFactory gameFactory, GameValidator validator) {
        this.games = games;
        this.gameFactory = gameFactory;
        this.validator = validator;
    }

    @GetMapping("/")
    public ResponseEntity<Collection<Game>> getGames() {
        Collection<Game> games = this.games.findAll();
        return ResponseEntity.ok().body(games);
    }

    @PostMapping("/")
    public ResponseEntity<?> queueInstance(@RequestBody GameSpec spec) {
        try {
            Game game = gameFactory.createFromSpec(spec);
            validator.validate(game);
            games.save(game);
            return ResponseEntity.ok().build();
        } catch (GameValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

}
