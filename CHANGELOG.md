# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


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
