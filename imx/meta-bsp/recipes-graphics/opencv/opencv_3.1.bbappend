FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " file://0001-Fix-command-line-argument-handling-fixes-6525.patch \
                   file://0002-tapi-examples-Removing-defaults-from-all-command-lin.patch \
                   file://0003-fix-the-wrong-parameter-in-tvl1_optical_flow.cpp.patch \
                   file://0004-clahe.cl-has-some-incorrect-usage-of-opencl-__local-.patch \
"

# Enable opencv with qt for fb and wayland as gtk is not supported for non x11 backends
inherit cmake_qt5

PACKAGECONFIG[qt5] = "-DWITH_QT=ON -DWITH_GTK=OFF,-DWITH_QT=OFF,qtbase,"

PACKAGECONFIG_append = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' qt5', \
                bb.utils.contains('DISTRO_FEATURES', 'x11', '', ' qt5', d), d)}"

# This is needed to run samples that contains images
do_install_append() {

    install -d ${D}${datadir}/OpenCV/samples/data
    cp -r ${S}/samples/data/* ${D}${datadir}/OpenCV/samples/data
}

PACKAGES_append = " ${PN}-data"
FILES_${PN}-data = "${datadir}/OpenCV/samples/data/"
