#!/bin/bash

die () {
	echo "Error: $1"
	exit 1
}

[ -f run/server.pid ] && ps aux | grep `cat run/server.pid` &>/dev/null && die "stop server first" 
mkdir -p run
source `pwd`/../../deps/vpython/ENV/vpython/bin/activate || die "cannot start virtual python environment"
python app.py $* &
PID="$!"
echo "$PID" > run/server.pid
wait $PID
rm -f run/server.pid &>/dev/null
