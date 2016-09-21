#!/bin/bash

function brew_install() {
	brew list $1 >/dev/null 2>&1 && return 0
	echo "Installing $1"
	echo brew install $@
	brew $@
}

# Homebrew installs are local to the current user
brew update
brew_install autoconf
brew_install automake
brew_install libtool
brew_install cmake
# OpenCV
brew_install libdc1394
brew_install ffmpeg \
	--with-libvpx \
	--with-opus \
	--with-openjpeg \
	--with-openh264 \
	--with-faac \
	--with-fdk-aac \
	--with-x265 \
	--with-libvorbis \
	--with-libvidstab \
	--with-snappy \
	--with-xz \
	--with-tools
brew tap homebrew/science
#opencv --with-cuda  --with-vtk --with-contrib --with-quicktime
# LDFLAGS:  -L/usr/local/opt/opencv3/lib
# CPPFLAGS: -I/usr/local/opt/opencv3/include
# PKG_CONFIG_PATH: /usr/local/opt/opencv3/lib/pkgconfig
# If you need Python to find bindings for this keg-only formula, run:
# echo /usr/local/opt/opencv3/lib/python2.7/site-packages >> /usr/local/lib/python2.7/site-packages/opencv3.pth
# mkdir -p /Users/paul/.local/lib/python2.7/site-packages
# echo 'import site; site.addsitedir("/usr/local/lib/python2.7/site-packages")' >> /Users/paul/.local/lib/python2.7/site-packages/homebrew.pth
brew_install opencv3 \
	--c++11 \
	--with-ffmpeg \
	--with-gstreamer \
	--with-java \
	--with-libdc1394 \
	--with-opengl
if ! grep '/usr/local/opt/opencv3/lib/python2.7/site-packages' /usr/local/lib/python2.7/site-packages/opencv3.pth 2>/dev/null; then
	echo /usr/local/opt/opencv3/lib/python2.7/site-packages >> /usr/local/lib/python2.7/site-packages/opencv3.pth
	mkdir -p ~/.local/lib/python2.7/site-packages
	echo 'import site; site.addsitedir("/usr/local/lib/python2.7/site-packages")' >> ~/.local/lib/python2.7/site-packages/homebrew.pth
fi
# gRPC
brew_install openssl
brew_install maven
brew_install gradle

