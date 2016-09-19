package ai.lucida.calendar;

import java.io.IOException;

import ai.lucida.grpc.ServiceAcceptor;
import ai.lucida.calendar.CAServiceHandler;

/**
 * Starts the calendar server and listens for requests.
 */
public class CalendarDaemon {
	/**
	 * Main method.  This comment makes the linter happy.
	 */
	public static void main(String[] args) throws Exception {
		ServiceAcceptor server = new ServiceAcceptor(8980, new CAServiceHandler());
		server.start();
		System.out.println("CA at port 8084");
		server.blockUntilShutdown();
	}
}
