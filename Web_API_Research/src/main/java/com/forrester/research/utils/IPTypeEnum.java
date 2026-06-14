package com.forrester.research.utils;

import com.forrester.research.LogErrorMessages;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum IPTypeEnum {

    DATA_SNAPSHOT(Collections.singletonList("Data Snapshot"), "REPORT:DATA_SNAPSHOT"),
    DATA_OVERVIEW(Collections.singletonList("Data Overview"), "REPORT:DATA_OVERVIEW"),
    WAVE(Arrays.asList("Wave", "New Wave"), "REPORT:WAVE"),
    CERT(Collections.singletonList("Certification"), "REPORT:CERTIFICATION");

    private final List<String> ipTypes;
    private final String experienceCode;

    IPTypeEnum(List<String> ipTypes, String experienceCode) {
        this.ipTypes = ipTypes;
        this.experienceCode = experienceCode;
    }

    public static IPTypeEnum findBy(String ipType) {
        return Arrays.stream(values()).filter(f -> f.getIpTypes().contains(ipType)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(LogErrorMessages.INVALID_IPTYPE
                        + Arrays.stream(values()).map(f -> f.getIpTypes().stream().map(String::toLowerCase).collect(Collectors.joining(", "))).collect(Collectors.joining(", "))));
    }

    public List<String> getIpTypes() {
        return ipTypes;
    }

    public String getExperienceCode() {
        return experienceCode;
    }
}
