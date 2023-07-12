package com.management.logging;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates the contexts in which logging is not applicable.
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
public @interface LogIgnore {

}
