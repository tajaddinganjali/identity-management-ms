package com.management.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.management.identity.enums.ProfileType;
import com.management.model.Customer;
import com.management.model.User;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserUtil {

    public static final ObjectMapper OBJECT_MAPPER_SNAKE_CASE = createObjectMapper(PropertyNamingStrategy.SNAKE_CASE);
    public static final ObjectMapper OBJECT_MAPPER_CAMEL_CASE =
            createObjectMapper(PropertyNamingStrategy.LOWER_CAMEL_CASE);

    private static ObjectMapper createObjectMapper(PropertyNamingStrategy strategy) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(strategy);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;

    }

    public static User mergeUser(User userFromDB, User userFoundFromProvider) {
        //Business logic

        return userFromDB;
    }

    public static User convertCustomerListToUser(String phone, List<Customer> customers,
                                                 ProfileType profileType) {
        //Business logic

        return new User();
    }

}
