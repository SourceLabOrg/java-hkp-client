# Java-HKP-Client Java OpenPGP HTTP Keyserver Protocol API Client

## What is it?

This library provides an easy to use client library for interacting with a OpenPGP HTTP Keyserver Protocol (HKP) server.

## How to use this library

This client library is released on Maven Central.  Add a new dependency to your project's POM file:

```xml
<dependency>
    <groupId>org.sourcelab</groupId>
    <artifactId>java-hkp-client</artifactId>
    <version>3.1.0</version>
</dependency>
```


#### Example Code:
```java
/*
 * Create a new configuration object.
 *
 * This configuration also allows you to define some optional details on your connection,
 * such as using an outbound proxy (authenticated or not), SSL client settings, etc..
 */
final Configuration.Builder configuration = Configuration.newBuilder()
        .withKeyServerHost("http://pool.sks-keyservers.net");

/*
 * Create an instance of HKPClient, passing your configuration.
 */
final HkpClient client = new HkpClient(configuration);

/*
 * Making requests by calling the public methods available on HkpClient.
 * 
 * For example, search for a key by Email:
 */
final SearchIndexResponse result = client
    .search(new SearchRequest("example@example.com"));

/*
 * or retrieve a key by KeyId.
 */
final Optional<PgpPublicKey> result = client.get(new GetRequest("0x92E73960FC59970DFB12F0146D712A2D27F74CE9"));

/*
 * See HkpClient for other available operations.
 */
```

Public methods available on HkpClient can be [found here](src/main/java/org/sourcelab/hkp/HkpClient.java#L62)

# Contributing

Found a bug? Think you've got an awesome feature you want to add? We welcome contributions!

## Submitting a Contribution

1. Search for an existing issue. If none exists, create a new issue so that other contributors can keep track of what you are trying to add/fix and offer suggestions (or let you know if there is already an effort in progress).  Be sure to clearly state the problem you are trying to solve and an explanation of why you want to use the strategy you're proposing to solve it.
1. Fork this repository on GitHub and create a branch for your feature.
1. Clone your fork and branch to your local machine.
1. Commit changes to your branch.
1. Push your work up to GitHub.
1. Submit a pull request so that we can review your changes.

*Make sure that you rebase your branch off of master before opening a new pull request. We might also ask you to rebase it if master changes after you open your pull request.*

## Acceptance Criteria

We love contributions, but it's important that your pull request adhere to some of the standards we maintain in this repository.

- All tests must be passing!
- All code changes require tests!
- All code changes must be consistent with our checkstyle rules.
- Great inline comments.

# Other Notes


## Releasing

Steps for proper release:
- Update release version: `mvn versions:set -DnewVersion=X.Y.Z`
- Validate and then commit version: `mvn versions:commit`
- Update CHANGELOG and README files.
- Merge to master.
- Deploy to Maven Central: `mvn clean deploy -P release`
- Create release on Github project.


## Changelog

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

[View Changelog](CHANGELOG.md)



