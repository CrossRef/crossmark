# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [2.0.100] - 2017-01-10

Source code to Crossmark now available on GitHub. Removal of some technical debt and unused code.

### Added
  - Tools
     - Docker Compose for all tasks.
     - Linting with Eastwood.
     - ./check.sh

  - Code
     - Fixing all linter complaints.
     - Template the 2.0 widget script to include configuration.
     - Shift deprecated endpoints into new namespace.
     - Pass JWT token through script to catch CDN errors.

  - Deployment and config
     - Bump version to 2.0.100
     - Switched to environment variables for config.
     - Removed cdn-url-secure config, was duplicating cdn-url.
     - Removed crossmark-server-secure config, was duplicating crossmark-server-secure.
     - Added version into consts object.
     - Include version in templated script.
     - Remove caching of items that are CDNed.
     
  - Tests
     - Upped coverage of tests in data namespace.
     - Added some test stubs for later.
