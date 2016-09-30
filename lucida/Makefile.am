
AUTOMAKE_OPTIONS=foreign subdir-objects

if IS_DEBUGON
SUBDIRS= \
    include \
    src/main/cpp \
	djinntonic/imc \
	djinntonic/face
else
SUBDIRS= \
    include \
    src/main/cpp \
	djinntonic/imc \
	djinntonic/face
endif

EXTRADIST=\
	include/lucida/generated/*.h \
	src/main/cpp/generated/*.cc \
	src/main/cpp/generated/*.h \
	settings.gradle \
	gradlew \
	gradlew.bat \
	README.md

# Handle automake/make conflicts
TARGETDIR=$(strip $(if $(findstring $(abs_top_srcdir), $(abs_top_builddir)), $(abs_top_builddir)/bin, $(abs_top_builddir)))


all-local:

