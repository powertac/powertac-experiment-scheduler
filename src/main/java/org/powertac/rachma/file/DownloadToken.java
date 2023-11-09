package org.powertac.rachma.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.powertac.rachma.user.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DownloadTokenId.class)
public class DownloadToken implements Serializable {

    @Id
    @ManyToOne
    @Getter
    private User user;

    @Id
    @Getter
    private String filePath;

    @Getter
    @Column(length = 999)
    private String token;

}
