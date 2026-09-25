# solutions50

## What is this application in a nutshell?

It is a small CLI application that allows teachers to grant access to GitHub repositories on a per-group basis in a faster and more convenient way than with GitHub's web interface.

This tool is implemented in Java and requires JDK 21 or later.

> **Note:** This application is part of a toolkit for managing classrooms created with [Classroom50](https://github.com/foundation50/classroom50/wiki). We strongly recommend reading the [main toolkit repository](https://github.com/raul-izquierdo/classroom-50-tools) first to get an overview of the project and understand where this application fits.

## Who is this for?

This tool is intended for teachers who:
1. Teach in laboratories where students have access to computers.
2. Explain, during class, the solution to an exercise that students have been working on, either in class or at home. During the explanation, the teacher does not want students to have access to the solution code yet, so they can focus on the key points being explained.
3. Want students to access the solution once the explanation is over, so they can review it in more detail on their own computers and at their own pace. The goal is for students to compare it with their own solution, ask questions to reinforce their understanding, and suggest alternative solutions.

In short, the teacher wants students to analyze the solution **during class** instead of leaving the code for them to review at home. This takes advantage of the fact that both the exercise and the solution are still fresh in their minds, allowing them to ask questions and resolve doubts immediately.

## The Problem

This only works if the time between the end of the explanation and the moment when the code is available in the students' IDEs is kept to a minimum, so the flow of the class is not interrupted.

However, students must go through all of the following steps to view the solution:
1. The teacher must open a browser and go to GitHub. Once there, they must find the solution repository, open the _Teams_ section under _Settings_, find the GitHub _team_ corresponding to the current group, and grant it access to the solution.
2. The students must then find the repository on GitHub, download it to their computers, open their IDE, and finally _import_ the project into it.

This is a tedious, error-prone process that interrupts the flow of the class, since it is easy to select the wrong organization, repository, or group. To avoid this, teachers often leave the solution somewhere for students to review at home, allowing the class to continue without interruptions.

## How does the app simplify this process?

The _solutions50_ tool automates step 1 above. Once the explanation is over, the teacher only has to click an icon on their computer. The tool automatically grants access to the solution being explained to the group currently in class, without requiring either one to be specified. This makes the process immediate.

_solutions50_ determines the group and solution to display as follows:
- The group is determined using the information in `groups.csv`, which describes each group's schedule. This small file is created at the beginning of the course (see [Configuration](#configuration)).
- The solution being explained is determined by finding the first repository that the current group does not have access to. This assumes that solutions will be explained in order, although solutions can also be shown in a different order.

Step 2 of the problem, giving students access to the solution, does not require any tool from this toolkit. The time required for this process can be reduced drastically by using features that GitHub already provides (see [Accessing the solution from the students' computers](#accessing-the-solution-from-the-students-computers)).

By combining _solutions50_ with the GitHub features described above, students can start analyzing the solution on their computers within about five seconds of the teacher finishing the explanation, keeping the class moving smoothly.

## Installation

> ⚠️ This section is for those who want to use this tool independently. If you are going to use it together with the rest of the toolkit (recommended), follow the instructions in the toolkit's [main repository](https://github.com/raul-izquierdo/classroom-50-tools#toolkit-installation). You do not need to repeat these steps because they are already included there.

Download [solutions50.jar](https://github.com/raul-izquierdo/solutions50/releases/latest/download/solutions50.jar) from the latest release of this repository.

To verify that it works, run the _version_ or _help_ command:

```bash
java -jar solutions50.jar -V
```

```bash
java -jar solutions50.jar -h
```

## Configuration

> ⚠️ This section is for those who want to use this tool independently. If you are going to use it together with the rest of the toolkit (recommended), follow the instructions in the toolkit's [main repository](https://github.com/raul-izquierdo/classroom-50-tools#toolkit-configuration). You do not need to repeat these steps because they are already included there.

To configure the application, follow these steps:

1. (Optional) Create a `.env` file with the required environment variables.
    ```env
    SOLUTIONS_ORG=<organization that contains the repositories with the solutions>
    GITHUB_TOKEN=<GitHub token - see below for instructions>
    ```

    This step is optional but _highly_ recommended, as it allows you to run _solutions50_ without specifying command-line flags. If you prefer not to create it, make sure to use the appropriate command-line flags.

    Here's how to obtain values for the above variables:
    - `SOLUTIONS_ORG` should contain the name of the organization that contains the solution repositories.
    - `GITHUB_TOKEN` should contain a GitHub personal access token with the `repo` and `admin:org` scopes. See the [GitHub documentation: Creating a personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic) for instructions.

2. Create a `groups.csv` file with the schedules for your assigned groups. The columns are: group name, day of the week, start time and duration (if no duration is specified, a default of 2 hours is assumed). Example:
    ```csv
    01, monday, 21:00
    02, tuesday, 14:00, 3h
    i01, wednesday, 16:00, 45m
    ```

    For more information about the format of this file, see [groups file format](#groups-file-format).

3. (Optional but highly recommended) Create a BAT file (Windows) or shell script (Linux/macOS) that runs `java -jar solutions50.jar`, and then create a desktop shortcut to that script. This lets you launch the tool as quickly and easily as possible during class, without opening a terminal or typing commands.


## Application Requirements

This application requires a GitHub organization containing the following:
- Repositories with the solutions.
    - The solutions must follow the naming convention described in [Solutions Naming](#solutions-naming).
- One _team_ for each student group.
    - Teams can be created manually through the GitHub web interface or automatically with the [teams50](https://github.com/raul-izquierdo/teams50) tool (recommended).


### Solutions Naming

#### Name Format

As mentioned above, solutions must be repositories in a GitHub organization. The organization may contain additional repositories that are not exercise solutions. By default, a repository is considered a _solution_ if its name **ends** with the word "_solution_" (because the default regex is `.*solution$`). This can be changed with the `-e` option by providing a different regular expression.

For example:
- To match repositories that contains the word "_solution_" anywhere in the name:
    ```sh
    java -jar solutions50.jar -e ".*solution.*"
    ```
- To match repositories that start with the word "_solution_":
    ```sh
    java -jar solutions50.jar -e "^solution.*"
    ```

The regular expression must follow the syntax described in [Java Pattern](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html).

#### Ordering

_solutions50_ determines which solution to show to the current group by sorting all solutions alphabetically and selecting the first one that the group does not have access to.

Therefore, solution repositories should be named so that their alphabetical order matches the _order in which the solutions should be presented during the course_. For example: `01-factorial-solution`, `session_01...`, and so on.

## Execution

To grant access to the solution repository to the team of the current group, simply run the application.

```bash
java -jar solutions50.jar
```

It will automatically determine which team corresponds to the current group and which solution is being explained, and grant access accordingly.
- The group is determined using the information in `groups.csv` and the current date.
- The solution is determined by finding the first repository that the current group does not have access to.

If either of these cannot be determined automatically, the application displays a menu with the available groups and solutions and asks the teacher to select the correct options.
- If a solution that the group already has access to is selected, the application allows that access to be **revoked**.

If you want to check what changes would be made without actually performing them, run the tool with the `--dry-run` option. This will display the actions that would be performed without making any changes.
```bash
java -jar solutions50.jar --dry-run
```

## Accessing the solution from the students' computers

To make access to the solution as easy as possible during class, the following actions are recommended:
- **Provide the solution URL**. Even after the teacher grants access to the solution, students would still have to find it on GitHub. To avoid this, the teacher should provide the solution URL together with the exercise's starter code, for example in its `README.md`. Once access is granted, students will already have the link and only need to click it. If they click the link before access is granted, GitHub will indicate that they do not yet have access to the repository (as expected).
- **Use Visual Studio Code for the Web**. Once a student is on the solution's GitHub page, they can press the `.` (period) key to open the project immediately in Visual Studio Code for the Web. Students can browse the solution code without downloading it or installing anything.

These steps allow students to access the solution within about five seconds of the teacher finishing the explanation, without installing or configuring anything on their computers.

## Groups File Format

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

Example (all sessions are 2 hours):
```csv
G4, thursday, 12:00
G3, wednesday, 8:15, 2
G1, monday, 10:00, 2h
G2, tuesday, 09:30, 120m
```

Notes:
- The CSV file should not include a header row
- If a group appears more than once, the **last entry** is used

> **NOTE:** Multiple timeslots per group (multiple classes per week) are not currently supported. This feature can be added if requested.


## Command-Line Arguments

Syntax:

```bash
java -jar solutions50.jar [flags]
```

Flags:
- **-t [token]**: GitHub API access token. If not provided, it will try to read from the GITHUB_TOKEN environment variable or from a `.env` file.
- **-s [solutions-org]**: GitHub organization where the solutions are stored. If not provided, it will try to read from the SOLUTIONS_ORG environment variable or from a `.env` file.
- **-g [groups.csv]**: The CSV file with the groups schedule. If not provided, defaults to `groups.csv` if it exists. If not, automatic selection will be disabled and the team and solution to show must be specified manually.
- **-e [regex]**: A regular expression to identify solution repositories. Defaults to `.*solution$` (i.e., any repository whose name ends with "solution").
- **--dry-run**: Do not perform any changes; only read and print the actions that would be performed.
- **-h, --help**: Show help.
- **-V, --version**: Show version.

## Exit Codes

The exit codes indicate the result of command execution:
- **0**: The command executed successfully.
- **1**: An error occurred.

## License

See `LICENSE`.
Copyright (c) 2025 Raul Izquierdo Castanedo


---
<style>p:has(+ :is(ul,ol)) { margin-bottom: 0; }</style>
