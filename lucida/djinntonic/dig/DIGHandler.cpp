#include "DIGHandler.h"

#include <sstream>
#include <string>
#include <vector>
#include <unistd.h>

#include <jpeglib.h>
#include <gflags/gflags.h>
#include <csetjmp>
#include <lucida/path_ops.h>

DEFINE_string(dig_network, "configs/dig.prototxt",
              "Network config for dig (default: config/dig.prototxt");

DEFINE_string(dig_weights, "../models/dig.caffemodel",
              "Weight config for dig (default: weights/dig.caffemodel");

using caffe::Blob;
using caffe::Caffe;
using caffe::Net;

// possibly not needed due to using namespace std
using std::cout;
using std::endl;
using std::string;
using std::unique_ptr;
using std::shared_ptr;

namespace lucida {

struct jpegErrorManager {
  struct jpeg_error_mgr pub;
  jmp_buf setjmp_buffer;
};
char jpegLastErrorMsg[JMSG_LENGTH_MAX];
void jpegErrorExit (j_common_ptr cinfo)
{
  jpegErrorManager* myerr = (jpegErrorManager*) cinfo->err;
  ( *(cinfo->err->format_message) ) (cinfo, jpegLastErrorMsg);
  longjmp(myerr->setjmp_buffer, 1);
}

DIGHandler::DIGHandler(const std::string& workdir) {
  if (!MakeAbsolutePathOrUrl(this->network_, FLAGS_dig_network, workdir)) {
	  LOG(ERROR) << "failed to generated absolute path from <" << workdir << "> and <" << FLAGS_dig_network << ">";
	  this->network_ = FLAGS_dig_network;
  }
  if (!MakeAbsolutePathOrUrl(this->weights_, FLAGS_dig_weights, workdir)) {
	  LOG(ERROR) << "failed to generated absolute path from <" << workdir << "> and <" << FLAGS_dig_weights << ">";
	  this->network_ = FLAGS_dig_weights;
  }

  // load caffe model
  this->net_ = new Net<float>(this->network_, caffe::TEST);
  this->net_->CopyTrainedLayersFrom(this->weights_);
  LOG(ERROR) << "Finished initializing the handler!"; 
}

DIGHandler::DIGHandler() {
  this->network_ = FLAGS_dig_network;
  this->weights_ = FLAGS_dig_weights;

  // load caffe model
  this->net_ = new Net<float>(this->network_, caffe::TEST);
  this->net_->CopyTrainedLayersFrom(this->weights_);
  LOG(ERROR) << "Finished initializing the handler!"; 
}

void DIGHandler::OnCreate(TypedCall<Request, ::google::protobuf::Empty>* call) {
	using namespace ::grpc;
	LOG(ERROR) << "Create is not implemented";
	Status status(UNIMPLEMENTED, "digserver::create is not implemented");
	call->Finish(status);
}


void DIGHandler::OnLearn(TypedCall<Request, ::google::protobuf::Empty>* call) {
	using namespace ::grpc;
	LOG(ERROR) << "Learn is not implemented"; 
	Status status(UNIMPLEMENTED, "digserver::learn is not implemented");
	call->Finish(status);
}


void DIGHandler::OnInfer(TypedCall<Request, Response>* call) {
	using namespace ::grpc;
	// Handle malformed data
	if (!call->request_.has_spec() ||
		call->request_.spec().content_size() != 1 ||
		call->request_.spec().content(0).data_size() != 1) {
		Status status(FAILED_PRECONDITION, "digserver::infer expects content and data");
		call->Finish(status);
	}
  
	// In our gRPC implmentation the server call back is already running in a
	// worker thread so we we can process the request the same as we would for a 
	// synchronous design.
	auto image = call->request_.spec().content(0).data(0);

	// determine the image size
	int64_t img_size = 1 * 28 * 28;

	// Removed- set in Net constructor
	// Caffe::set_phase(Caffe::TEST);
	// use cpu
	Caffe::set_mode(Caffe::CPU);

	int img_num = 1;

	// prepare data into array
	float* data = (float*) malloc(img_num * img_size * sizeof(float));
	float* preds = (float*) malloc(img_num * sizeof(float));

	unsigned char* buffer;

	// read in the image
	std::istringstream is(image);
	is.seekg(0, is.end);
	int length = is.tellg();
	is.seekg(0, is.beg);
	char* img_buffer = new char[length];
	is.read(img_buffer, length);

	struct jpeg_decompress_struct cinfo;
	jpegErrorManager jerr; 
	cinfo.err = jpeg_std_error(&jerr.pub);
	jerr.pub.error_exit = jpegErrorExit;

	if (setjmp(jerr.setjmp_buffer)) {
		call->response_.set_msg("null");
		std::ostringstream os;
		os << "digserver::infer jpeg error - " << jpegLastErrorMsg;
		Status status(INVALID_ARGUMENT, os.str());
		call->Finish(status);
		return;
	}

	jpeg_create_decompress(&cinfo);

	jpeg_mem_src(&cinfo, (unsigned char*)img_buffer, length);
	jpeg_read_header(&cinfo, TRUE);
	jpeg_start_decompress(&cinfo);

	if (cinfo.output_components==1 ){
		img_size =
			cinfo.output_width * cinfo.output_height * cinfo.output_components;
		buffer = (unsigned char*) malloc(
				cinfo.output_components * cinfo.output_width);
		unsigned char* src = (unsigned char*) malloc( img_size );
		while (cinfo.output_scanline < cinfo.output_height) {
			(void) jpeg_read_scanlines(&cinfo, (JSAMPARRAY) &buffer, 1);
			for (int j = 0; j < cinfo.output_components; j++) {
				for (unsigned int k = 0; k < cinfo.output_width; k++) {
					int index = (cinfo.output_components -1 - j) *
						cinfo.output_width * cinfo.output_height +
						(cinfo.output_scanline - 1) * cinfo.output_width + k;
					src[index] = buffer[k * cinfo.output_components + j];
				}
			}
		}

		int s_w = cinfo.output_width; 
		int s_h = cinfo.output_height; 
		int d_w = 28;
		int d_h = 28;

		if (s_w <= d_w && s_h <= d_h) {
			double x_r = s_w / (double)d_w;
			double y_r = s_h / (double)d_h;

			for(int c=0; c<1 ; c++) {
				for (int i=0; i<d_h; i++) {
					for (int j=0; j<d_w; j++) {
						data[c*(d_h*d_w)+ i*d_w + j] = (float)src[c*(s_h*s_w) + int(floor(i*y_r)*s_w  + floor(j*x_r))];
					}  
				}  
			}

		} else if (s_w <= d_w && s_h > d_h) {
			int y_l = s_h / d_h;
			float x_r = s_w / (float) d_w;
			float y_r = s_h / (float) d_h;
			for (int c =0 ; c < 1; c++){
				for (int i = 0; i < d_h; i++) {
					for (int j = 0; j < d_w; j++) {
						int x_t = int(j * x_r);
						int y_b = int(i * y_r - y_r/2.0 + 0.5);
						int sum = 0;
						for (int ii=y_b; ii < y_b + y_l; ii++){
							if (ii>=0 && ii < s_h) {
								sum += src[c*(s_h*s_w)+ii*s_w + x_t]; 
							}
						}
						data[c*(d_h*d_w)+ i*d_w + j] = ((float)sum / y_l + 0.5);
					}
				}
			}
		} else if (s_w > d_w && s_h <= d_h) {
			int x_l = s_w / d_w;
			float x_r = s_w / (float) d_w;
			float y_r = s_h / (float) d_h;
			for (int c =0 ; c < 1; c++){
				for (int i = 0; i < d_h; i++) {
					for (int j = 0; j < d_w; j++) {
						int x_b = int(j * x_r - x_r/2.0 + 0.5);
						int y_t = int(i * y_r);
						int sum = 0;
						for (int jj=x_b; jj < x_b + x_l; jj++) {
							if (jj >=0 && jj < s_w) {
								sum += src[c*(s_h*s_w)+y_t*s_w + jj];
							}
						}
						data[c*(d_h*d_w)+ i*d_w + j] = ((float)sum / x_l + 0.5);
					}
				}
			}

		} else {
			int x_l = s_w / d_w;
			int y_l = s_h / d_h;
			float x_r = s_w / (float) d_w;
			float y_r = s_h / (float) d_h;
			for (int c =0 ; c < 1; c++){
				for (int i = 0; i < d_h; i++) {
					for (int j = 0; j < d_w; j++) {
						int x_b = int(j * x_r - x_r/2.0 + 0.5);
						int y_b = int(i * y_r - y_r/2.0 + 0.5);
						int sum = 0;
						for (int ii=y_b; ii < y_b + y_l; ii++){
							for (int jj=x_b; jj < x_b + x_l; jj++) {
								if (ii>=0 && ii < s_h && jj>=0 && jj < s_w) {
									sum += src[c*(s_h*s_w)+ii*s_w+jj]; 
								}
							}
						}
						data[c*(d_h*d_w)+ i*d_w + j] = ((float)sum / (x_l * y_l) + 0.5);
					}
				}
			}
		}

		delete[] img_buffer;
		delete[] buffer;
		delete[] src;
		jpeg_finish_decompress(&cinfo);
		jpeg_destroy_decompress(&cinfo);

		float loss;
		reshape(this->net_, img_num * 28*28*1);

		std::vector<Blob<float>*> in_blobs = this->net_->input_blobs();
		in_blobs[0]->set_cpu_data(data);
		std::vector<Blob<float>*> out_blobs = this->net_->ForwardPrefilled(&loss);
		memcpy(preds, out_blobs[0]->cpu_data(), img_num * sizeof(float));

		/*
		std::unique_ptr<std::string> digit =
			folly::make_unique<std::string>(std::to_string(int(preds[0])));
		promise->setValue(std::move(digit));
		*/
		call->response_.set_msg(std::to_string(int(preds[0])));
	} else {
		/*
		std::unique_ptr<std::string> image_class = 
			folly::make_unique<std::string>("null");
		promise->setValue(std::move(image_class));
		*/
		call->response_.set_msg("null");
	}
}

void DIGHandler::reshape(Net<float>* net, int input_size) {
  int n_in = net->input_blobs()[0]->num();
  int c_in = net->input_blobs()[0]->channels();
  int w_in = net->input_blobs()[0]->width();
  int h_in = net->input_blobs()[0]->height();

  int n_out = net->output_blobs()[0]->num();
  int c_out = net->output_blobs()[0]->channels();
  int w_out = net->output_blobs()[0]->width();
  int h_out = net->output_blobs()[0]->height();

  // assumes C, H, W are known, only reshapes batch dim
  if (input_size / (c_in * w_in * h_in) != n_in) {
    n_in = input_size / (c_in * w_in * h_in);
    net->input_blobs()[0]->Reshape(n_in, c_in, w_in, h_in);

    n_out = n_in;
    net->output_blobs()[0]->Reshape(n_out, c_out, w_out, h_out);
  }
}

} // namespace cpp2
