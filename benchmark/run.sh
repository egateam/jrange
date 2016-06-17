#!/usr/bin/env bash

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

echo "==> merge_node.pl"
${COMMAND_TIME} perl ~/Scripts/egas/merge_node.pl \
    -v -c 0.95 \
    -f links.lastz.tsv \
    -f links.blast.tsv \
    > /dev/null
