# NASA F Prime

OpenEmbedded/Yocto recipes for the [NASA F Prime](https://github.com/nasa/fprime)
flight software framework.

## Contents

| Recipe | Purpose |
|---|---|
| `fprime-fpp-native` | FPP autocoder tool suite (prebuilt GraalVM native binaries from the PyPI wheel) |
| `python3-fprime-tools` | `fprime-util` / `fprime-version-check`, required by the F Prime CMake configure |
| `python3-cookiecutter`, `python3-binaryornot`, `python3-python-slugify`, `python3-text-unidecode` | Dependencies of fprime-tools missing from oe-core/meta-python in scarthgap |
| `fprime-ref` | The F Prime `Ref` reference deployment (v4.2.0), built for the target |

## Quick start (with kas, on ELISA Space Grade Linux)

```sh
git clone <this repo> layers/meta-aerospace
kas build layers/meta-aerosace/kas/fprime-sgl-qemuarm64.yml
```

This builds `core-image-minimal` for `qemuarm64` with the `Ref` deployment
binary installed at `/usr/bin/Ref` and its topology dictionary under
`/usr/share/fprime/Ref/`.

## Building your own deployment

Use `fprime-ref_4.2.0.bb` as a template: fetch your project (with F Prime as
submodule or sibling checkout referenced by `settings.ini`), set
`OECMAKE_SOURCEPATH` to your deployment directory, keep the
`CMAKE_INSTALL_PREFIX` override pointing into `${B}` and collect the binary in
`do_install`.

## Design notes

- F Prime deployments build the framework in-tree; the framework's install
  rules only fire for deployments, so there is deliberately no
  "framework static libs in the sysroot" recipe.
- The FPP tools are Scala programs distributed as GraalVM native-image
  binaries. Building them from source requires sbt and a JVM; this layer
  installs the prebuilt manylinux binaries instead (x86_64 and aarch64 build
  hosts only).
- Version pins matter: the F Prime CMake configure verifies the fpp tool
  version against the `requirements.txt` of the F Prime checkout being built.
  fpp 3.2.0 matches fprime v4.2.0.

## Compatibility

- Yocto scarthgap (5.0). Layer depends on oe-core and meta-python.
