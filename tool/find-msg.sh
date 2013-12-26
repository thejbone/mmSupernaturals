#!/bin/bash

BASEPATH='./src/main/java/com/mmiillkkaa/supernaturals/'
LOGFILE='/tmp/mmSupernaturals-msg.log'
> $LOGFILE

cd $(dirname $0) && cd ../
cd $BASEPATH
for i in $(find ./); do grep -i sendMessage $i >/dev/null && echo $i >> \
    "$LOGFILE"_; done && sort "$LOGFILE"_ > $LOGFILE
\rm "$LOGFILE"_
echo "Check the log file: $LOGFILE"
