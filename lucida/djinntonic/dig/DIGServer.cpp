#include <gflags/gflags.h>
#include <glog/logging.h>
#include <lucida/path_ops.h>
#include <sstream>
#include <csignal>
#include "DIGHandler.h"

DEFINE_int32(port,
             8087,
             "Port for DIG server (default: 8087)");

DEFINE_int32(num_of_threads,
             4,
             "Number of threads (default: 4)");

using namespace lucida;

namespace {
	std::shared_ptr<AsyncServiceAcceptor> server_for_signal;
	bool shutdown = false;
}

void SignalShutdown(int) {
	server_for_signal->Shutdown();
}

int main(int argc, char* argv[]) {
	google::InitGoogleLogging(argv[0]);
	google::ParseCommandLineFlags(&argc, &argv, true);
	std::string workdir = Dirname(argv[0]);

	// Prep
	std::ostringstream os;
	os << "localhost:"<< FLAGS_port;
	std::shared_ptr<AsyncServiceAcceptor> server(new AsyncServiceAcceptor(new DIGHandler(workdir), "digserver"));
	server_for_signal = server;

	// Catch Ctrl-C and shutdown if received
	struct sigaction action = { 0 };
	struct sigaction actionPrev = { 0 };
	action.sa_handler = SignalShutdown;
	action.sa_flags |= 0;
	sigaction(SIGINT, &action, &actionPrev);
	
	// Start receiving RPC's
	server->Start(os.str(), FLAGS_num_of_threads);

	// Wait until shutdown
	server->BlockUntilShutdown();
	sigaction(SIGINT, &actionPrev, NULL);
	return 0;
}
