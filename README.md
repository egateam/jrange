[![Travis](https://img.shields.io/travis/wang-q/jrange.svg)](https://travis-ci.org/wang-q/jrange)
[![Codecov branch](https://img.shields.io/codecov/c/github/wang-q/jrange/master.svg)](https://codecov.io/github/wang-q/jrange?branch=master)

# NAME

`jrange` operates ranges and links of ranges on chromosomes.

## SYNOPSIS

```
Usage: <main class> [options] [command] [command options]
  Options:
    --help, -h
       Print this help and quit
       Default: false
  Commands:
    merge      Merge runlist yaml files
      Usage: merge [options] <infiles>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.

```

## DESCRIPTION

This Java package is ported from some Perl scripts in `egas`.

## REQUIREMENTS

Oracle/Open JDK 1.7 or higher.

## EXAMPLES

```bash
mvn clean verify

java -jar target/jrange-*-jar-with-dependencies.jar \
    merge -o stdout \
    src/test/resources/I.links.tsv
```

## AUTHOR

Qiang Wang &lt;wang-q@outlook.com&gt;

## COPYRIGHT AND LICENSE

This software is copyright (c) 2016 by Qiang Wang.

This is free software; you can redistribute it and/or modify it under the same terms as the Perl 5
programming language system itself.
