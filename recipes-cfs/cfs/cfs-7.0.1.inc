LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/cf/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/ci_lab/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/cs/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/ds/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/fm/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/hk/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/hs/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/lc/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/md/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/mm/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/sample_app/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/sbn/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/sc/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/sch_lab/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://apps/to_lab/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://cfe/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://libs/sample_lib/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://osal/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://psp/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://psp/fsw/generic-vxworks-rtp/parg/LICENSE;md5=2a1c47ec0cd7a3f142124fe8d1c69097 \
                    file://tools/cFS-GroundSystem/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://tools/cfs-cosmos-plugin/LICENSE.txt;md5=cf078ca122a2956eee80afc7df13af70 \
                    file://tools/eds/LICENSE.txt;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://tools/elf2cfetbl/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://tools/tblCRCTool/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "gitsm://github.com/nasa/cFS.git;protocol=https;name=cfs;branch=dev \
	   git://github.com/nasa/cFE.git;protocol=https;name=cfe;branch=dev;subdir=cfe \
	   git://github.com/nasa/osal.git;protocol=https;name=osal;branch=dev;subdir=osal \
	   git://github.com/nasa/PSP.git;protocol=https;name=psp;branch=dev;subdir=psp \
           git://github.com/nasa/ci_lab.git;protocol=https;name=cilab;branch=dev;subdir=apps/ci_lab\
           git://github.com/nasa/to_lab.git;protocol=https;name=tolab;branch=dev;subdir=apps/to_lab\
           git://github.com/nasa/sch_lab.git;protocol=https;name=schlab;branch=dev;subdir=apps/sch_lab\
           git://github.com/nasa/sample_app.git;protocol=https;name=sampleapp;branch=dev;subdir=apps/sample_app\
           git://github.com/nasa/sample_lib.git;protocol=https;name=samplelib;branch=dev;subdir=libs/sample_lib\
           git://github.com/nasa/cFS-GroundSystem.git;protocol=https;name=cFS-GroundSystem;branch=dev;subdir=tools/cFS-GroundSystem\
           git://github.com/nasa/elf2cfetbl.git;protocol=https;name=elf2cfetbl;branch=dev;subdir=tools/elf2cfetbl\
           git://github.com/nasa/tblCRCTool.git;protocol=https;name=tblCRCTool;branch=dev;subdir=tools/tblCRCTool\
           git://github.com/nasa/EdsLib.git;protocol=https;name=eds;branch=dev;subdir=tools/eds\
           git://github.com/nasa/CF.git;protocol=https;name=cf;branch=dev;subdir=apps/cf\
           git://github.com/nasa/CS.git;protocol=https;name=cs;branch=dev;subdir=apps/cs\
           git://github.com/nasa/DS.git;protocol=https;name=ds;branch=dev;subdir=apps/ds\
           git://github.com/nasa/FM.git;protocol=https;name=fm;branch=dev;subdir=apps/fm\
           git://github.com/nasa/HK.git;protocol=https;name=hk;branch=dev;subdir=apps/hk\
           git://github.com/nasa/HS.git;protocol=https;name=hs;branch=dev;subdir=apps/hs\
           git://github.com/nasa/LC.git;protocol=https;name=lc;branch=dev;subdir=apps/lc\
           git://github.com/nasa/MD.git;protocol=https;name=md;branch=dev;subdir=apps/md\
           git://github.com/nasa/MM.git;protocol=https;name=mm;branch=dev;subdir=apps/mm\
           git://github.com/nasa/SBN.git;protocol=https;name=sbn;branch=dev;subdir=apps/sbn\
           git://github.com/nasa/SC.git;protocol=https;name=sc;branch=dev;subdir=apps/sc\
           git://github.com/nasa/cfs-cosmos-plugin.git;protocol=https;name=cfs-cosmos-plugin;branch=dev;subdir=tools/cfs-cosmos-plugin\
           git://github.com/nasa/cfs-commandline-tools.git;protocol=https;name=commandline-tools;branch=dev;subdir=tools/commandline-tools\
"

SRCREV = "d74cc5e2eae23c337ec33167e6c1cbb305dc67a3"
SRCREV_cfe = "8011cff7a0f636c6a83f6470300ac490ebb2482b"
SRCREV_osal = "31916a43daf72f062acab364ec235761bba8a2d4"
SRCREV_psp = "ce9126a61dd9233d6b33b92b9e65fca9989e7d5c"
SRCREV_cf = "3e248738aedb06b0e99baeaa03101184ecf9ae47"
SRCREV_cilab = "fdd680437d9e50422d364436030d2aab5b73e600"
SRCREV_cs = "32bce67683c76f33cf4eddc3cda2372728a19737"
SRCREV_ds = "0cd8596f213183726506049cf2203fc197d54b35"
SRCREV_fm = "a9d92d4a969dd3fff3ca3be7cd1e06efb88d672c"
SRCREV_hk = "e335152d702474ac8f57b63891d54228abd41f79"
SRCREV_hs = "0048f957f1638beb1cb2f40c49642aad8509cb73"
SRCREV_lc = "6db86600e6edb68a9fd16cc820224dc28bd674cc"
SRCREV_md = "2399492e46adf051bf8b8f11307ab2dc6d2b11d0"
SRCREV_mm = "f7e0141ef7a8515b8e944e55ec3fc0b05bd10a56"
SRCREV_sampleapp = "b21871b8747c67320d99962416757ddffa3244a5"
SRCREV_samplelib = "537c9297f759ccd4b0138d9c0b9abc065027417f"
SRCREV_sbn = "9ebe9aa98929d9fec08886f4d0c9fc463f652548"
SRCREV_sc = "e488b4946f7b486986c29fca00ca9f4d2efe5365"
SRCREV_schlab = "bfc82af9dfec38722b8d68ff1cf232f1b58662d1"
SRCREV_tolab = "56394aef0ae3fd4081eb4b9245bf8e0947f93560"
SRCREV_gcfe = "8011cff7a0f636c6a83f6470300ac490ebb2482b"
SRCREV_gosal = "31916a43daf72f062acab364ec235761bba8a2d4"
SRCREV_gpsp = "ce9126a61dd9233d6b33b92b9e65fca9989e7d5c"
SRCREV_cFS-GroundSystem = "23209ff0568303e01ae926fc25911a7ca7c1b6bf"
SRCREV_cfs-cosmos-plugin = "7fcc03a57534f6189390b4d15da68c3318687123"
SRCREV_commandline-tools = "42b1b5b5075d998ed08c315780588b6c07de2d8a"
SRCREV_eds = "c44fc670d54d5d61abc5954a0844e3f349c47070"
SRCREV_elf2cfetbl = "a8710d2d5c2ffd9cae1370cbed405e3d7fda8454"
SRCREV_tblCRCTool = "45dbc42b05978d50e9f9842416a3ba3791e7dbc0"

SRCREV_FORMAT = "cfs"

setup_sgl_linux () {
    # Copy the default osconfig into a customer sgl-linux osconfig
    cp ${S}/sample_defs/default_osconfig.cmake ${S}/sample_defs/sgl-linux_osconfig.cmake

    # Copy our CMake toolchain file into the sample directory
    cp ${WORKDIR}/toolchain.cmake ${S}/sample_defs/toolchain-sgl-linux.cmake
    echo 'SET(CFE_SYSTEM_PSPNAME      "pc-linux")' >> ${S}/sample_defs/toolchain-sgl-linux.cmake
    echo 'SET(OSAL_SYSTEM_OSTYPE      "posix")'    >> ${S}/sample_defs/toolchain-sgl-linux.cmake

    # Set the sample app to use the sgl-linux toolchain
    sed -i -e 's/^SET(cpu1_SYSTEM riscv64-poky-linux)/SET(cpu1_SYSTEM sgl-linux)/g' ${S}/sample_defs/targets.cmake
    sed -i -e 's/^SET(cpu2_SYSTEM mips32r2-poky-linux)/SET(cpu2_SYSTEM sgl-linux)/g' ${S}/sample_defs/targets.cmake
}
