SUMMARY = "F Prime reference deployment (Ref)"
DESCRIPTION = "Builds the Ref reference deployment from the NASA F Prime flight software \
framework. The F Prime framework is built in-tree, as is standard for F Prime deployments; \
this recipe also serves as the template for building project deployments with OpenEmbedded."
HOMEPAGE = "https://github.com/nasa/fprime"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9ed4a07039b1bbecf29a156d2f6c9d37"

# Tag v4.2.0. The googletest submodule is not fetched: it is only required
# when BUILD_TESTING is enabled.
SRC_URI = "git://github.com/nasa/fprime.git;protocol=https;nobranch=1"
SRCREV = "f972bed2c56b1c497e70c6e7b1da7593e28f463b"

S = "${WORKDIR}/git"

inherit cmake python3native

# fpp autocoder suite and fprime-util/fprime-version-check are hard requirements
# of the F Prime CMake configure step (cmake/required.cmake).
DEPENDS = "fprime-fpp-native python3-fprime-tools-native"

OECMAKE_SOURCEPATH = "${S}/Ref"

# GCC 13 at -O2 flags a false-positive array-bounds diagnostic in
# Fw/DataStructures/Array.hpp (via Svc::TlmPacketizer), which F Prime's
# blanket -Werror turns fatal. Keep the warning visible but non-fatal;
# -Wno-error=<x> takes priority over a later -Werror.
CXXFLAGS += "-Wno-error=array-bounds"

# F Prime registers POST_BUILD per-component install steps that run during
# do_compile. Point the install prefix into the build tree (the same thing
# fprime-util does with build-artifacts) so those steps never touch the host,
# then collect the artifacts in do_install ourselves.
# FPrime_DIR: OE's toolchain file sets CMAKE_FIND_ROOT_PATH_MODE_PACKAGE=ONLY,
# which discards the deployment's in-tree find_package(FPrime PATHS ..) hint;
# the <pkg>_DIR cache variable is honored directly and survives F Prime's
# configure-time sub-builds, which forward cache variables.
EXTRA_OECMAKE = " \
    -DFPrime_DIR=${S}/cmake \
    -DCMAKE_INSTALL_PREFIX:PATH=${B}/build-artifacts \
    -DBUILD_TESTING=OFF \
    -DFPRIME_ENABLE_FRAMEWORK_UTS=OFF \
"

do_install() {
    # Artifacts land in build-artifacts/<toolchain-name>/Ref; the toolchain
    # name is derived from the toolchain file, so glob rather than hardcode.
    artifact_dir="$(dirname "$(find ${B}/build-artifacts -type d -name bin -path '*/Ref/*')")"
    if [ ! -x "${artifact_dir}/bin/Ref" ]; then
        bbfatal "Ref binary not found under ${B}/build-artifacts"
    fi
    install -d ${D}${bindir}
    install -m 0755 "${artifact_dir}/bin/Ref" ${D}${bindir}/Ref

    # Install the topology dictionary needed by the ground data system
    if [ -d "${artifact_dir}/dict" ]; then
        install -d ${D}${datadir}/fprime/Ref
        install -m 0644 "${artifact_dir}"/dict/* ${D}${datadir}/fprime/Ref/
    fi
}

FILES:${PN} += "${datadir}/fprime/Ref"
