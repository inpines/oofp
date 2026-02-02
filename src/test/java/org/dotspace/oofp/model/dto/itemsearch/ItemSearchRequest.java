package org.dotspace.oofp.model.dto.itemsearch;

import lombok.Data;

import java.util.List;

@Data
public class ItemSearchRequest {

    private String keyword;

    private Filters filters;
    private Paging paging;
    private List<Sort> sort;

    // getters / setters

    @Data
    public static class Filters {
        private Integer signupNo;
        private String ssType;
        // getters / setters
    }

    @Data
    public static class Paging {
        private Integer page;
        private Integer size;
        // getters / setters
    }

    @Data
    public static class Sort {
        private String field;
        private String direction;
        // getters / setters
    }
}
