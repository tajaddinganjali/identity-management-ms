package com.management.util.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PasscodeValidator.class)
public @interface ValidPasscode {

    /**
     * default message.
     */
    String message() default "Passcode not valid";

    /**
     * groups.
     */
    Class<?>[] groups() default {};

    /**
     * default payload.
     */
    Class<? extends Payload>[] payload() default {};

}
