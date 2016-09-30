bin_PROGRAMS = faceserver

faceserver_SOURCES = \
	FACEHandler.h \
	FACEHandler.cpp \
	FACEServer.cpp

CAFFE = $(top_srcdir)/../deps/caffe/BUILD/caffe

faceserver_LDFLAGS = -L$(CAFFE)/install/lib -lcaffe -lgrpc++ -lgrpc \
            -lgflags -lglog -lpthread -lprotobuf -ljpeg -lboost_system
## -levent

faceserver_CPPFLAGS = $(CAFFE_CXXFLAGS) -I$(top_srcdir)/include -I$(CAFFE)/install/include

faceserver_LDADD = $(top_builddir)/src/main/cpp/lucida/liblucida.la

.PHONY: start_server stop_server

start_server:
	@echo "=========================================================="
	@echo "Attempting to start server in a virtual console (gnu screen)"
	@echo "  man screen for help"
	@echo "  use 'ctrl-A d' to detach from the console"
	@echo "  use 'screen -r $(bin_PROGRAMS)' to reattach"
	@echo "  use 'screen -list' to show all running consoles"
	@echo "  starting detached..."
	@echo ""
	@echo "  start_server.sh -c ./$(bin_PROGRAMS) $(filter-out $@,$(MAKECMDGOALS))"
	@echo "    pid will be saved at ./run/server.pid"
	@screen -d -m -S $(bin_PROGRAMS) $(top_srcdir)/../scripts/start_server.sh -c LD_LIBRARAY_PATH=$(CAFFE)/install/lib ./$(bin_PROGRAMS) $(filter-out $@,$(MAKECMDGOALS))

stop_server:
	@echo "=========================================================="
	@echo "Running stop_server.sh
	@echo "  this should close the $(bin_PROGRAMS) console if it exists"
	@echo "  run 'screen -list' to show all running consoles"
	@$(top_srcdir)/../scripts/stop_server.sh $(bin_PROGRAMS)
