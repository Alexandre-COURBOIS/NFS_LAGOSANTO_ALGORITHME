package com.lagosanto.algorythm.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagosanto.algorythm.models.WorkUnit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkUnitService {
    @Value("${project.api.urlbase}")
    private String URLBASE;

    private static final String URL_WORKUNITS = "workunits/";
    private static final String URL_WORKUNIT = "workunit/";
    private static final String URL_WORKUNITS_BY_OPERATION = "workunit_by_operationid/";
    private ObjectMapper mapper = new ObjectMapper();

    public List<WorkUnit> getAllWorkUnits() throws IOException {
        return handleWorkUnitRequest(URLBASE + URL_WORKUNITS, true);
    }

    public WorkUnit getWorkUnit(int idParameter) throws IOException {
        return handleWorkUnitRequest(URLBASE + URL_WORKUNIT + idParameter, false).get(0);
    }

    public List<WorkUnit> getWorkUnitsByOperation(int idOperation) throws IOException {
        return handleWorkUnitRequest(URLBASE + URL_WORKUNITS_BY_OPERATION + idOperation, true);
    }

    private List<WorkUnit> handleWorkUnitRequest(String url, boolean expectMultiple) throws IOException {
        Connection.Response response = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            if(expectMultiple) {
                return parseMultipleWorkUnits(response);
            } else {
                return List.of(parseWorkUnit(response));
            }
        } else {
            throw new IOException("HTTP code " + response.statusCode() + " for URL: " + url);
        }
    }

    private List<WorkUnit> parseMultipleWorkUnits(Connection.Response response) throws JsonProcessingException {
        String jsonText = response.body();
        JsonNode json = mapper.readTree(jsonText);
        List<WorkUnit> workUnits = new ArrayList<>();
        json.forEach(objNode -> workUnits.add(parseWorkUnit(objNode)));
        return workUnits;
    }

    private WorkUnit parseWorkUnit(JsonNode objNode) {
        int id = objNode.get("id").asInt();
        String code = objNode.get("code").asText();
        String libelle = objNode.get("libelle").asText();
        return new WorkUnit(id, code, libelle);
    }

    private WorkUnit parseWorkUnit(Connection.Response response) throws JsonProcessingException {
        String jsonText = response.body();
        JsonNode json = mapper.readTree(jsonText);
        return parseWorkUnit(json.get(0));
    }
}
