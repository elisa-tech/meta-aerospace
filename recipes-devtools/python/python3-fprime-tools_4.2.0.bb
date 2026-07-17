SUMMARY = "F Prime flight software framework build helper (fprime-util)"
DESCRIPTION = "Provides the fprime-util and fprime-version-check entry points and the \
fprime.fbuild Python package, all required at build time by the F Prime CMake system."
HOMEPAGE = "https://github.com/nasa/fprime-tools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

PYPI_PACKAGE = "fprime_tools"

SRC_URI[sha256sum] = "f0f421b617326c61cba54c4df9743e474716a8df704061cf8f2dbebb230ea8bf"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} = " \
    python3-cookiecutter \
    python3-gcovr \
    python3-markdown \
    python3-pexpect \
    python3-pytest \
    python3-requests \
    python3-urllib3 \
"

BBCLASSEXTEND = "native nativesdk"
