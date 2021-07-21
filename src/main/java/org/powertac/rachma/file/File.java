package org.powertac.rachma.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.powertac.rachma.game.Game;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @Getter
    @Column(length = 36)
    private String id;

    @Getter
    private FileRole role;

    @Getter
    @ManyToOne
    private Game game;

}
