cd BUILD || die "no build directory"
if python -mplatform | grep -i darwin; then
	unzip -u -d /usr/local protoc-$RELEASE-osx-x86_64.zip || die "install failed"
elif python -mplatform | grep -i 'ubuntu|debian|redhat|centos'; then
	tar -zxf protoc-$RELEASE-os-x86_64.tar.gz || die "install failed"
else
	die "unsupported platform"
fi
cd ..
