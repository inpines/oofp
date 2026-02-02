package org.dotspace.oofp.utils.eip.attr;

@FunctionalInterface
public interface ServiceRequestExtractor<T, R> {
    R extract(T payload, AttrView attrView);
}
