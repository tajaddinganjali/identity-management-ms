package com.management.util.validation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;

@Slf4j
public class PasscodeValidator implements ConstraintValidator<ValidPasscode, String> {

    @Override
    public boolean isValid(String passcode, ConstraintValidatorContext context) {
        PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(
                new LengthRule(4, 4),
                new CharacterRule(EnglishCharacterData.Digit, 4)
        ));

        PasswordData passwordData = new PasswordData(passcode);

        RuleResult ruleResult = passwordValidator.validate(passwordData);

        boolean isValid = ruleResult.isValid();
        if (!isValid) {
            List<String> errors =
                    ruleResult.getDetails().stream().map(RuleResultDetail::getErrorCode).collect(Collectors.toList());
            log.error(String.format("Validation of %s passcode failed with errors %s", passcode, errors));
        }

        return ruleResult.isValid();
    }

}
