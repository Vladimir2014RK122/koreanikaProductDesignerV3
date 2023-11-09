package ru.koreanika.common;

import org.json.simple.JSONObject;

public interface RepresentToJson {

    JSONObject getJsonView();

    void initFromJson(JSONObject jsonObject);
}
