package com.management.util.validation;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.passay.AllowedRegexRule;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RepeatCharacterRegexRule;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;

@Slf4j
public class PinValidator implements ConstraintValidator<ValidPin, String> {

    @Override
    public boolean isValid(String pin, ConstraintValidatorContext constraintValidatorContext) {

        PasswordValidator validator = new PasswordValidator(
                // length between 8 and 16 characters
                new LengthRule(5, 7),
                new AllowedRegexRule("^[A-Z0-9]+$"),
                new RepeatCharacterRegexRule(7),
                // define some illegal sequences that will fail when >= 5 chars long
                // alphabetical is of the form 'abcde', numerical is '34567', qwery is 'asdfg'
                // the false parameter indicates that wrapped sequences are allowed; e.g. 'xyzabc'
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 7, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 7, false)
        );

        RuleResult result = validator.validate(new PasswordData(pin));
        boolean isValid = result.isValid();
        if (!isValid) {
            List<String> errors =
                    result.getDetails().stream().map(RuleResultDetail::getErrorCode).collect(Collectors.toList());
            log.error(String.format("Validation of %s pin failed with errors %s", pin, errors));
        }

        return isValid;
    }

}
