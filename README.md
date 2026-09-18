# Solutions

Easily show or hide GitHub Classroom assignment solutions by granting or revoking team read access to solution repositories—often with just a single confirmation, thanks to automatic group and solution detection.

> **Note:** This tool is part of a broader toolkit for managing classes with GitHub Classroom. For an overview and to see how this fits in, check out the [main repository](https://github.com/raul-izquierdo/classroom-tools).

## See it in Action

The standout feature is automatic group and solution detection:
- Instantly identifies which group is currently in session using the schedule CSV and your system time
- Selects the next solution repository for that group

Just walk into class, run the tool, press `y`, and the right solution is unlocked for the right group.

Example `schedule.csv`:
```csv
G1,wednesday 08:00
G2,wednesday 09:00
G-english-1,wednesday 10:00
```

Running the tool:
```bash
$ java -jar solutions.jar -s schedule.csv

Connecting with GitHub... done.

[wednesday 08:15] You are currently with group 'G1'. Would you like to show them 'factorial-solution'? (y/N): y
Access granted.
```

> **NOTE:** For this functionality to work, specific naming conventions must be followed. See [Naming Rules](#naming-rules) for details.

If automatic detection is not possible, you'll get an interactive picker:

```bash
Choose the group:
(type to filter or use arrows ↑/↓): G1
> G1
  G2
  G-english-1

Choose the solution:
(type to filter or use arrows ↑/↓):
  linked-list-solution [accessible]
> factorial-solution [hidden]

Grant access? (y/N): y
Access granted.
```

When you select a solution manually, its access is toggled (granted or revoked) immediately.

## Usage

The JAR can be downloaded from the [releases page](https://github.com/raul-izquierdo/solutions/releases).

```bash
java -jar solutions.jar [-s schedule.csv] [-o <organization>] [-t <token>] [--dry-run]
```

| Option              | Description                                             |
|---------------------|---------------------------------------------------------|
| `-s <schedule.csv>` | CSV file with the group schedule (default: `schedule.csv`). See [Schedule File Format](#schedule-file-format) for details. |
| `-o <organization>` | The organization where the solution repositories are located.                               |
| `-t <token>`        | GitHub API access token. For more details, see [Obtaining the GitHub token](https://github.com/raul-izquierdo/classroom-tools#obtaining-the-github-token). |
| `-r <regex>`        | Regular expression to detect solution repository names. Default = ".*solution$" (solutions are repositories whose names end with `solution`)                                    |
| `--dry-run`         | Preview mode: shows what changes would be made without actually modifying repository permissions.                                    |

If you don't provide `-o` or `-t`, the tool will look for `GITHUB_ORG` and `GITHUB_TOKEN` in a `.env` file in your working directory:
```dotenv
GITHUB_ORG=<your-org>
GITHUB_TOKEN=<token>
```

**Note:** The required organization is the one that contains the solution repositories. Depending on your preferences, this may differ from the organization linked to GitHub Classroom. Some instructors prefer to store solutions in a separate organization from the one used for assignments (which is my recommendation). In this case, be sure to specify the organization containing the solutions here.

## Schedule File Format

Your CSV should have one line per group, following this format:

```csv
<groupLabel>, <weekday>, <start-time>[, <duration>]
```

- **groupLabel:** Any text (the group's name)
- **weekday:** monday, tuesday, wednesday, thursday, friday, saturday, or sunday
- **start-time:** `H:mm`, `HH:mm`, or `H` (e.g., `8:15`, `09:30`, `8`), between 08:00 and 21:00
- **duration** (optional):
  - Defaults to 2 hours if omitted
  - Accepts:
    - Hours: `2` or `2h`
    - Minutes: `120m`

Notes:
- The CSV file should not include a header row
- If a group appears more than once, the last entry is used

Example (all sessions are 2 hours):
```csv
G4, thursday, 12:00
G3, wednesday, 8:15, 2
G1, monday, 10:00, 2h
G2, tuesday, 09:30, 120m
```

> **NOTE:** Multiple timeslots per group (multiple classes per week) are not currently supported. This feature can be added if requested.

## Naming Rules

To help the tool recognize which _teams_ are _groups_ and which _repositories_ are _solutions_, follow these conventions:

### Repository Names for Solutions

1. By default, a repository is considered a _solution_ if its name **ends** with the word "_solution_" (because the default regex is `.*solution$`). This can be changed using the `-r` option to provide a different regular expression.

    For example:
    - To match repositories that contains the word "_solution_" anywhere in the name:
        ```sh
        java -jar solutions.jar -r ".*solution.*"
        ```
    - To match repositories that start with the word "_solution_":
        ```sh
        java -jar solutions.jar -r "^solution.*"
        ```
2. For **automatic** solution selection, name your solution repositories so that sorting them **alphabetically** matches the order you want to _reveal_ them. A simple way to achieve this is to use a prefix that includes the class number: `01-factorial-solution`, `class_01...`, etc.

In summary:
- _Start_ the repository name with the class number.
- _End_ it with `solution` (or change the regex accordingly).

Examples (note that separators don't matter): `01-factorial-solution`, `class_01_factorial_solution`, `c04.strategy-pattern.solution`.
Choose one of these styles and stick with it for all your solution repositories.

### Team Names for Groups

A team is recognized as a group if its name follows the convention used by the [teams.jar](https://github.com/raul-izquierdo/teams) tool. If the teams were created with this tool, they will already follow this convention. However, if you create teams _manually_, make sure their names match the [generated team names for groups](https://github.com/raul-izquierdo/teams?tab=readme-ov-file#generated-team-names-for-groups) — specifically, prefix the group name with `group ` (note the space).


## License

See `LICENSE`.
Copyright (c) 2025 Raul Izquierdo Castanedo
