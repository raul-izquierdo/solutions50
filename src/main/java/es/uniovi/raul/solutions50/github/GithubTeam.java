package es.uniovi.raul.solutions50.github;

/**
 * Represents a team in the organization.
 * Each team has a display name, a slug (unique identifier)
 *
 * @param organization the name of the organization to which the team belongs
 * @param name the display name of the team
 * @param slug the unique identifier (slug) of the team
 */
public record GithubTeam(String organization, String name, String slug) {

    public GithubTeam {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Display name cannot be null or blank.");

        if (slug == null || slug.isBlank())
            throw new IllegalArgumentException("Slug cannot be null or blank.");
    }
}
