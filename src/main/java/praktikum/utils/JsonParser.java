package praktikum.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

import java.util.Arrays;
import java.util.List;

public class JsonParser {
    private static final Configuration gsonConf = Configuration
            .builder()
            .jsonProvider(new GsonJsonProvider())
            .mappingProvider(new GsonMappingProvider())
            .build();

    private static <T> T getObject(String json, Class<T> objectClass, String jsonPath) {
        return JsonPath.using(gsonConf).parse(json).read(jsonPath, objectClass);
    }

    public static <T> List<T> getObjectsList(String json, Class<T[]> objectClass, String jsonPath) {
        return Arrays.asList(getObject(json, objectClass, jsonPath));
    }
}
