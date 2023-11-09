package org.powertac.rachma.file;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.powertac.rachma.user.User;

import javax.persistence.ManyToOne;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class DownloadTokenId implements Serializable {

    String user;
    String filePath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadTokenId that = (DownloadTokenId) o;
        if (!user.equals(that.user)) return false;
        return filePath.equals(that.filePath);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + filePath.hashCode();
        return result;
    }

}
