package com.example.clientserver.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {

    public JSONObject getJsonResponse(String response){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return objectMapper.readValue(response, JSONObject.class);
        } catch (JsonProcessingException e){
            e.getStackTrace();
            return new JSONObject();
        }
    }
}
