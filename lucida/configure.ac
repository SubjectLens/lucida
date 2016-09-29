dnl Autotool for XTL

AC_PREREQ(2.59)
AC_INIT([lucida], [0.1.0])
AC_CANONICAL_SYSTEM
AC_CONFIG_HEADERS([config.h])
AM_INIT_AUTOMAKE([-Wno-portability])

case "$host" in
x86_64-*Darwin*)
    OS="Darwin";ARCH="x86_64";;
i386-*Darwin*)
    OS="Darwin";ARCH="x86";;
i686-*linux* | i586-*linux*)
    OS="Linux";ARCH="x86";;
x86_64*linux*)
    OS="Linux";ARCH="x86_64";;
esac

dnl ---------------------------------------------------------------------------
dnl Environment checks

AM_PROG_CC_C_O
AC_PROG_CXX
AC_PROG_CPP
AC_PROG_INSTALL
AC_PROG_RANLIB
AC_PROG_LIBTOOL
AC_PROG_MKDIR_P
AC_HEADER_STDC([])

dnl Always use C11
AM_CXXFLAGS="-std=c++11 -Wno-deprecated"

AM_CONDITIONAL(IS_OSX, [test x$OS = xDarwin])
AM_CONDITIONAL(IS_X64, [test x$ARCH = xx86_64])

dnl ---------------------------------------------------------------------------
dnl Place this copyright notice in generated configure

AC_COPYRIGHT([Copyright (c) Solidra LLC. All rights reserved.])

dnl ---------------------------------------------------------------------------
dnl Enable/disable debugging

AC_ARG_ENABLE([debug],
              [AC_HELP_STRING([--enable-debug],
                              [Build binaries with debugging info.])],
              [AS_IF([test "x$enable_debug" = "xyes"], 
                       [AM_CXXFLAGS="${AM_CXXFLAGS} -g -O0 -DDEBUG"],
                     [test "x$enable_debug" = "xno"], 
                       [AM_CXXFLAGS="${AM_CXXFLAGS} -DNDEBUG"])],
              [AM_CXXFLAGS="${AM_CXXFLAGS} -DNDEBUG"])

AM_CONDITIONAL(IS_DEBUGON, [echo "${CXXFLAGS}${AM_CXXFLAGS}" | grep 'DDEBUG' >/dev/null 2>/dev/null ])

dnl ---------------------------------------------------------------------------

CPP_PLUGIN="`which grpc_cpp_plugin`"
AS_IF([test "x$CPP_PLUGIN" = "x"],
	[AC_MSG_ERROR([cannot find C++ gRPC plugin])])
AC_MSG_CHECKING([gRPC plugin])
AC_MSG_RESULT([$CPP_PLUGIN])


dnl ---------------------------------------------------------------------------
AC_CONFIG_FILES(
    [
    Makefile
    include/Makefile
    src/main/cpp/Makefile
    ])

AC_SUBST(AM_CXXFLAGS)
AC_SUBST(CPP_PLUGIN)
AC_OUTPUT

