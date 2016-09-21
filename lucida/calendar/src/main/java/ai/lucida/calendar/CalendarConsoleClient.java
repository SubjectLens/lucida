package ai.lucida.calendar;

import java.io.UnsupportedEncodingException;

import com.google.protobuf.ByteString;
import ai.lucida.grpc.ServiceConnector;
import ai.lucida.grpc.Request;

/** 
 * A Calendar Client that get the upcoming events from Calendar Server and prints the results
 * to the console.
 */
public class CalendarConsoleClient {
	public static void main(String [] args) {
		// Collect the port number.
		int port = 8084;
		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		} else {
			System.out.println("Using default port for Calendar Client: " + port);
		}
        
		System.out.println("///// Connecting to Calendar... /////");
        ServiceConnector client = new ServiceConnector("localhost", port);

		// Query.
		String LUCID = "Clinc";
		Request request;
        try {
			System.out.println("///// Processing request... /////");
		    request = client.buildInferRequest(LUCID, "text", "What is on my Google calendar for last week?");
		    String result = client.infer(request);
		    System.out.println("///// Result: /////");
			System.out.println(result);
        } catch (Exception e) {
            System.out.println("request failed: " + e.getMessage());
        }
	}
}
