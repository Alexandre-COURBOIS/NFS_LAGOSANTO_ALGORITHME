package com.lagosanto.algorythm.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagosanto.algorythm.models.Operation;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperationService {
    @Value("${project.api.urlbase}")
    private String URLBASE;
    private static final String URL_OPERATIONS = "operations/";
    private static final String URL_OPERATION = "operation/";
    private static final String URL_OPERATIONS_BY_WORKUNIT = "operations_by_workunit/";

    public List<Operation> getAllOperations() throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + URL_OPERATIONS);
        return parseOperationsFromJson(json);
    }

    public Operation getOperation(int idParameter) throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + URL_OPERATION + idParameter);
        return parseSingleOperationFromJson(json);
    }

    public List<Operation> getOperationsByWorkUnit(int idWorkUnit) throws IOException {
        JsonNode json = getJsonNodeFromResponse(URLBASE + URL_OPERATIONS_BY_WORKUNIT + idWorkUnit);
        return parseOperationsFromJsonFromWorkunit(json)
                .stream()
                .filter(operation -> {
                    try {
                        return getAllOperations().stream()
                                .anyMatch(o -> o.getId() == operation.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private JsonNode getJsonNodeFromResponse(String url) throws IOException {
        Connection.Response response = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.body());
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " for url : " + url);
        }
    }

    private List<Operation> parseOperationsFromJson(JsonNode json) {
        List<Operation> operations = new ArrayList<>();
        for (final JsonNode objNode : json) {
            Operation operation = createOperationFromJsonNode(objNode);
            operations.add(operation);
        }
        return operations;
    }
    private List<Operation> parseOperationsFromJsonFromWorkunit(JsonNode json) {
        List<Operation> operations = new ArrayList<>();
        for (final JsonNode objNode : json) {
            Operation operation = createOperationFromJsonNodeFromWorkUnit(objNode);
            operations.add(operation);
        }
        return operations;
    }

    private Operation parseSingleOperationFromJson(JsonNode json) {
        Operation operation = null;
        for (final JsonNode objNode : json) {
            operation = createOperationFromJsonNode(objNode);
        }
        return operation;
    }

    private Operation createOperationFromJsonNode(JsonNode objNode) {
        int id = objNode.get("id").asInt();
        String code = objNode.get("code").asText();
        String libelle = objNode.get("libelle").asText();
        int delai = objNode.get("delai").asInt();
        int delaiInstallation = objNode.get("delaiInstallation").asInt();
        return new Operation(id,code,libelle,delai,delaiInstallation);
    }

    private Operation createOperationFromJsonNodeFromWorkUnit(JsonNode objNode) {
        int id = objNode.get("id").asInt();
        String code = objNode.get("code").asText();
        return new Operation(id,code);
    }
}

