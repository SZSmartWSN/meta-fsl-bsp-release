# Copyright (C) 2012-2016 O.S. Systems Software LTDA.
# Copyright (C) 2013-2016 Freescale Semiconductor
# Copyright 2017-2018 NXP

SUMMARY = "Test programs for IMX BSP"
DESCRIPTION = "Unit tests for the IMX BSP"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS  = "virtual/kernel alsa-lib libdrm"
do_configure[depends] += "virtual/kernel:do_shared_workdir"
DEPENDS_append_mx6 = " imx-lib"
DEPENDS_append_mx7 = " imx-lib"
DEPENDS_append_imxvpu = " virtual/imxvpu"

PE = "1"
PV = "7.0+${SRCPV}"

SRCBRANCH = "imx_4.14.62_1.0.0_beta"
IMXTEST_SRC ?= "git://source.codeaurora.org/external/imx/imx-test.git;protocol=https"
SRC_URI = "${IMXTEST_SRC};branch=${SRCBRANCH}"
SRC_URI_append = " file://memtool_profile "

SRCREV = "34422b58b4cbb6982344bf3815d607411dc1ae46"

S = "${WORKDIR}/git"

inherit module-base

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PLATFORM_mx6q  = "IMX6Q"
PLATFORM_mx6dl = "IMX6Q"
PLATFORM_mx6sl = "IMX6SL"
PLATFORM_mx6sll = "IMX6SL"
PLATFORM_mx6sx = "IMX6SX"
PLATFORM_mx6ul = "IMX6UL"
PLATFORM_mx7d  = "IMX7D"
PLATFORM_mx7ulp  = "IMX7D"
PLATFORM_mx8 = "IMX8"

PARALLEL_MAKE="-j 1"
EXTRA_OEMAKE += "${PACKAGECONFIG_CONFARGS}"

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG_append_imxvpu = " vpu"

PACKAGECONFIG[x11] = ",,libx11 libxdamage libxrender libxrandr"
PACKAGECONFIG[vpu] = "HAS_VPU=true,HAS_VPU=false,virtual/imxvpu"

do_compile() {
    CFLAGS="${TOOLCHAIN_OPTIONS}"
    INC=" \
        -I${STAGING_INCDIR} \
        -I${S}/include \
        -I${STAGING_KERNEL_BUILDDIR}/include/uapi \
        -I${STAGING_KERNEL_BUILDDIR}/include \
        -I${STAGING_KERNEL_DIR}/include/uapi \
        -I${STAGING_KERNEL_DIR}/include \
        -I${STAGING_KERNEL_DIR}/arch/arm/include \
        -I${STAGING_KERNEL_DIR}/drivers/mxc/security/rng/include \
        -I${STAGING_KERNEL_DIR}/drivers/mxc/security/sahara2/include"
    oe_runmake V=1 VERBOSE='' \
               CROSS_COMPILE=${TARGET_PREFIX} \
               CC="${CC} ${INC} -L${STAGING_LIBDIR} ${LDFLAGS}" \
               LINUXPATH=${STAGING_KERNEL_DIR} \
               KBUILD_OUTPUT=${STAGING_KERNEL_BUILDDIR} \
               PLATFORM=${PLATFORM}
}

do_install() {
    oe_runmake DESTDIR=${D}/unit_tests \
               PLATFORM=${PLATFORM} \
               install

    if [ -e ${WORKDIR}/clocks.sh ]; then
        install -m 755 ${WORKDIR}/clocks.sh ${D}/unit_tests/clocks.sh
    fi

    install -d -m 0755 ${D}/home/root/
    install -m 0644 ${WORKDIR}/memtool_profile ${D}/home/root/.profile
}

FILES_${PN} += "/unit_tests /home/root/.profile "
RDEPENDS_${PN} = "bash"

FILES_${PN}-dbg += "/unit_tests/.debug"

COMPATIBLE_MACHINE = "(mx6|mx7|mx8)"

EXTRA_OEMAKE += "SDKTARGETSYSROOT=${STAGING_DIR_HOST}"
