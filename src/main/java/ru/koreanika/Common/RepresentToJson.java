package ru.koreanika.Common;

import org.json.simple.JSONObject;

public interface RepresentToJson {

    JSONObject getJsonView();

    void initFromJson(JSONObject jsonObject);
}
