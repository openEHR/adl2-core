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