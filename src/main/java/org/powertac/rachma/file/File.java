package org.powertac.rachma.file;

import lombok.*;
import org.powertac.rachma.game.Game;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @Getter
    @Column(length = 36)
    private String id;

    @Getter
    @Deprecated // please use labels
    private FileRole role;

    @Getter
    @ManyToOne
    private Game game;

    @Getter
    @Setter
    private String relativePath;

    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_labels", joinColumns = {@JoinColumn(name = "file_id", referencedColumnName = "id")})
    @Column(name = "label")
    private Set<String> labels = new HashSet<>();

    public File addLabel(String label) {
        labels.add(label);
        return this;
    }

}
