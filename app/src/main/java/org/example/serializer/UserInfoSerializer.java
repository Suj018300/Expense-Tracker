package org.example.serializer;


import org.apache.kafka.common.serialization.Serializer;
import org.example.model.UserInfoDto;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoDto> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize (String arg0, UserInfoDto arg1 ) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(arg1).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public void close() {};
}
