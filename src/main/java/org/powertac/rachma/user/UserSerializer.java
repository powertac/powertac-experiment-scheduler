package org.powertac.rachma.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.rachma.user.domain.User;
import org.powertac.rachma.user.domain.UserRole;

import java.io.IOException;
import java.util.Collection;

public class UserSerializer extends StdSerializer<User> {

    public UserSerializer() {
        super(User.class);
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (null != user) {
            gen.writeStartObject();
            gen.writeStringField("id", user.getId());
            gen.writeStringField("username", user.getUsername());
            writeRoles(user.getRoles(), gen);
            gen.writeBooleanField("enabled", user.isEnabled());
            gen.writeEndObject();
        } else {
            throw new IOException("supplied user is null");
        }
    }

    private void writeRoles(Collection<UserRole> roles, JsonGenerator gen) throws IOException {
        gen.writeArrayFieldStart("roles");
        for (UserRole role : roles) {
            gen.writeString(role.getName());
        }
        gen.writeEndArray();
    }

}
