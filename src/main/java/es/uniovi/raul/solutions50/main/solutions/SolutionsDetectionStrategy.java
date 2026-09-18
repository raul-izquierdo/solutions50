package es.uniovi.raul.solutions50.main.solutions;

/**
 * An interface to identify solution repositories.
 */
@FunctionalInterface
public interface SolutionsDetectionStrategy {
    /**
     * Indicates whether the given repository name corresponds to a solution repository.
     */
    boolean isSolutionRepository(String repository);

}
