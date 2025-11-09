package utn.back.mordiscoapi.common.constraint;

import org.springframework.stereotype.Component;

@Component
public class ConstraintViolationMessageResolver {
    /**
     * Resolves a detailed message from a constraint violation.
     *
     * @param detailedMessage the detailed message to resolve
     * @return the resolved message
     */
    public String resolveMessage(String detailedMessage) {
        return DataBaseConstraint.resolveMessage(detailedMessage);
    }
}