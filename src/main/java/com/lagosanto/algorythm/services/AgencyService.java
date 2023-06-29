package com.lagosanto.algorythm.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagosanto.algorythm.models.Agency;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgencyService {

    @Value("${project.api.urlbase}")
    private String DEFAULT_URL;
    private static final String ALL_AGENCIES = "agencies/";
    private static final String AGENCY = "agency/";

    private ObjectMapper mapper = new ObjectMapper();

    private Connection.Response connectAndGetResponse(String url) throws IOException {
        return Jsoup.connect(url)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
    }

    private Agency mapJsonNodeToAgency(JsonNode objNode) {
        int id = objNode.get("id").asInt();
        String code = objNode.get("code").asText();
        String libelle = objNode.get("libelle").asText();
        return new Agency(id, code, libelle);
    }

    public List<Agency> getAllAgencies() throws IOException {
        Connection.Response response = connectAndGetResponse(DEFAULT_URL + ALL_AGENCIES);

        if (response.statusCode() == 200) {
            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Agency> agencies = new ArrayList<>();

            for (final JsonNode objNode : json) {
                Agency agency = mapJsonNodeToAgency(objNode);
                agencies.add(agency);
            }
            return agencies;
        } else {
            throw new IOException("HTTP code " + response.statusCode() + " to get all agencies");
        }
    }

    public Agency getAgency(int idParameter) throws IOException {
        Connection.Response response = connectAndGetResponse(DEFAULT_URL + AGENCY + idParameter);

        if (response.statusCode() == 200) {
            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            Agency agency = null;
            for (final JsonNode objNode : json) {
                agency = mapJsonNodeToAgency(objNode);
            }
            return agency;
        } else {
            throw new IOException("HTTP code " + response.statusCode() + " to find agency at id : " + idParameter);
        }
    }
}
