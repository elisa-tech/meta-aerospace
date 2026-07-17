# meta-aerospace

An OpenEmbedded/Yocto layer providing recipes for open-source aerospace
flight software frameworks, targeting embedded Linux — including the
ELISA [Space Grade Linux](https://elisa.tech/space-grade-linux-sig/)
(SGL) distribution.

## Contents

| Framework | Website | Source | Documentation | Main recipes |
|---|---|---|---|---|
| NASA Core Flight System (cFS) | [core-flight-system](https://etd.gsfc.nasa.gov/capabilities/core-flight-system/) | [nasa/cFS](https://github.com/nasa/cFS) | [README.cfs.md](README.cfs.md) | `cfs`, `cfs-hosttools-native`, `cfs-native-std-native` |
| NASA F Prime | [fprime.jpl.nasa.gov](https://fprime.jpl.nasa.gov) | [nasa/fprime](https://github.com/nasa/fprime) | [README.fprime.md](README.fprime.md) | `fprime-ref`, `fprime-fpp-native`, `python3-fprime-tools` |

Supporting recipes live in [recipes-devtools/](recipes-devtools/)
(host tools and python modules not provided by oe-core/meta-python).

## Quick start (with kas, on Space Grade Linux)

Clone this layer:

```sh
git clone <this repo> layers/meta-aerospace
```

Then build **one** of the following kas configurations, depending on the
framework you want. Each builds `core-image-minimal` for `qemuarm64` on
top of the SGL scarthgap configuration:

```sh
# NASA cFS
kas build layers/meta-aerospace/kas/cfs-sgl-qemuarm64.yml
```

```sh
# NASA F Prime
kas build layers/meta-aerospace/kas/fprime-sgl-qemuarm64.yml
```

See the per-framework READMEs linked above for what gets installed and
for design notes on each set of recipes.

## Space Grade Linux and ELISA

This layer is developed in the context of the
[ELISA](https://elisa.tech/) Aerospace Working Group, now continued as
the [Space Grade Linux Special Interest
Group](https://elisa.tech/space-grade-linux-sig/)
([aerospace mailing list](https://lists.elisa.tech/g/aerospace),
[space-grade-linux mailing list](https://lists.elisa.tech/g/space-grade-linux)).
The SGL reference distribution lives at
[elisa-tech/meta-sgl](https://github.com/elisa-tech/meta-sgl).

## Dependencies

  URI: https://github.com/openembedded/openembedded-core.git
  branch: scarthgap

  URI: https://github.com/openembedded/meta-openembedded.git (meta-python)
  branch: scarthgap

The layer is compatible with Yocto scarthgap (5.0).  the kas
configurations above pull in everything required via SGL.

## Adding the meta-aerospace layer to your build

Run 'bitbake-layers add-layer meta-aerospace'

## Patches

Please submit any patches against the meta-aerospace layer to the
maintainer:

Maintainer: Rob Woolley <rob.woolley@windriver.com>
