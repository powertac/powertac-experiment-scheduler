package org.powertac.rachma.treatment;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameConfig;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {

    @Id
    @Getter
    @Setter
    @Column(length = 36)
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @ManyToOne
    @JsonIgnore
    private Baseline baseline;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Modifier modifier;

    @Getter
    @Setter
    private Instant createdAt;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "treatment")
    @OrderColumn
    @JsonIgnore
    private List<Game> games;

    @JsonGetter
    public String getBaselineId() {
        return baseline.getId();
    }

    @JsonGetter
    public List<String> getGameIds() {
        return games.stream()
            .map(Game::getId)
            .collect(Collectors.toList());
    }

}
