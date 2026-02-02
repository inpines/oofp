package org.dotspace.oofp.model.dto.itemsearch;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dotspace.oofp.utils.eip.AttrKey;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemSearchAttrKeys {

    public final AttrKey<ItemSearchResult> ITEM_SEARCH_RESULT = AttrKey.of(
            "item-search.result", new TypeReference<>() {});

}
