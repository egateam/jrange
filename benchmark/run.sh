#!/usr/bin/env bash

#----------------------------#
# Colors in term
#----------------------------#
# http://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
GREEN=
RED=
NC=
if tty -s < /dev/fd/1 2> /dev/null; then
    GREEN='\033[0;32m'
    RED='\033[0;31m'
    NC='\033[0m' # No Color
fi

log_warn () {
    echo >&2 -e "${RED}==> $@ <==${NC}"
}

log_info () {
    echo >&2 -e "${GREEN}==> $@${NC}"
}

log_debug () {
    echo >&2 -e "==> $@"
}

#----------------------------#
# Prepare
#----------------------------#
cpanm App::Rangeops

COMMAND_TIME="command time -v"
if [[ `uname` == 'Darwin' ]];
then
    COMMAND_TIME="command time -l"
fi

#----------------------------#
# Run
#----------------------------#
log_info "jrange merge lastz blast"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    merge \
    -o stdout -c 0.95 \
    links.lastz.tsv \
    links.blast.tsv \
    | sort \
    > jmerge.tsv.tmp

log_info "rangeops merge lastz blast"
${COMMAND_TIME} rangeops \
    merge \
    -o stdout -c 0.95 -p 8 \
    links.lastz.tsv \
    links.blast.tsv \
    | sort \
    > pmerge.tsv.tmp

log_info "jrange clean sort.clean"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    clean \
    -o stdout \
    sort.clean.tsv \
    > jclean.tsv.tmp

log_info "rangeops clean sort.clean"
${COMMAND_TIME} rangeops \
    clean \
    -o stdout \
    sort.clean.tsv \
    > pclean.tsv.tmp

log_info "jrange clean bundle sort.clean"
${COMMAND_TIME} java -jar ../target/jrange-*-jar-with-dependencies.jar \
    clean \
    -o stdout \
    --bundle 500 \
    sort.clean.tsv \
    > jbundle.tsv.tmp

log_info "rangeops clean bundle sort.clean"
${COMMAND_TIME} rangeops \
    clean \
    -o stdout \
    --bundle 500 \
    sort.clean.tsv \
    > pbundle.tsv.tmp
