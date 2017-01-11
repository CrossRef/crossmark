# Crossmark Dialog Server

Service for showing the Crossmark dialog and serving up assets.

## Versioning

The server (i.e. the 'Crossmark service') is versioned, and the version number can be found in the `project.clj` file. Major, minor and point release numbers are used. This is the version of the service, which serves up both the widget code and the contents of the dialog. There is only one current version of Crossmark service.

The widget itself is versioned. Major and minor versions are used, and correspond to the current server version, but point releases are not used. This is because the script must be referenced explicitly in the publisher's site when they embed the version. A minor release requires a publisher to change their HTML. 

## CDN

A CDN should be provided for assets, mirroring the service.

## Configuration

Configuration is done via environment variables.

 - `PORT` - port to run server on
 - `CDN_URL` - public URL of CDN, e.g. `https://crossmark-cdn.crossref.org`. For development, should be set to e.g. `http://localhost:8000`.
 - `CROSSMARK_SERVER` - public url of where this service is running, e.g. `https://crossmark.crossref.org`. For development, should be set to e.g. `http://localhost:8000`.
 - `JWT_SECRET` - a secret for encoding JWTs
 - `API_BASE` - custom base of REST API. Defaults to `https://api.crossref.org`
 - `PUSH_URL` - URL for reporting stats
 - `PUSH_TOKEN` - token for reporting status

## Deployment

The server can be deployed either running directly or via Docker.

### Running directly

1. Generate a JWT secret.
2. Create a file that resembles `run.sh.example`
3. Run it! 

### Running with Docker

Run via Docker Swarm, or create a `docker-compose.yml` file with above environment variables.

## Tinkering

Run the server:

    time docker-compose run --service-ports -w /usr/src/app test lein run

Then visit, e.g.:

    http://localhost:8000/dialog?doi=10.1016/j.earscirev.2013.04.010

## Quality

### Tests 

Tests are split into two categories:

 - 'server' tests generally take a metadata API response and build a context object (passed into the HTML render). The context object is tested.
 - 'browser' tests execute the widget in a headless browser

To run server tests:

    time docker-compose run -w /usr/src/app test lein test :server

To run browser tests:

    time docker-compose run -w /usr/src/app test lein test :browser

Everything

    time docker-compose run -w /usr/src/app test lein test

Run code quality check:

    time docker-compose run -w /usr/src/app test lein eastwood

### Coverage

Code coverage from running all tests. Results are found in `target/coverage`.

    lein cloverage

### Test TODOs

Test stubs are marked with the `:todo` tag.

### Check

Pre-release check everything. Don't release unless this passes!

    ./check.sh

## License

Copyright © Crossref

Distributed under the The MIT License (MIT).
