SUMMARY = "The most basic Text::Unidecode port"
HOMEPAGE = "https://github.com/kmike/text-unidecode"
LICENSE = "Artistic-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ccfb8875bc60bc3d6e91e42e5e8f5587"

SRC_URI[sha256sum] = "bad6603bb14d279193107714b288be206cac565dfa49aa5b105294dd5c4aab93"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
