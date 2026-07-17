SUMMARY = "A command-line utility that creates projects from project templates"
HOMEPAGE = "https://github.com/cookiecutter/cookiecutter"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2697b65c7f6d940d2ffb22f8089b71ff"

SRC_URI[sha256sum] = "db21f8169ea4f4fdc2408d48ca44859349de2647fbe494a9d6c3edfc0542c21c"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-arrow \
    python3-binaryornot \
    python3-click \
    python3-jinja2 \
    python3-python-slugify \
    python3-pyyaml \
    python3-requests \
    python3-rich \
"

BBCLASSEXTEND = "native nativesdk"
