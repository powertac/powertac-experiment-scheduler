package org.powertac.rachma.baseline;

import org.powertac.rachma.game.generator.GameGeneratorConfig;
import org.powertac.rachma.validation.exception.ValidationException;

public interface BaselineFactory {

    Baseline createFromSpec(BaselineSpec spec) throws ValidationException;
    Baseline generate(String name, GameGeneratorConfig config) throws ValidationException;

}
