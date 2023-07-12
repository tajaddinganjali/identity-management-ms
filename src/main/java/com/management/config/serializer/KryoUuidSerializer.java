package com.management.config.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.UUID;

public class KryoUuidSerializer extends Serializer<UUID> {

    @Override
    public void write(Kryo kryo, Output output, UUID object) {
        output.writeString(object.toString());
    }

    @Override
    public UUID read(Kryo kryo, Input input, Class<UUID> type) {
        String id = input.readString();
        return UUID.fromString(id);
    }

}
