package ai.lucida.calendar;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.stub.StreamObserver;
import com.google.protobuf.Empty;

import ai.lucida.grpc.LucidaServiceGrpc;
import ai.lucida.grpc.Request;
import ai.lucida.grpc.Response;

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

    @Override
    /** {@inheritDoc} */
    public void create(Request request, StreamObserver<Empty> responseObserver) {
        /* Do nothing */
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    /** {@inheritDoc} */
    public void learn(Request request, StreamObserver<Empty> responseObserver) {
        /* Do nothing */
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    /** {@inheritDoc} */
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
                .setMsg(time_interval[0] + " " + time_interval[1])
                .build());
            responseObserver.onCompleted();
                        
        } catch (UnsupportedEncodingException e) {
            logger.info("non UTF-8 encoding passed to service");
            responseObserver.onError(e);
        }
    }
}
