package org.dotspace.oofp.model.dto.itemsearch;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ItemSearchResult {
    private ItemSearchRequest request;
    private String resultCode;
    private Map<String, Object> resultDetails;
    private List<Map<String, String>> items;
}
