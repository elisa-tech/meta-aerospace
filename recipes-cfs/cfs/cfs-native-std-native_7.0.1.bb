require cfs-${PV}.inc

inherit cmake native

S = "${WORKDIR}/git"

# Add O_${CFS_CFG} as a Makefile variable to override the default
EXTRA_OEMAKE = "O_${CFS_CFG}=${B}"

# Use the native_std build configuration
CFS_CFG="native_std"

do_configure () {
    # Use PREP_OPTS_${CFS_CFG} as an environment variables to allow
    # target-configs.mk to append to it
    PREP_OPTS_${CFS_CFG}="${OECMAKE_ARGS}" \
    oe_runmake -C "${S}" ${CFS_CFG}.prep
}

do_compile () {
    oe_runmake -C "${S}" ${CFS_CFG}.compile
}

do_install () {
    oe_runmake -C "${S}" DESTDIR=${D} ${CFS_CFG}.install
}

