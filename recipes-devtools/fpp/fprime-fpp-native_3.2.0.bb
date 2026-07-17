SUMMARY = "F Prime Prime (FPP) modeling language tools"
DESCRIPTION = "The FPP autocoder tool suite (fpp-depend, fpp-to-cpp, fpp-to-dict, ...) \
required at build time by the F Prime flight software framework. The tools are written \
in Scala and distributed as GraalVM native-image binaries via PyPI wheels; building them \
from source requires sbt and a JVM, so this recipe installs the prebuilt binaries."
HOMEPAGE = "https://github.com/nasa/fpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://fprime_fpp-${PV}.dist-info/licenses/LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e"

inherit native

# Prebuilt manylinux_2_28 wheels are only published for these build-host architectures
def fpp_wheel_src(d):
    wheels = {
        "x86_64": ("36/b2/242e4dd1b397a5372c238e5d5e4bd066a45d6685cabc9a719d8c7d47f5c8",
                   "f02bcfd88028c25e40fb7ac8cce18d554ee26c8943a1573df0c3dc1b545d205f"),
        "aarch64": ("3b/a6/cd52c9009cc8dbffe7d1d8ae0594f90a08090704a8fa81087442a0a7c6c0",
                    "7c1ab608b2c27abf096222b6dd2ec8756ea7c2a308d0358aab63b3ae1452ef3b"),
    }
    arch = d.getVar("BUILD_ARCH")
    if arch not in wheels:
        bb.fatal("fprime-fpp-native: no prebuilt fpp wheel for build host architecture '%s'" % arch)
    path, sha = wheels[arch]
    pv = d.getVar("PV")
    return ("https://files.pythonhosted.org/packages/%s/fprime_fpp-%s-py3-none-manylinux_2_28_%s.whl"
            ";downloadfilename=fprime_fpp-%s-%s.zip;subdir=fprime-fpp-%s;sha256sum=%s"
            % (path, pv, arch, pv, arch, pv, sha))

SRC_URI = "${@fpp_wheel_src(d)}"

S = "${WORKDIR}/fprime-fpp-${PV}"

# The GraalVM native-image binary is prebuilt against the build host's glibc
INHIBIT_SYSROOT_STRIP = "1"

# F Prime's CMake locates each tool as a separate fpp-<subcommand> executable.
# The multiplexed 'fpp' binary takes the subcommand as its first argument, so
# thin shell wrappers replicate the wheel's console_scripts dispatch.
FPP_SUBCOMMANDS = "check depend filenames format from-xml locate-defs locate-uses syntax to-cpp to-dict to-layout"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/fprime_fpp/fpp ${D}${bindir}/fpp
    for sub in ${FPP_SUBCOMMANDS}; do
        cat > ${D}${bindir}/fpp-${sub} <<EOF
#!/bin/sh
exec "\$(dirname "\$(readlink -f "\$0")")/fpp" ${sub} "\$@"
EOF
        chmod 0755 ${D}${bindir}/fpp-${sub}
    done
}
