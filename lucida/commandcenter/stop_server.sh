#!/bin/bash

die () {
	echo "Error: $1"
	exit 1
}

[ -f run/server.pid ] || die "server not running"
kill -1 `cat run/server.pid` || die "server not running"
