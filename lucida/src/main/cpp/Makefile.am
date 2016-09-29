noinst_LTLIBRARIES=liblucida.la

AUTOMAKE_OPTIONS=subdir-objects
PROTOPATH=../proto

# List all input files here - no paths
PROTOFILES=lucida_service.proto

GENERATED_PATH=./generated

GENERATED_FILES_NOEXT=\
	lucida_service.pb \
	lucida_service.pb.grpc

# List all output files here - no paths
GENERATED_FILES=\
	$(addsuffix .h,$(GENERATED_FILES_NOEXT)) \
	$(addsuffix .cc,$(GENERATED_FILES_NOEXT))
GENERATED_FILE_PATHS=$(addprefix $(GENERATED_PATH),$(addprefix /,$(GENERATED_FILES)))
GENERATED_INC_PATHS=$(addprefix $(top_srcdir)/include/lucida/generated/,$(addsuffix .h,$(GENERATED_FILES_NOEXT)))

SUFFIXES = .cc .o
.cc.lo:
	$(LIBTOOL) --tag=CC --mode=compile cc -c $(CXXFLAGS) $(AM_CXXFLAGS) $*.cc -o $@ 

BUILT_SOURCES=$(GENERATED_FILE_PATHS) \
				$(GENERATED_INC_PATHS)
CLEANFILES=$(GENERATED_FILE_PATHS) \
			$(GENERATED_INC_PATHS)

$(GENERATED_FILE_PATHS): $(addprefix $(PROTOPATH), $(addprefix /, $(PROTOFILES)))
	mkdir -p $(GENERATED_PATH)
	protoc "--cpp_out=$(GENERATED_PATH)" "-I$(PROTOPATH)" "--plugin=protoc-gen-grpc=$(CPP_PLUGIN)" "--grpc_out=$(GENERATED_PATH)" $(addprefix $(PROTOPATH)/, $(PROTOFILES))

$(GENERATED_INC_PATHS): $(GENERATED_FILE_PATHS)
	cp $(GENERATED_PATH)/*.h $(top_srcdir)/include/lucida/generated

.PHONY: list-generated

list-generated:
	@echo "--- Generated files no extension ---"
	@echo "$(GENERATED_FILES_NOEXT)"
	@echo "---------- Generated files ---------"
	@echo "$(GENERATED_FILES)"
	@echo "------- Generated file paths -------"
	@echo "$(GENERATED_FILE_PATHS)"
	@echo "------ Generated include paths -----"
	@echo "$(GENERATED_INC_PATHS)"
	@echo "------------------------------------"

liblucida_la_SOURCES = $(GENERATED_FILE_PATHS) \
	request_builder.cpp \
	service_names.cpp \
	service_acceptor.cpp \
	service_connector.cpp

liblucida_la_CPPFLAGS = -I$(top_srcdir)/include

# Always static linkage
liblucida_la_LDFLAGS = -static

