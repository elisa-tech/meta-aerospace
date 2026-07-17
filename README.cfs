# NASA Core Flight System (cFS)

OpenEmbedded/Yocto recipes for the [NASA Core Flight System](https://github.com/nasa/cFS)
flight software framework (cFE 7 / Draco line, pinned from the `dev` branches).

## Contents

| Recipe | Purpose |
|---|---|
| `cfs` | The full cFS bundle (cFE + OSAL + POSIX PSP, lab apps, and the CFS application suite: CF, CS, DS, FM, HK, HS, LC, MD, MM, SBN, SC), cross-compiled for the target and installed under `/exe` |
| `cfs-hosttools-native` | Host-side build tools needed during the cross build: `elf2cfetbl` (table binary generator) and `cfeconfig_platformdata_tool` |
| `cfs-native-std-native` | The stock `native_std` build configuration compiled entirely for the build host — useful for running cFS on the build machine and sanity-checking the pinned revisions |
| `cfs-7.0.1.inc` | Shared metadata: SRC_URI/SRCREV pins for every bundle component and the `setup_sgl_linux` helper that wires the Yocto toolchain into the cFS build |

## Quick start

```sh
bitbake-layers add-layer meta-aerospace
bitbake cfs
```

Add `cfs` to your image; the mission tree installs under `/exe`
(`core-cpu1` executable, apps and tables in `cf/`). On the target:

```sh
cd /exe && ./core-cpu1
```

## Design notes

- **Toolchain injection (`sgl-linux` target system)** — cFS selects a
  compiler per CPU via `SET(cpuN_SYSTEM <name>)` in `targets.cmake`, which
  resolves to `toolchain-<name>.cmake` in the mission `_defs` directory.
  The `setup_sgl_linux` helper copies the toolchain file that
  `cmake.bbclass` generates into
  `sample_defs/toolchain-sgl-linux.cmake` (appending
  `CFE_SYSTEM_PSPNAME=pc-linux` and `OSAL_SYSTEM_OSTYPE=posix`) and
  rewrites `targets.cmake` so cpu1/cpu2 build with it. The cross build
  therefore uses the standard Yocto toolchain and sysroot.
- **Host table tool** — during a cross build, cFE's `tabletool-execute`
  step normally runs the `elf2cfetbl` binary it just built, which cannot
  execute on the build host. The carried patch
  (`override-tbltool.patch`) makes the tables Makefile take `elf2cfetbl`
  from `PATH`, and `cfs` DEPENDS on `cfs-hosttools-native` to provide it
  from the native sysroot.
- **Submodule pinning** — the nasa/cFS bundle references cFE, OSAL, PSP,
  the apps and the tools as git submodules. The include file fetches each
  one as an explicit `git://` SRC_URI entry with its own `SRCREV` so all
  revisions are controlled by the recipe (and fetchable from a mirror)
  rather than by whatever the bundle commit points at.
- **Build entry points** — the target recipe drives the bundle's
  `simple.mk` (`prep` / `all` / `install`) with the CMake arguments
  bitbake would normally pass, while `cfs-native-std-native` uses the
  top-level Makefile's `native_std.prep/compile/install` targets.
  `cfs-hosttools-native` configures `cfe/` directly with
  `MISSIONCONFIG=sample` and `CFE_EDS_ENABLE=OFF` and builds only the two
  host tools.

## Building your own mission

The recipes build the bundle's `sample` mission configuration
(`-DMISSIONCONFIG=sample`, i.e. `sample_defs/`). To build your own
mission, use `cfs_7.0.1.bb` as a template: fetch your mission `_defs`
tree (and any additional apps), point `MISSIONCONFIG` at it, and reuse
`setup_sgl_linux` (or the equivalent for your `_defs`) so your
`targets.cmake` selects the `sgl-linux` toolchain.

## Version coupling — read before bumping SRCREV

All components are fetched from their `dev` branches at pinned revisions.
When you bump the bundle `SRCREV` you **must** re-sync every component
`SRCREV_*` in `cfs-7.0.1.inc` to the bundle's submodule pins
(`git submodule status` in the cFS tree) — cFE, OSAL, PSP and the apps
evolve in lock-step on `dev`, and mixing revisions breaks the build or,
worse, the message/table ABI.

## Compatibility

- Yocto scarthgap (5.0). Layer depends on oe-core only.
