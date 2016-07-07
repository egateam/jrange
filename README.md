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

## DESCRIPTION

This Java package is ported from some Perl scripts in `egas`.

## REQUIREMENTS

Oracle/Open JDK 1.7 or higher.

## EXAMPLES

```bash
mvn clean verify

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

# command connect not fully working
java -jar target/jrange-*-jar-with-dependencies.jar \
    connect -o stdout \
    -m src/test/resources/I.merge.tsv \
    src/test/resources/I.links.tsv
```

## COMPARISON

### BENCHMARK

```bash
cd benchmark
bash run.sh
```

* OSX 10.11 i7-6700k oracleJDK8

```
==> jrange
        3.93 real         4.71 user         0.60 sys
2227257344  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    546375  page reclaims
        33  page faults
         0  swaps
        43  block input operations
         4  block output operations
         0  messages sent
         0  messages received
         2  signals received
       123  voluntary context switches
      4787  involuntary context switches
==> App::Rangeops
      188.23 real       547.69 user         2.74 sys
  73101312  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    974396  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         3  block output operations
       804  messages sent
       721  messages received
        20  signals received
      2566  voluntary context switches
    213744  involuntary context switches
```

* Ubuntu 14.04 E5-2690 v3 openJDK7

```
==> jrange
        Command being timed: "java -jar ../target/jrange-0.0.1-jar-with-dependencies.jar merge -o stdout -c 0.95 links.lastz.tsv links.blast.tsv"
        User time (seconds): 8.94
        System time (seconds): 23.49
        Percent of CPU this job got: 139%
        Elapsed (wall clock) time (h:mm:ss or m:ss): 0:23.29
        Average shared text size (kbytes): 0
        Average unshared data size (kbytes): 0
        Average stack size (kbytes): 0
        Average total size (kbytes): 0
        Maximum resident set size (kbytes): 2134124
        Average resident set size (kbytes): 0
        Major (requiring I/O) page faults: 0
        Minor (reclaiming a frame) page faults: 861136
        Voluntary context switches: 1285
        Involuntary context switches: 742
        Swaps: 0
        File system inputs: 0
        File system outputs: 104
        Socket messages sent: 0
        Socket messages received: 0
        Signals delivered: 0
        Page size (bytes): 4096
        Exit status: 0
==> merge_node.pl
        Command being timed: "perl /home/wangq/Scripts/egas/merge_node.pl -v -c 0.95 -f links.lastz.tsv -f links.blast.tsv"
        User time (seconds): 632.64
        System time (seconds): 0.61
        Percent of CPU this job got: 276%
        Elapsed (wall clock) time (h:mm:ss or m:ss): 3:49.19
        Average shared text size (kbytes): 0
        Average unshared data size (kbytes): 0
        Average stack size (kbytes): 0
        Average total size (kbytes): 0
        Maximum resident set size (kbytes): 72484
        Average resident set size (kbytes): 0
        Major (requiring I/O) page faults: 0
        Minor (reclaiming a frame) page faults: 314199
        Voluntary context switches: 14891
        Involuntary context switches: 4683
        Swaps: 0
        File system inputs: 0
        File system outputs: 1592
        Socket messages sent: 0
        Socket messages received: 0
        Signals delivered: 0
        Page size (bytes): 4096
        Exit status: 0
```

## AUTHOR

Qiang Wang &lt;wang-q@outlook.com&gt;

## COPYRIGHT AND LICENSE

This software is copyright (c) 2016 by Qiang Wang.

This is free software; you can redistribute it and/or modify it under the same terms as the Perl 5
programming language system itself.
