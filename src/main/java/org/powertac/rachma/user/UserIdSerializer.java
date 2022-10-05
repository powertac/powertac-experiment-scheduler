package org.powertac.rachma.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class UserIdSerializer extends StdSerializer<User> {

    public UserIdSerializer() {
        super(User.class);
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(user != null ? user.getId() : null);
    }
}
