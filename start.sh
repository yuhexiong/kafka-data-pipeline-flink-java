#!/bin/bash

while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -c|--class)
        CLASS="$2"
        shift
        shift
        ;;
        *)
        echo "unknown option: $1"
        exit 1
        ;;
    esac
done

if [ -z "$CLASS" ]; then
    CLASS="$MY_CLASS"
fi

if [ -z "$CLASS" ]; then
    echo "use $0 -c <class>"
    exit 1
fi

java -cp /usr/src/app/flinkKafka.jar entry.$CLASS
