package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.BrokerNotFoundException;
import org.powertac.rachma.game.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/games")
public class GameRestControllerV2 {

    private final GameRepository games;
    private final GameFactory gameFactory;
    private final GameValidator validator;
    private final GameFileManager gameFileManager;
    private final GameDTOMapper gameMapper;
    private final Logger logger;

    public GameRestControllerV2(GameRepository games,
                                GameFactory gameFactory,
                                GameValidator validator,
                                GameFileManager gameFileManager,
                                GameDTOMapper mapper) {
        this.games = games;
        this.gameFactory = gameFactory;
        this.validator = validator;
        this.gameFileManager = gameFileManager;
        this.gameMapper = mapper;
        logger = LogManager.getLogger(GameRestControllerV2.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(gameMapper.toDTO(games.findById(id)));
        } catch (Exception e) {
            logger.error("could not load game " + id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> findMany() {
        try {
            return ResponseEntity.ok(games.findAll().stream()
                .map(gameMapper::toDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("could not load games", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody NewGameDTO newGameData) {
        try {
            Game game = gameFactory.createFromDTO(newGameData);
            validator.validate(game);
            createGameFileScaffold(game); // FIXME : move to GameRunner (JIT-approach)
            games.save(game);
            return ResponseEntity.ok().build();
        } catch (IOException | GameValidationException | BrokerNotFoundException e) {
            logger.error("unable to create new game", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void createGameFileScaffold(Game game) throws IOException { // FIXME : move to GameRunner (JIT-approach)
        try {
          gameFileManager.createScaffold(game);
        } catch (IOException e) {
            gameFileManager.removeAllGameFiles(game);
            throw e;
        }
    }

}
