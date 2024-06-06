package com.cmhq.core.service.domain;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Jiyang.Zheng on 2024/6/6 18:57.
 */
public class ImportCourierOrderDomain {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private void checkParam(Object data) throws ValidationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(data);
        Iterator<ConstraintViolation<Object>> iterator = constraintViolations.iterator();
        if (iterator.hasNext()) {
            String message = iterator.next().getMessage();
            throw new ValidationException(message);
        }
    }
}
