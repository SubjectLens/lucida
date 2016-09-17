/*
 * Copyright 2016 (c). All rights reserved.
 * Author: Paul Glendenning
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of the author, nor the names of other
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ai.lucida.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.math.BigInteger;
import java.security.SecureRandom;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.lucida.grpc.ServiceNames;
import ai.lucida.grpc.Request;
import ai.lucida.grpc.Response;
import ai.lucida.grpc.QuerySpec;
import ai.lucida.grpc.QueryInput;
import ai.lucida.grpc.LucidaServiceGrpc;

/**
 * Lucida client of gRPC service.
 */
public class ServiceConnector {
	private static final Logger logger = LoggerFactory.getLogger(ServiceConnector.class);

	private final ManagedChannel channel_;
	private final LucidaServiceGrpc.LucidaServiceBlockingStub blockingStub_;
	private final LucidaServiceGrpc.LucidaServiceStub asyncStub_;
	private final LucidaServiceGrpc.LucidaServiceFutureStub futureStub_;
	private final SecureRandom random_ = new SecureRandom();

	/**
	 * Construct client for accessing a Lucida service at {@code host:port}.
	 * If port=443 then the channel will be secure via TLS, otherwisethe channel
	 * is insecure. No authentication is provided in either case.
	 *
	 * @param host	Fully qualified host name
	 * @param port	The port number [1,65536)
	 */
	public ServiceConnector(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
	}

	/**
	 * Construct client for accessing a Lucida service using an existing channel.
	 * The channel credentials and encryption is dictated by the channelBuilder.
	 *
	 * @param channelBulder The channel builder.
	 * @see io.grpc.ManagedChannelBuilder
	 * @see io.grpc.NettyChannelBuilder
	 */
	public ServiceConnector(ManagedChannelBuilder<?> channelBuilder) {
		channel_ = channelBuilder.build();
		blockingStub_ = LucidaServiceGrpc.newBlockingStub(channel_);
        asyncStub_ = LucidaServiceGrpc.newStub(channel_);
        futureStub_ = LucidaServiceGrpc.newFutureStub(channel_);
	}

	/**
	 * Channel accessor.
	 *
	 * @return The io.grpc.Channel for this client.
	 */
	Channel getChannel() {
		return channel_;
	}

	/**
	 * Stub accessor.
	 *
	 * @return The blocking stub for this client.
	 */
	LucidaServiceGrpc.LucidaServiceBlockingStub getBlockingStub() {
		return blockingStub_;
	}

	/**
	 * Stub accessor.
	 *
	 * @return The async stub for this client.
	 */
	LucidaServiceGrpc.LucidaServiceStub getAsyncStub() {
		return asyncStub_;
	}
    
	/**
	 * Stub accessor.
	 *
	 * @return The furure stub for this client.
	 */
	LucidaServiceGrpc.LucidaServiceFutureStub getFutureStub() {
		return futureStub_;
	}
    
	/**
	 * Shutdown the client connection and wait for a maximum timeout seconds.
	 *
	 * @param  timeout Timeout in second
	 * @throws InterruptedException  If the shutdown did not complete within
	 *         the specified timeout.
	 */
	public void shutdown(int timeout) throws InterruptedException {
		channel_.shutdown().awaitTermination(timeout, SECONDS);
	}

	/**
	 * Create a random string.
	 *
	 * @return	A nonce.
	 */
	public String createNonce() {
        String n = new BigInteger(130, random_).toString(32);
        return n;
	}

	/**
	 * Create an intelligent instance based on supplied id.
	 *
	 * @param  Id   The LUCID.
	 * @return True if successful.
	 */
	public boolean create(String id) {
		logger.info("{} {}", ServiceNames.createCommandName, id);
		Request request = Request.newBuilder()
				.setLUCID(id)
				.setSpec(QuerySpec.newBuilder()
                    .setName(ServiceNames.createCommandName))
				.build();
		try {
			blockingStub_.create(request);
		} catch (StatusRuntimeException e) {
			logger.warn("RPC failed: {}", e.getStatus());
			return false;
		}
		return true;
	}

	/**
	 * General blocking request for learn. Building is left to the caller.
	 * @param  The request.
	 * @return True if successful.
	 */
	public boolean learn(Request request) {
		try {
			blockingStub_.learn(request);
		} catch (StatusRuntimeException e) {
			logger.warn("RPC failed: {}", e.getStatus());
			return false;
		}
		return true;
	}

	/**
	 * Learn from text, url, or image data.
	 *
	 * @param Id    The LUCID.
	 * @param input The data to learn from.
	 */
	public boolean learn(String id, java.lang.Iterable<QueryInput> input) {
		logger.info("SYNC  {} variant {}", ServiceNames.learnCommandName, id);
		Request request = Request.newBuilder()
			.setLUCID(id)
			.setSpec(QuerySpec.newBuilder()
                .setName(ServiceNames.learnCommandName)
				.addAllContent(input))
			.build();
        return learn(request);
	}

	/**
	 * Learn from text, url, or image data.
	 *
	 * @param Id    The LUCID.
	 * @param input The data to learn from.
	 * @return True if successful.
	 */
	public boolean learn(String id, QueryInput input) {
		return learn(id, Collections.singletonList(input));
	}

	/**
	 * Learn from text or URL.
	 *
	 * @param Id        The LUCID.
	 * @param type      A serice type name.
	 * @param textOrUrl The data to learn from.
	 * @return True if successful.
	 * @see   ai.lucida.grpc.ServiceNames
	 */
	public boolean learn(String id, String type, java.lang.Iterable<ByteString> textOrUrl) {
		logger.info("SYNC  {} {} {}", ServiceNames.learnCommandName, type, id);
        if (!ServiceNames.isTypeName(type))
            return false;
		Request request = Request.newBuilder()
			.setLUCID(id)
			.setSpec(QuerySpec.newBuilder()
				.setName(ServiceNames.learnCommandName)
				.addContent(QueryInput.newBuilder()
					.setType(type)
					.addAllData(textOrUrl)))
			.build();
        return learn(request);
	}
    
	/**
	 * General blocking request for infer. Building is left to the caller.
	 * @param  The request.
	 * @return A ByetString if successful, else null.
	 */
	public ByteString infer(Request request) {
        Response result = null;
		try {
			result = blockingStub_.infer(request);
		} catch (StatusRuntimeException e) {
			logger.warn("RPC failed: {}", e.getStatus());
			return null;
		}
		return result.getMsg();
	}

	/**
	 * Infer from text or url or bytes.
	 *
	 * @param Id    The LUCID.
	 * @param input The data to learn from.
	 */
	public ByteString infer(String id, String type, ByteString data, java.lang.Iterable<String> tags) {
		logger.info("SYNC  {} {} {}", ServiceNames.inferCommandName, type, id);
        if (!ServiceNames.isTypeName(type))
            return null;
		Request request = Request.newBuilder()
			.setLUCID(id)
			.setSpec(QuerySpec.newBuilder()
				.setName(ServiceNames.inferCommandName)
    			.addContent(QueryInput.newBuilder()
    				.setType(type)
    				.addData(data)
                    .addAllTags(tags)))
			.build();
        return infer(request);
	}

	/**
	 * Infer from text or url.
	 *
	 * @param Id    The LUCID.
	 * @param input The data to learn from.
	 */
	public ByteString infer(String id, String type, ByteString data) {
		logger.info("SYNC  {} {} {}", ServiceNames.inferCommandName, type, id);
        if (!ServiceNames.isTypeName(type))
            return null;
		Request request = Request.newBuilder()
			.setLUCID(id)
			.setSpec(QuerySpec.newBuilder()
				.setName(ServiceNames.inferCommandName)
    			.addContent(QueryInput.newBuilder()
    				.setType(type)
    			    .addData(data)))
			.build();
        return infer(request);
	}

	/**
	 * Infer from bytes.
	 *
	 * @param Id    The LUCID.
	 * @param input The data to learn from.
	 */
	public ByteString infer(String id, String type, ByteString data, String tag) {
		logger.info("SYNC  {} {} {}", ServiceNames.inferCommandName, type, id);
        if (!ServiceNames.isTypeName(type))
            return null;
		Request request = Request.newBuilder()
			.setLUCID(id)
			.setSpec(QuerySpec.newBuilder()
				.setName(ServiceNames.inferCommandName)
    			.addContent(QueryInput.newBuilder()
    				.setType(type)
        			.addData(data)
                    .addTags(tag)))
			.build();
        return infer(request);
	}
}
