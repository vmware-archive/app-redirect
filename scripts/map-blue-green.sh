#!/bin/sh

if [ $# != 4 ] ; then cat << EOM

    This script is used for mapping the blue/green configured environments

    usage: $0 SPACE PATH_TO_CF CURRENT NEXT

    where SPACE is one of (staging|production)
      and PATH_TO_CF is the path to the 'cf' executable
      and CURRENT the name of the current running app (either blue or green)
      and NEXT the name of the app that will be set up to run 

EOM
    exit
fi

SPACE=$1
CF=$2
CURRENT=$3
NEXT=$4

echo "Starting blue-green mapping script. Current running app is $CURRENT, next running app will be $NEXT"

echo "switching to space $SPACE"
$CF space $SPACE || exit

($CF map $NEXT downloads cfapps.io && $CF unmap downloads.cfapps.io $CURRENT) || exit

wait
