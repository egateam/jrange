[![Travis](https://img.shields.io/travis/egateam/jrange.svg)](https://travis-ci.org/egateam/jrange)
[![Codecov branch](https://img.shields.io/codecov/c/github/egateam/jrange/master.svg)](https://codecov.io/github/egateam/jrange?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.egateam/jrange.svg)](http://search.maven.org/#search|ga|1|g%3A%22com.github.egateam%22%20AND%20a%3A%22jrange%22)

[TOC levels=1-3]: # " "
- [NAME](#name)
- [SYNOPSIS](#synopsis)
- [DESCRIPTION](#description)
- [REQUIREMENTS](#requirements)
- [INSTALLATION](#installation)
- [EXAMPLES](#examples)
- [COMPARISON](#comparison)
    - [BENCHMARK](#benchmark)
- [AUTHOR](#author)
- [COPYRIGHT AND LICENSE](#copyright-and-license)


# NAME

`jrange` operates ranges and links of ranges on chromosomes.

# SYNOPSIS

```text
$ jrange --help
Usage: <main class> [options] [command] [command options]
  Options:
    --help, -h
       Print this help and quit
       Default: false
  Commands:
    path      Replace ranges within links, incorporate hit strands and remove nested links
      Usage: path [options]
        Options:
          --file
             output filename instead of full path
             Default: false

    sort      Replace ranges within links, incorporate hit strands and remove nested links
      Usage: sort [options] <infiles>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.

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

    clean      Replace ranges within links, incorporate hit strands and remove nested links
      Usage: clean [options] <infiles>
        Options:
          --bundle, -b
             Bundle overlapped links. This value is the overlapping size.
             Suggested value is [500].
             Default: 0
          --outfile, -o
             Output filename. [stdout] for screen.
          --replace, -r
             Two-column tsv file, normally produced by command merge.
          --verbose, -v
             Verbose mode.
             Default: false

    connect      (Unfinished) Connect range links in paralog graph
      Usage: connect [options] <infiles>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.

    covered      Covered regions from .ovlp.tsv files
      Usage: covered [options] <infiles>
        Options:
          --basecov
             coverage per base location
             Default: false
          --coverage, -c
             minimal coverage
             Default: 3
          --idt, -i
             minimal length of overlaps
             Default: 0.0
          --len, -l
             minimal length of overlaps
             Default: 1000
          --longest
             only keep the longest span
             Default: false
          --meancov
             mean coverage per base location
             Default: false
          --outfile, -o
             Output filename. [stdout] for screen.
          --paf
             input format as PAF
             Default: false

```

# DESCRIPTION

This Java package is ported from the Perl package `App::Rangeops`.

# REQUIREMENTS

Oracle/Open JDK 1.7 or higher.

# INSTALLATION

* By Homebrew (Linuxbrew)

    ```bash
    brew install wang-q/tap/jrange
    ```

* By maven

    ```bash
    git clone https://github.com/egateam/jrange
    cd jrange
    
    mvn clean verify
    ```

# EXAMPLES

```bash
# mvn clean verify
# java -jar target/jrange-*-jar-with-dependencies.jar sort src/test/resources/II.links.tsv -o stdout

jrange path
jrange path --file

jrange sort src/test/resources/II.links.tsv -o stdout

jrange merge src/test/resources/II.links.tsv -o stdout

cat src/test/resources/I.links.tsv |
    jrange merge stdin -o stdout

jrange clean src/test/resources/II.sort.tsv -o stdout

jrange clean src/test/resources/II.sort.tsv --bundle 500 -o stdout 

jrange clean src/test/resources/II.sort.tsv -r src/test/resources/II.merge.tsv -o stdout

jrange covered src/test/resources/1_4.pac.paf.ovlp.tsv -o stdout
    
jrange covered src/test/resources/1_4.pac.paf.ovlp.tsv --basecov -o stdout
    
jrange covered src/test/resources/1_4.pac.paf.ovlp.tsv --meancov -o stdout

jrange covered src/test/resources/11_2.long.paf --paf -o stdout

# (Unfinished)
#jrange connect src/test/resources/II.clean.tsv -o stdout
#jarnge reduce

```

# COMPARISON

## BENCHMARK

```bash
bash benchmark/run.sh
```

* OSX 10.14 i7-8700k oracleJDK8

```
==> merge <==
==> jrange merge lastz blast
        3.82 real         4.32 user         0.72 sys
2232799232  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    546831  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         1  signals received
        69  voluntary context switches
      6125  involuntary context switches
==> linkr merge lastz blast
       11.68 real        11.62 user         0.03 sys
  10772480  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
      2344  page reclaims
       295  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         0  signals received
         2  voluntary context switches
      1500  involuntary context switches
==> rangeops merge lastz blast
      109.04 real       301.83 user         1.68 sys
  81469440  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    192907  page reclaims
        98  page faults
         0  swaps
         0  block input operations
         0  block output operations
       140  messages sent
       131  messages received
         0  signals received
       338  voluntary context switches
    320048  involuntary context switches

==> clean <==
==> jrange clean sort.clean
        2.45 real         3.55 user         0.42 sys
1151713280  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    282674  page reclaims
       210  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         1  signals received
         3  voluntary context switches
      5934  involuntary context switches
==> linkr clean sort.clean
        3.36 real         3.33 user         0.01 sys
  20246528  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
      4935  page reclaims
        17  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         0  signals received
         0  voluntary context switches
       961  involuntary context switches
==> rangeops clean sort.clean
       52.82 real        52.42 user         0.24 sys
  85401600  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
     25222  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         0  signals received
         4  voluntary context switches
     13771  involuntary context switches

==> clean bundle <==
==> jrange clean bundle sort.clean
        4.77 real         6.32 user         0.61 sys
1808756736  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    443476  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         3  signals received
         0  voluntary context switches
      7896  involuntary context switches
==> linkr clean bundle sort.clean
        6.65 real         6.52 user         0.06 sys
  32636928  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
      7974  page reclaims
         3  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         0  signals received
         0  voluntary context switches
     14738  involuntary context switches
==> rangeops clean bundle sort.clean
       82.68 real        82.10 user         0.34 sys
  89927680  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
     26459  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         0  block output operations
         0  messages sent
         0  messages received
         0  signals received
         4  voluntary context switches
     35064  involuntary context switches
```

# AUTHOR

Qiang Wang &lt;wang-q@outlook.com&gt;

# COPYRIGHT AND LICENSE

This software is copyright (c) 2016 by Qiang Wang.

This is free software; you can redistribute it and/or modify it under the same terms as the Perl 5
programming language system itself.
