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
    > /dev/null

echo "==> App::Rangeops merge lastz blast"
${COMMAND_TIME} rangeops \
    merge \
    -o stdout -c 0.95 -p 8 \
    links.lastz.tsv \
    links.blast.tsv \
    > /dev/null

echo "==> jrange clean sort.clean"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    clean \
    -o stdout \
    sort.clean.tsv \
    > /dev/null

echo "==> App::Rangeops clean sort.clean"
${COMMAND_TIME} rangeops \
    clean \
    -o stdout \
    sort.clean.tsv \
    > /dev/null
