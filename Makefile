DOCKER_CONTAINER=lucida
VERSION=latest

THREADS=4

#include ./Makefile.common

.PHONY: all test clean init reinit reconf docker start_all stop_all

all: check-init
	@cd lucida && gradle build
	@cd lucida && $(MAKE)

## build docker environment
docker:
	docker build -t $(DOCKER_CONTAINER):$(VERSION) .

## build local environment
#export LD_LIBRARY_PATH=/usr/local/lib

# Prerequisites
check-init:
	@if [ ! -e lucida/configure ]; then echo "Run 'make init' first to initialize the build."; exit 1; fi

test: check-init
	@cd lucida && gradle test
	@cd lucida && $(MAKE) test

clean: check-init
	@cd lucida && gradle clean
	@cd lucida && $(MAKE) clean

reinit-local:
	@cd deps && $(MAKE) clean

reinit: clean reinit-local init

# Inialize build - only need to do once
init:
	cd deps && $(MAKE) && cd ..
	if [ ! -e lucida/configure ]; then \
		cd lucida && ../scripts/autogen.sh && ./configure && cd ..; fi

# Autoconf reconfigure
reconf: check-init
	@touch lucida/configure.ac
	@cd lucida && $(MAKE)

# Start all servers
start_all:
	cd lucida && $(MAKE) start_server
	cd lucida && gradle start_server
	cd lucida/commandcenter && $(MAKE) start_server

# Stop all servers
stop_all:
	cd lucida && $(MAKE) stop_server
	cd lucida && gradle stop_server
	cd lucida/commandcenter && $(MAKE) stop_server
