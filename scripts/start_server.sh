#!/bin/bash

die () {
	echo "Error: $1"
	exit 1
}

usage() {
cat < EOF
start_server.sh [options] -c cmd [args ...]
  -c: ends options, the executable cmd must follow.
  -d: debug, just print what will be executed.
  -S <bash script>: source this script in start_server before running cmd.

EOF
exit 0
}

# Get Lucida root path
pushd $(dirname $0) &> /dev/null
cd ..
LUCIDAROOT=`pwd`
popd &> /dev/null
RUNDIR=`pwd`

# Process command line options
SERVER_DBG=0
SERVER_SRC=
while getopts ":dcS:" OPTION; do
case $OPTION in
d ) SERVER_DBG=1;;
c ) shift $((OPTIND-1));break;;   
S ) SERVER_SRC=$OPTARG;;
h ) usage;;
* ) usage;;
esac
done
[ "x$0" == "x-c" ] || die "missing cmd $0 $*"
shift

# Check if we are running
[ -f run/server.pid ] && kill -0 `cat run/server.pid` &>/dev/null && die "stop server first" 
mkdir -p run

[ -f $0 ] || which $0 || die "Cannot find \'$0\' in path"
[ -x $0 ] || die "file \'$0\' is not executable"
[ "x$SERVER_SRC" == "x" ] || source $SERVER_SRC || die "cannot source $SERVER_SRC"
echo "====================================================="
echo "Running server \'$0 $*\'"
echo "  To stop run stop_server.sh in the current directory"
echo "  The pid will be saved at `pwd`/run/server.pid"
$0 $* &
PID="$!"
# Use RUNDIR incase source script changed working dir
echo "$PID" > $RUNDIR/run/server.pid
#wait $PID
while kill -0 $PID &> /dev/null; do sleep 1; done
rm -f $RUNDIR/run/server.pid &>/dev/null

