#!/usr/bin/env bash

cpanm --mirror-only --mirror https://stratopan.com/wangq/ega/master App::Rangeops

COMMAND_TIME="command time -v"
if [[ `uname` == 'Darwin' ]];
then
    COMMAND_TIME="command time -l"
fi

echo "==> jrange lastz blast"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    merge \
    -o stdout -c 0.95 \
    links.lastz.tsv \
    links.blast.tsv \
    > /dev/null

echo "==> jrange sort.clean"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    merge \
    -o stdout -c 0.95 \
    sort.clean.tsv \
    > /dev/null

echo "==> App::Rangeops lastz blast"
${COMMAND_TIME} rangeops \
    merge \
    -o stdout -c 0.95 -p 8 \
    links.lastz.tsv \
    links.blast.tsv \
    > /dev/null

echo "==> App::Rangeops sort.clean"
${COMMAND_TIME} rangeops \
    merge \
    -o stdout -c 0.95 -p 8 \
    sort.clean.tsv \
    > /dev/null
