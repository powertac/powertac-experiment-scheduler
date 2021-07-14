package org.powertac.rachma.api.rest;

import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;
import org.powertac.rachma.game.GameValidationException;
import org.powertac.rachma.game.GameValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameRepository games;
    private final GameValidator validator;

    public GameController(GameRepository games, GameValidator validator) {
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
            validator.validate(game);
            games.save(game);
            return ResponseEntity.ok().build();
        } catch (GameValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
