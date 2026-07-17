require ${BPN}-${PV}.inc

SRC_URI += "file://override-tbltool.patch"

S = "${WORKDIR}/git"

DEPENDS = "cfs-hosttools-native"

INSTALLPREFIX="/exe"

# Add O_${CFS_CFG} as a Makefile variable to override the default
EXTRA_OEMAKE = "\
 O=${B}\
 INSTALLPREFIX=${prefix}\
"

EXTRA_OECMAKE = "\
 -DMISSIONCONFIG='sample'\
 -DCMAKE_INSTALL_PREFIX='${INSTALLPREFIX}'\
"

inherit cmake

do_configure () {
    setup_sgl_linux

    # Use PREP_OPTS as an environment variables to allow
    # simple.mk to append to it
    oe_runmake -C "${S}" -f simple.mk prep \
    PREP_OPTS="${OECMAKE_ARGS} ${EXTRA_OECMAKE}"
}

do_compile () {
    oe_runmake -C "${S}" -f simple.mk all

    # Build cmdUtil for target — a standalone UDP command sender used to
    # interact with running cFS apps (e.g. send NO-OP to sample_app).
    ${CC} ${CFLAGS} ${LDFLAGS} -o ${B}/cmdUtil \
        ${S}/tools/cFS-GroundSystem/Subsystems/cmdUtil/cmdUtil.c \
        ${S}/tools/cFS-GroundSystem/Subsystems/cmdUtil/SendUdp.c
}

do_install () {
    oe_runmake -C "${S}" -f simple.mk DESTDIR=${D} install

    # Install cmdUtil alongside core-cpu1 so it is available on target
    install -m 0755 ${B}/cmdUtil ${D}${INSTALLPREFIX}/cpu1/cmdUtil
}


FILES:${PN} = "${INSTALLPREFIX}"
