if [ -z "$THREADS" ]; then
	THREADS=4
fi

mkdir -p BUILD &&
	git clone https://github.com/jhauswald/caffe.git &&
	cd caffe &&
	git checkout ipa &&
	cp Makefile.config.example Makefile.config &&
	make -j$THREADS &&
	make distribute ||
	die "build failed"
