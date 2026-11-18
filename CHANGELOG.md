# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [2.2.0](https://github.com/raul-izquierdo/solutions50/compare/v2.1.0...v2.2.0) - 2026/11/18

### Changed

- Improved the format of confirmation messages in automatic selection mode


## [2.1.0](https://github.com/raul-izquierdo/solutions50/compare/v2.0.3...v2.1.0) - 2026/10/07

### Changed

- perf: Teams and solutions are fetched in parallel

### Fixed

- fix: Only 100 teams were fetched. This is more than enough for the current use case. If more than 100 teams are
  needed, the code should be updated to fetch all teams in a paginated way. Instead of implementing pagination, a
  error is logged when more than 100 teams are detected, and the user is advised to contact the maintainer

## [2.0.3](https://github.com/raul-izquierdo/solutions50/compare/v2.0.2...v2.0.3) - 2026/09/28


### Fixed

- Update repository field access from 'full_name' to 'name'
- Correct parameters order for grantAccess and revokeAccess methods



## [2.0.2](https://github.com/raul-izquierdo/solutions50/compare/v2.0.1...v2.0.2) - 2026/09/25

### Fixed

- Add manifest entry to silence JLine native-access warning


## [2.0.1](https://github.com/raul-izquierdo/solutions50/compare/v2.0.0...v2.0.1) - 2026/09/25


### Fixed

- fix: Make team name matching case-insensitive in AutomaticSelection


## [2.0.0](https://github.com/raul-izquierdo/solutions50/compare/v1.0.0...v2.0.0) - 2026/09/25

### BREAKING CHANGES

- fix!: Change command line flags and default csv name

### Added

- docs: Create README
-
### Changed

- ui: Add log message for default schedule file existence check


## [1.0.0]

### Added

- Automatic granting of access to solutions based on a schedule.
- Manual selection of group and solution when automatic detection is not possible.
- Added `-r` option to specify a regular expression for detecting solution repositories. Default is `.*solution$` (repositories whose names end with `solution`).
