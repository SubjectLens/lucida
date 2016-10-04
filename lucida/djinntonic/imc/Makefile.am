bin_PROGRAMS = imcserver

imcserver_SOURCES = \
	IMCHandler.h \
	IMCHandler.cpp \
	IMCServer.cpp

CAFFE = $(top_srcdir)/../deps/caffe/BUILD/caffe

imcserver_LDFLAGS = -L$(CAFFE)/install/lib $(top_builddir)/src/main/cpp/lucida/liblucida.la -lcaffe $(AM_LDFLAGS) -ljpeg -lboost_system
## -levent

imcserver_CPPFLAGS = $(CAFFE_CXXFLAGS) -I$(top_srcdir)/include -I$(CAFFE)/install/include

imcserver_LDADD = 

#.PHONY: start_server stop_server
# Using AM_EXTRA_RECURSIVE_TARGETS

start_server-local:
	@echo "=========================================================="
	@echo "Attempting to start server in a virtual console (gnu screen)"
	@echo "  man screen for help"
	@echo "  use 'ctrl-A d' to detach from the console"
	@echo "  use 'screen -r $(bin_PROGRAMS)' to reattach"
	@echo "  use 'screen -list' to show all running consoles"
	@echo "  starting detached..."
	@echo ""
	@echo "  start_server.sh ./$(bin_PROGRAMS)"
	@echo "    pid will be saved at ./run/server.pid"
	screen -d -m -S $(bin_PROGRAMS) LD_LIBRARY_PATH=$(CAFFE)/install/lib $(top_srcdir)/../scripts/start_server.sh ./$(bin_PROGRAMS) 

stop_server-local:
	@echo "=========================================================="
	@echo "Running stop_server.sh"
	@echo "  this should close the $(bin_PROGRAMS) console if it exists"
	@echo "  run 'screen -list' to show all running consoles"
	$(top_srcdir)/../scripts/stop_server.sh $(bin_PROGRAMS)

