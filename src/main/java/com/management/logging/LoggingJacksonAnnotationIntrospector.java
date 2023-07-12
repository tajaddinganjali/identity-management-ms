package com.management.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.management.util.LogUtil;

/**
 * Extension of <code>JacksonAnnotationIntrospector</code> slightly modified
 * to adjust to the needs of logging functionality of the microservice.
 */
public class LoggingJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    private static final long serialVersionUID = 3779203738359197262L;

    @Override
    public boolean _isIgnorable(Annotated annotated) {
        LogIgnore ann = _findAnnotation(annotated, LogIgnore.class);
        return ann != null;
    }

    @Override
    public Access findPropertyAccess(Annotated annotated) {
        JsonProperty ann = _findAnnotation(annotated, JsonProperty.class);
        if (ann != null) {
            return Access.AUTO;
        }
        return null;
    }

    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember annotatedMember) {
        return LogUtil.EXCLUSIONS.contains(annotatedMember.getName()) || super.hasIgnoreMarker(annotatedMember);
    }

}
