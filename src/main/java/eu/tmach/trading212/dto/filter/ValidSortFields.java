package eu.tmach.trading212.dto.filter;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // Anotace bude nad celou třídou
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortFieldsValidator.class)
public @interface ValidSortFields {
    String message() default "Neplatné pole pro řazení";
    String[] allowed(); // Tady definujeme povolená pole
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}