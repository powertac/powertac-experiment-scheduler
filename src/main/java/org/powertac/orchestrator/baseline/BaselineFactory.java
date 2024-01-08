package org.powertac.orchestrator.baseline;

import org.powertac.orchestrator.game.generator.GameGeneratorConfig;
import org.powertac.orchestrator.validation.exception.ValidationException;

public interface BaselineFactory {

    Baseline createFromSpec(BaselineSpec spec) throws ValidationException;
    Baseline generate(String name, GameGeneratorConfig config) throws ValidationException;

}
