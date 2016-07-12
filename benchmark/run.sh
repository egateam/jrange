#!/usr/bin/env bash

cpanm --mirror-only --mirror https://stratopan.com/wangq/ega/master App::Rangeops

COMMAND_TIME="command time -v"
if [[ `uname` == 'Darwin' ]];
then
    COMMAND_TIME="command time -l"
fi

echo "==> jrange merge lastz blast"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    merge \
    -o stdout -c 0.95 \
    links.lastz.tsv \
    links.blast.tsv \
    | sort \
    > jmerge.tsv.tmp

echo "==> App::Rangeops merge lastz blast"
${COMMAND_TIME} rangeops \
    merge \
    -o stdout -c 0.95 -p 8 \
    links.lastz.tsv \
    links.blast.tsv \
    | sort \
    > pmerge.tsv.tmp

echo "==> jrange clean sort.clean"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    clean \
    -o stdout \
    sort.clean.tsv \
    > jclean.tsv.tmp

echo "==> App::Rangeops clean sort.clean"
${COMMAND_TIME} rangeops \
    clean \
    -o stdout \
    sort.clean.tsv \
    > pclean.tsv.tmp

echo "==> jrange clean bundle sort.clean"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    clean \
    -o stdout \
    --bundle 500 \
    sort.clean.tsv \
    > jbundle.tsv.tmp

echo "==> App::Rangeops clean bundle sort.clean"
${COMMAND_TIME} rangeops \
    clean \
    -o stdout \
    --bundle 500 \
    sort.clean.tsv \
    > pbundle.tsv.tmp
