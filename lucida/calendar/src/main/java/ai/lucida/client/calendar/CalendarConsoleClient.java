package ai.lucida.client.calendar;

import java.io.UnsupportedEncodingException;

import com.google.protobuf.ByteString;
import ai.lucida.grpc.ServiceConnector;
import ai.lucida.grpc.ServiceNames;

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
        try {       
		    ByteString query_input_data = ByteString.copyFrom("What is on my Google calendar for last week?", "UTF-8");
		    System.out.println("///// Processing request... /////");
		    ByteString result = client.infer(LUCID, "text", query_input_data);
		    System.out.println("///// Result: /////");
            if (result != null)
		        System.out.println(result.toString());
            else
		        System.out.println("request failed");
        } catch (UnsupportedEncodingException e) {
            System.out.println("request failed: " + e.getMessage());
        }
		return;
	}
}
