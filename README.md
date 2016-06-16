[![Travis](https://img.shields.io/travis/wang-q/jrunlist.svg)](https://travis-ci.org/wang-q/jrunlist)
[![Codecov branch](https://img.shields.io/codecov/c/github/wang-q/jrunlist/master.svg)](https://codecov.io/github/wang-q/jrunlist?branch=master)

# NAME

`jrunlist` operates chromosome runlist files

## SYNOPSIS

```
Usage: <main class> [options] [command] [command options]
  Options:
    --help, -h
       Print this help and quit
       Default: false
  Commands:
    combine      Combine multiple sets of runlists in a yaml file.
                It's expected that the YAML file is --mk.
                Otherwise this command will make no effects.
      Usage: combine [options] <infile>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.
          --remove, -r
             Remove 'chr0' from chromosome names.
             Default: false

    compare      Compare 2 YAML files.
                Only the *first* file can contain multiple sets of runlists.
      Usage: compare [options] <infile1> <infile2>
        Options:
          --op
             operations: intersect, union, diff or xor.
             Default: intersect
          --outfile, -o
             Output filename. [stdout] for screen.
          --remove, -r
             Remove 'chr0' from chromosome names.
             Default: false

    cover      Output covers on chromosomes.
                Like `command combine`, but <infiles> are chromosome positions.
                I:1-100
                I(+):90-150      Strands will be omitted.
                S288c.I(-):190-200      Species names will be omitted.
      Usage: cover [options] <infiles>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.
          --remove, -r
             Remove 'chr0' from chromosome names.
             Default: false

    genome      Convert chr.size to runlists
      Usage: genome [options] <infile>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.
          --remove, -r
             Remove 'chr0' from chromosome names.
             Default: false

    merge      Merge runlist yaml files
      Usage: merge [options] <infiles>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.

    some      Extract some records from a runlist yaml file
      Usage: some [options] <infile> <list.file>
        Options:
          --outfile, -o
             Output filename. [stdout] for screen.

    span      Operate spans in a YAML file.
        List of operations
                cover:  a single span from min to max;
                holes:  all the holes in runlist;
                trim:   remove N integers from each end of each span of runlist;
                pad:    add N integers from each end of each span of runlist;
                excise: remove all spans smaller than N;
                fill:   fill in all holes smaller than or equals to N.
      Usage: span [options] <infile>
        Options:
          --number, -n
             Apply this number to trim, pad, excise or fill.
             Default: 0
          --op
             operations: cover, holes, trim, pad, excise or fill.
             Default: cover
          --outfile, -o
             Output filename. [stdout] for screen.
          --remove, -r
             Remove 'chr0' from chromosome names.
             Default: false

    split      Split a runlist yaml file
      Usage: split [options] <infile>
        Options:
          --outdir, -o
             Output location. [stdout] for screen.
             Default: .
          --suffix, -s
             Extension of output files.
             Default: .yml

    stat      Coverage on chromosomes for runlists
      Usage: stat [options] <chr.size> <infile>
        Options:
          --all
             Only write whole genome stats.
             Default: false
          --outfile, -o
             Output filename. [stdout] for screen.
          --remove, -r
             Remove 'chr0' from chromosome names.
             Default: false

    statop      Coverage on chromosomes for one YAML crossed another
                Only the *first* file can contain multiple sets of runlists.
      Usage: statop [options] <chr.size> <infile1> <infile2>
        Options:
          --all
             Only write whole genome stats.
             Default: false
          --base, -b
             basename of infile2
          --op
             operations: intersect, union, diff or xor.
             Default: intersect
          --outfile, -o
             Output filename. [stdout] for screen.
          --remove, -r
             Remove 'chr0' from chromosome names.
             Default: false

```

## DESCRIPTION

This Java class is ported from the Perl module `App::RL`.

## REQUIREMENTS

Oracle/Open JDK 1.7 or higher.

Optional:

* yamljs: `npm install -g yamljs`

## EXAMPLES

```bash
mvn clean verify

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    genome -o stdout \
    src/test/resources/chr.sizes

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    merge -o stdout \
    src/test/resources/I.yml \
    src/test/resources/II.yml

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    cover -o stdout \
    src/test/resources/S288c.txt

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    span -o stdout \
    --op cover \
    src/test/resources/brca2.yml

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    compare -o stdout \
    --op intersect \
    src/test/resources/intergenic.yml \
    src/test/resources/repeat.yml

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    compare -o stdout \
    --op intersect \
    src/test/resources/I.II.yml \
    src/test/resources/intergenic.yml

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    stat -o stdout \
    src/test/resources/chr.sizes \
    src/test/resources/intergenic.yml

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    statop -o stdout \
    --op intersect \
    src/test/resources/chr.sizes \
    src/test/resources/intergenic.yml \
    src/test/resources/repeat.yml

java -jar target/jrunlist-*-jar-with-dependencies.jar \
    stat \
    benchmark/chr.sizes benchmark/sep-gene.yml \
    --all -o stdout

time java -jar target/jrunlist-*-jar-with-dependencies.jar \
    statop \
    benchmark/chr.sizes benchmark/sep-gene.yml benchmark/paralog.yml \
    --op intersect --all -o stdout > /dev/null
```

## COMPARISON

### CLOC

```bash
cloc ~/Scripts/java/jrunlist/src/main
cloc ~/Scripts/cpan/App-RL/lib/ ~/Scripts/cpan/App-RL/script/
```

| name     | cloc    |
| :-----   | :-----: |
| App::RL  | 961     |
| jrunlist | 1083    |

### BENCHMARK

```bash
cd benchmark
bash run.sh
```

* OSX 10.11 i7-6700k oracleJDK8

```
==> jrunlist
        3.12 real         8.17 user         0.91 sys
1064009728  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    270271  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         5  block output operations
         0  messages sent
         0  messages received
         2  signals received
         2  voluntary context switches
     40022  involuntary context switches
==> App::RL
      352.19 real       350.19 user         1.60 sys
 115826688  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
     42417  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         6  block output operations
         0  messages sent
         0  messages received
        20  signals received
        56  voluntary context switches
     55700  involuntary context switches
```

* Ubuntu 14.04 E5-2690 v3 openJDK7

```
==> jrunlist
        Command being timed: "java -jar ../target/jrunlist-0.1.2-jar-with-dependencies.jar statop chr.sizes sep-gene.yml paralog.yml --op intersect --all -o stdout"
        User time (seconds): 7.57
        System time (seconds): 0.90
        Percent of CPU this job got: 186%
        Elapsed (wall clock) time (h:mm:ss or m:ss): 0:04.53
        Average shared text size (kbytes): 0
        Average unshared data size (kbytes): 0
        Average stack size (kbytes): 0
        Average total size (kbytes): 0
        Maximum resident set size (kbytes): 1113040
        Average resident set size (kbytes): 0
        Major (requiring I/O) page faults: 0
        Minor (reclaiming a frame) page faults: 29505
        Voluntary context switches: 801
        Involuntary context switches: 194
        Swaps: 0
        File system inputs: 0
        File system outputs: 64
        Socket messages sent: 0
        Socket messages received: 0
        Signals delivered: 0
        Page size (bytes): 4096
        Exit status: 0
==> App::RL
        Command being timed: "runlist stat2 -s chr.sizes sep-gene.yml paralog.yml --op intersect --all --mk -o stdout"
        User time (seconds): 383.45
        System time (seconds): 0.17
        Percent of CPU this job got: 100%
        Elapsed (wall clock) time (h:mm:ss or m:ss): 6:23.49
        Average shared text size (kbytes): 0
        Average unshared data size (kbytes): 0
        Average stack size (kbytes): 0
        Average total size (kbytes): 0
        Maximum resident set size (kbytes): 115824
        Average resident set size (kbytes): 0
        Major (requiring I/O) page faults: 0
        Minor (reclaiming a frame) page faults: 168542
        Voluntary context switches: 93
        Involuntary context switches: 5565
        Swaps: 0
        File system inputs: 0
        File system outputs: 0
        Socket messages sent: 0
        Socket messages received: 0
        Signals delivered: 0
        Page size (bytes): 4096
        Exit status: 0
```

### Different implementations of IntSpan

```
time java -Xmx16g -jar target/jrunlist-*-jar-with-dependencies.jar \
    statop \
    benchmark/chr.sizes benchmark/sep-gene.yml benchmark/dust.yml \
    --op intersect --all -o stdout > /dev/null
```

* IntSpan with `ArrayList<Integer>`

```
# 4g
real	1m54.431s
user    10m37.026s
sys	    0m4.820s
# 8g
real	1m2.622s
user	2m58.087s
sys	    0m7.783s
# 16g
real	0m52.443s
user	1m30.462s
sys	    0m8.424s
```

* IntSpan with HPPC `IntArrayList`

```
# 4g
java.lang.OutOfMemoryError
# 8g
real	0m26.901s
user	0m37.996s
sys	    0m10.456s
# 16g
real	0m31.387s
user	0m49.367s
sys	    0m12.870s
```

## AUTHOR

Qiang Wang &lt;wang-q@outlook.com&gt;

## COPYRIGHT AND LICENSE

This software is copyright (c) 2016 by Qiang Wang.

This is free software; you can redistribute it and/or modify it under the same terms as the Perl 5
programming language system itself.
