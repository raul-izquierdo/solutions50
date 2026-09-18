# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.0.0]

### Added

- Automatic granting of access to solutions based on a schedule.
- Manual selection of group and solution when automatic detection is not possible.
- Added `-r` option to specify a regular expression for detecting solution repositories. Default is `.*solution$` (repositories whose names end with `solution`).
