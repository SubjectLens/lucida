#pragma once

#include <lucida/service_acceptor.h>
#include <caffe/caffe.hpp>

namespace lucida {

class DIGHandler : public AsyncServiceHandler {
public:
	DIGHandler();
	DIGHandler(const std::string& workdir);
private:
	void OnCreate(TypedCall<Request, ::google::protobuf::Empty>* call) override;
	void OnLearn(TypedCall<Request, ::google::protobuf::Empty>* call) override;
	void OnInfer(TypedCall<Request, Response>* call) override;

	void reshape(caffe::Net<float> *net, int input_size);

	std::string network_;
	std::string weights_;
	caffe::Net<float>* net_;
};

} // namespace lucida
