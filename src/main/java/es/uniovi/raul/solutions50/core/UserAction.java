package es.uniovi.raul.solutions50.core;

import es.uniovi.raul.solutions50.github.GithubTeam;

/**
 * Represents a selection of a team and a repository.
 *
 * Used as a return type for the workflows (automatic or manual) to indicate what action to perform on what team and repository.
 *
 * @param type the type of action to perform (grant or revoke)
 * @param team the selected team
 * @param repository the selected repository
 */

public record UserAction(ActionType type, GithubTeam team, String repository) {

    public enum ActionType {
        GRANT_ACCESS, REVOKE_ACCESS
    }

}
