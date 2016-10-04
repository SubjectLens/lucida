
AUTOMAKE_OPTIONS=foreign subdir-objects

if IS_DEBUGON
SUBDIRS= \
    include \
    src/main/cpp/lucida \
    src/test/cpp/lucida \
	djinntonic/imc \
	djinntonic/dig \
	djinntonic/face
else
SUBDIRS= \
    include \
    src/main/cpp/lucida \
    src/test/cpp/lucida \
	djinntonic/imc \
	djinntonic/dig \
	djinntonic/face
endif

EXTRADIST=\
	settings.gradle \
	gradlew \
	gradlew.bat \
	README.md

# Handle automake/make conflicts
TARGETDIR=$(strip $(if $(findstring $(abs_top_srcdir), $(abs_top_builddir)), $(abs_top_builddir)/bin, $(abs_top_builddir)))

.PHONY: test start_all

test: all
	cd $(top_builddir)/src/test/cpp/lucida && $(MAKE) test
	
all-local:

start_all: all

