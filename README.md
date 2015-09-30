# adl2-core

ADL / AOM 2 (previously known as '1.5') core libs and parser

## Getting Started

### Requirements

* Java 7
* Maven 3

### Usage

The latest stable build is deployed into Maven Central repository. To include adl2 parser into your project, add this dependency into your pom:
```xml
<dependency>
    <groupId>org.openehr.adl2-core</groupId>
    <artifactId>adl-parser</artifactId>
    <version>1.2.0</version>
</dependency>
```

Look into adl-parser directory for some examples about using the parser.

### Build from source
Clone the project from github. Then, run:
```bash
mvn clean install
```

This will create jar files in the _target_ directories of each submodule

## Deploy to maven central repository

The project uses maven-release-plugin to deploy the artifacts to maven central.

First, make sure that there are no outstanding changes left to be committed, otherwise, the deployment will fail. Also, make sure that `mvn clean install` completes successfully.

To begin with deployment, run:
```bash
mvn release:prepare
```

This will ask you for the version of the deployed artifacts, name of the created scm tag, and the new development version, all of which will have sensible defaults. It may also ask you for your GitHub credentials. If there were any problems, run `mvn release:clean` to undo any changes.

Once the release is successfully prepared, you can deploy it with:
```bash
mvn release:perform
```

This will deploy the artifacts in the staging repository of [OSS Repository Hosting](http://central.sonatype.org/). For information about releasing the artifacts from the staging repository in maven central, read http://central.sonatype.org/pages/releasing-the-deployment.html.

Short version: 
 * Login in https://oss.sonatype.org/
 * Go to _Staging Repositories_ (on left panel) and search for _orgopenehr_-####.
 * Select this repository, and press _Close_
 * One the repository is successfully closed, press _Release_
 * Artifacts should be synhronized to maven central within few minutes 