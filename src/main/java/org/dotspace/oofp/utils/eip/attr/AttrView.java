package org.dotspace.oofp.utils.eip.attr;

import org.dotspace.oofp.utils.eip.AttrKey;
import org.dotspace.oofp.utils.functional.monad.Maybe;

@FunctionalInterface
public interface AttrView {
    Maybe<Object> get(AttrKey<Object> attrKey);
}
