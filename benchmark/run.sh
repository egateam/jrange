#!/usr/bin/env bash

cpanm --mirror-only --mirror https://stratopan.com/wangq/ega/master App::Rangeops

COMMAND_TIME="command time -v"
if [[ `uname` == 'Darwin' ]];
then
    COMMAND_TIME="command time -l"
fi

echo "==> jrange"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    merge \
    -o stdout -c 0.95 \
    links.lastz.tsv \
    links.blast.tsv \
    > /dev/null

echo "==> App::Rangeops"
${COMMAND_TIME} rangeops \
    merge \
    -o stdout -c 0.95 -p 8 \
    links.lastz.tsv \
    links.blast.tsv \
    > /dev/null
