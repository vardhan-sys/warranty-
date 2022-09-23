package com.geaviation.techpubs.service.util.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.geaviation.techpubs.services.util.admin.AdminAppUtil;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Component
public class AdminAppUtilTest {

    @InjectMocks
    private AdminAppUtil adminAppUtil;

    @Test
    public void parseMdmCompanyEngineModelResponse_Test() throws JSONException {
        JSONObject engineSeriesJson = new JSONObject();
        engineSeriesJson.put("CF34-10E", Lists.newArrayList("CF34-10E5A1"));
        engineSeriesJson.put("CF34-3", Lists.newArrayList("CF34-3A1", "CF34-3B", "CF34-3B1"));
        engineSeriesJson.put("CF34-8C", Lists.newArrayList("CF34-8C5", "CF34-8C5B1"));
        engineSeriesJson.put("CF34-8E", Lists.newArrayList("CF34-8E5"));

        JSONObject engineJson = new JSONObject();
        engineJson.put("CF34", engineSeriesJson);

        JSONObject engineFamilyJson = new JSONObject();
        engineFamilyJson.put("engineFamilies",  engineJson);

        StringBuilder stringBuilder = new StringBuilder(engineFamilyJson.toString());
        Map<String, List<String>> testMap = adminAppUtil.parseMdmCompanyEngineModelResponse(stringBuilder);

        assertEquals(testMap.keySet().size(), 1);
        assertEquals(testMap.get("CF34").size(), 4);
        assertTrue(testMap.get("CF34").containsAll(Lists.newArrayList(engineSeriesJson.keys())));
    }
}