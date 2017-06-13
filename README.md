[![Travis](https://img.shields.io/travis/egateam/jrange.svg)](https://travis-ci.org/egateam/jrange)
[![Codecov branch](https://img.shields.io/codecov/c/github/egateam/jrange/master.svg)](https://codecov.io/github/egateam/jrange?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.egateam/jrange.svg)](http://search.maven.org/#search|ga|1|g%3A%22com.github.egateam%22%20AND%20a%3A%22jrange%22)

[TOC levels=1-3]: # " "
- [NAME](#name)
- [SYNOPSIS](#synopsis)
- [DESCRIPTION](#description)
- [REQUIREMENTS](#requirements)
- [EXAMPLES](#examples)
- [COMPARISON](#comparison)
    - [BENCHMARK](#benchmark)
- [AUTHOR](#author)
- [COPYRIGHT AND LICENSE](#copyright-and-license)


# NAME

`jrange` operates ranges and links of ranges on chromosomes.

# SYNOPSIS

```text
Usage: <main class> [options] [command] [command options]
  Options:
    --help, -h
       Print this help and quit
       Default: false
  Commands:
    merge      Merge overlapped ranges via overlapping graph
      Usage: merge [options] <infiles>
        Options:
          --coverage, -c
             When larger than this ratio, merge ranges.
             Default: 0.95
          --outfile, -o
             Output filename. [stdout] for screen.
          --verbose, -v
             Verbose mode.
             Default: false

    connect      Connect range links in paralog graph
      Usage: connect [options] <infiles>
        Options:
          --merged, -m
             Merged nodes file in .tsv format
          --outfile, -o
             Output filename. [stdout] for screen.

```

# DESCRIPTION

This Java package is ported from some Perl scripts in `egas`.

# REQUIREMENTS

Oracle/Open JDK 1.7 or higher.

# EXAMPLES

```bash
mvn clean verify

java -jar target/jrange-*-jar-with-dependencies.jar \
    sort -o stdout \
    src/test/resources/II.links.tsv

java -jar target/jrange-*-jar-with-dependencies.jar \
    merge -o stdout \
    src/test/resources/II.links.tsv

cat src/test/resources/I.links.tsv \
    | java -jar target/jrange-*-jar-with-dependencies.jar \
    merge -o stdout \
    stdin

java -jar target/jrange-*-jar-with-dependencies.jar \
    clean -o stdout \
    src/test/resources/II.sort.tsv

java -jar target/jrange-*-jar-with-dependencies.jar \
    clean -o stdout \
    --bundle 500 \
    src/test/resources/II.sort.tsv

java -jar target/jrange-*-jar-with-dependencies.jar \
    clean -o stdout \
    -r src/test/resources/II.merge.tsv \
    src/test/resources/II.sort.tsv

# command connect not fully working
java -jar target/jrange-*-jar-with-dependencies.jar \
    connect -o stdout \
    src/test/resources/II.clean.tsv
```

# COMPARISON

## BENCHMARK

```bash
cd benchmark
bash run.sh
```

* OSX 10.11 i7-6700k oracleJDK8

```
==> jrange merge lastz blast
        3.86 real         4.60 user         0.67 sys
2226823168  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    546737  page reclaims
         2  page faults
         0  swaps
         0  block input operations
         3  block output operations
         0  messages sent
         0  messages received
         3  signals received
        57  voluntary context switches
      4757  involuntary context switches
==> App::Rangeops merge lastz blast
      180.09 real       521.59 user         0.92 sys
  78188544  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    200231  page reclaims
        60  page faults
         0  swaps
        35  block input operations
        23  block output operations
       140  messages sent
       131  messages received
        20  signals received
       445  voluntary context switches
    121331  involuntary context switches
==> jrange clean sort.clean
        2.46 real         3.65 user         0.38 sys
1151385600  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    282793  page reclaims
         2  page faults
         0  swaps
         0  block input operations
         2  block output operations
         0  messages sent
         0  messages received
         1  signals received
         0  voluntary context switches
      5279  involuntary context switches
==> App::Rangeops clean sort.clean
       77.23 real        77.07 user         0.11 sys
  83320832  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
     37848  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         2  block output operations
         0  messages sent
         0  messages received
        20  signals received
        60  voluntary context switches
      6857  involuntary context switches
==> jrange clean bundle sort.clean
        5.07 real         6.67 user         0.68 sys
2235871232  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    549744  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         3  block output operations
         0  messages sent
         0  messages received
         4  signals received
         0  voluntary context switches
      6231  involuntary context switches
==> App::Rangeops clean bundle sort.clean
      123.19 real       122.97 user         0.15 sys
  91852800  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
     39960  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         2  block output operations
         0  messages sent
         0  messages received
        20  signals received
        59  voluntary context switches
     10483  involuntary context switches
```

# AUTHOR

Qiang Wang &lt;wang-q@outlook.com&gt;

# COPYRIGHT AND LICENSE

This software is copyright (c) 2016 by Qiang Wang.

This is free software; you can redistribute it and/or modify it under the same terms as the Perl 5
programming language system itself.
