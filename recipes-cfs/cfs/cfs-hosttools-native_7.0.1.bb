require cfs-${PV}.inc

inherit cmake native

S = "${WORKDIR}/git"

# Add O_${CFS_CFG} as a Makefile variable to override the default
EXTRA_OEMAKE = "O_${CFS_CFG}=${B}"

# Use the native_std build configuration
CFS_CFG="native_std"

MISSIONCONFIG="sample"
TARGETSYSTEM="sgl-linux"

OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OECMAKE = "\
  -DMISSIONCONFIG=${MISSIONCONFIG} \
  -DCFE_EDS_ENABLE=OFF \
"

do_configure () {
    setup_sgl_linux

    cmake \
      ${OECMAKE_GENERATOR_ARGS} \
      -S "${S}/cfe" \
      -B "${B}" \
      ${OECMAKE_ARGS} \
      ${EXTRA_OECMAKE} \
      -Wno-dev
}

do_compile () {
    MISSIONCONFIG=${MISSIONCONFIG}  \
    oe_runmake -C "${B}" cfeconfig_platformdata_tool elf2cfetbl
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/cfeconfig_platformdata_tool/cfeconfig_platformdata_tool ${D}${bindir}
    install -m 0755 ${B}/tools/elf2cfetbl/elf2cfetbl ${D}${bindir}
}
