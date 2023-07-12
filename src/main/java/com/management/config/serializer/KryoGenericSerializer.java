package com.management.config.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import org.springframework.data.redis.serializer.RedisSerializer;

public class KryoGenericSerializer<T> implements RedisSerializer<T> {

    @Override
    public byte[] serialize(T param) {
        Kryo kryo = new Kryo();
        kryo.register(UUID.class, new KryoUuidSerializer());
        ByteArrayOutputStream objStream = new ByteArrayOutputStream();
        Output objOutput = new Output(objStream);
        kryo.writeClassAndObject(objOutput, param);
        objOutput.close();
        return objStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) {
        Kryo kryo = new Kryo();
        kryo.register(UUID.class, new KryoUuidSerializer());
        return (T) kryo.readClassAndObject(new Input(bytes));
    }

}
