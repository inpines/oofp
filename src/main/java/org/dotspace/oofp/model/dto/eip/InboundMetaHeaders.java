package org.dotspace.oofp.model.dto.eip;

public final class InboundMetaHeaders {

    private InboundMetaHeaders() {}

    /** W3C Trace Context（如果未來要接 OpenTelemetry） */
    public static final String TRACE_PARENT = "traceparent";

    /** 自家簡化版 trace id（目前主用） */
    public static final String TRACE_ID = "X-Trace-Id";

    /** Request id（client / gateway） */
    public static final String REQUEST_ID = "X-Request-Id";

    /** Forwarded IP */
    public static final String FORWARDED_FOR = "X-Forwarded-For";

    /** User agent */
    public static final String USER_AGENT = "User-Agent";

}
