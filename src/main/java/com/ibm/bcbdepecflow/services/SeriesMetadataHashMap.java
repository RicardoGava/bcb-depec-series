package com.ibm.bcbdepecflow.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Getter
@NoArgsConstructor
public class SeriesMetadataHashMap {
    private HashMap<String, String> seriesMetadataHashMap = new HashMap<>();

    public void putMetadataHashMap(String key, String value) {
        seriesMetadataHashMap.put(key, value);
    }

    public String getMetadataHashMap(String key) {
        return seriesMetadataHashMap.get(key);
    }

}
