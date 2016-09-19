package ai.lucida.calendar;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

import io.grpc.stub.StreamObserver;
import com.google.protobuf.Empty;
import com.google.protobuf.ByteString;

import ai.lucida.grpc.LucidaServiceGrpc;
import ai.lucida.grpc.Request;
import ai.lucida.grpc.Response;
import ai.lucida.grpc.QuerySpec;
import ai.lucida.grpc.QueryInput;

/** 
 * Implementation of the calendar interface. A client request to any
 * method defined in the thrift file is handled by the
 * corresponding method here.
 */
public class CAServiceHandler extends LucidaServiceGrpc.LucidaServiceImplBase {
	private static final Logger logger = LoggerFactory.getLogger(CAServiceHandler.class);
	
    private final TextProcessor textProcessor_;
    
    public CAServiceHandler() {
        textProcessor_ = new TextProcessor();
    }
    
    public static void print(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}
    
    /**
     * <pre>
     * ask the intelligence to infer using the data supplied in the query
     * </pre>
     */
    public void infer(Request request, StreamObserver<Response> responseObserver) {

	    print("@@@@@ Infer; User: " + request.getLUCID());

	    if (request.getSpec().getContentList().isEmpty() || 
                request.getSpec().getContentList().get(0).getDataList().isEmpty()) {
            logger.info("empty content passed to service");
	        throw new IllegalArgumentException();
	    }

        try {
    	    String query_data = request.getSpec().getContentList().get(0).getDataList().get(0).toString("UTF-8");

    	    print("Asking: " + query_data);

    	    String[] time_interval = textProcessor_.parse(query_data);

    	    print("Result " + time_interval[0] + " " + time_interval[1]);

    	    responseObserver.onNext(Response.newBuilder()
                .setMsg(ByteString.copyFrom(time_interval[0] + " " + time_interval[1], "UTF-8"))
                .build());
            responseObserver.onCompleted();
                        
        } catch (UnsupportedEncodingException e) {
            logger.info("non UTF-8 encoding passed to service");
            responseObserver.onError(e);
        }
    }
}
