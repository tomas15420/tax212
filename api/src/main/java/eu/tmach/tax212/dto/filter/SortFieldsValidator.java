package eu.tmach.tax212.dto.filter;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class SortFieldsValidator implements ConstraintValidator<ValidSortFields, PageableFilter> {
    private Set<String> allowedFields;

    @Override
    public void initialize(ValidSortFields constraintAnnotation) {
        this.allowedFields = Set.of(constraintAnnotation.allowed());
    }

    public boolean isValid(PageableFilter value, ConstraintValidatorContext context) {
        if (value == null || value.getSort() == null || value.getSort().isEmpty()) {
            return true;
        }

        boolean isValid = true;

        for (SortOrder order : value.getSort()) {
            if (order.getField() == null) {
                isValid = false;
                break;
            }

            if (!allowedFields.contains(order.getField())) {
                isValid = false;
                break;
            }

            String dir = order.getDir();
            if (dir == null || (!dir.equalsIgnoreCase("asc") && !dir.equalsIgnoreCase("desc"))) {
                isValid = false;
                break;
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("sort")
                    .addConstraintViolation();
        }

        return isValid;
    }
}