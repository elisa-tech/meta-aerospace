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

## Prerequisites

### Host packages (Ubuntu/Debian)

Install the [Yocto Project required host packages](https://docs.yoctoproject.org/ref-manual/system-requirements.html#required-packages-for-the-build-host)
plus `kas`:

```sh
sudo apt install -y gawk wget git diffstat unzip texinfo gcc build-essential \
    chrpath socat cpio python3 python3-pip python3-pexpect xz-utils \
    debianutils iputils-ping python3-git python3-jinja2 python3-subunit \
    zstd liblz4-tool file locales libacl1
sudo locale-gen en_US.UTF-8

pip3 install kas
```

### Disk space and build time

A full build (from source, no sstate cache) requires approximately:
- **17 GB** disk space (9 GB build artifacts, 5.5 GB download cache, 1.5 GB sstate cache, 0.5 GB layer sources)
- **30–90 minutes** on a modern multi-core machine (first build)
- Subsequent builds with sstate cache hit complete in under a minute

## Quick start (with kas, on ELISA Space Grade Linux)

```sh
git clone <this repo> layers/meta-aerospace
kas build layers/meta-aerospace/kas/cfs-sgl-qemuarm64.yml
```

This builds `core-image-minimal` for `qemuarm64` with the `sample`
mission installed; the mission tree lives under `/exe/cpu1`
(`core-cpu1` executable, apps and tables in `cf/`).

### What `kas build` does

The kas configuration (`kas/cfs-sgl-qemuarm64.yml`) automatically:

1. Clones all required layers (openembedded-core, meta-openembedded, meta-clang, meta-sgl) into `layers/`
2. Checks out bitbake at the correct scarthgap-compatible version
3. Configures the build for the `sgl` distro (Space Grade Linux) with clang toolchain
4. Sets `MACHINE=qemuarm64` and adds `cfs` to `IMAGE_INSTALL`
5. Runs `bitbake core-image-minimal`

No manual `source oe-init-build-env` or `bblayers.conf` editing is needed.

## Running the image

After a successful build, the image is in `build/tmp-glibc/deploy/images/qemuarm64/`.

### With `kas shell` + `runqemu`

```sh
kas shell layers/meta-aerospace/kas/cfs-sgl-qemuarm64.yml -c "runqemu nographic"
```

### Manually with qemu-system-aarch64

```sh
qemu-system-aarch64 \
    -machine virt -cpu cortex-a57 -smp 4 -m 256 \
    -kernel build/tmp-glibc/deploy/images/qemuarm64/Image \
    -drive id=disk0,file=build/tmp-glibc/deploy/images/qemuarm64/core-image-minimal-qemuarm64.rootfs.ext4,if=none,format=raw \
    -device virtio-blk-pci,drive=disk0 \
    -append "root=/dev/vda console=ttyAMA0" \
    -nographic
```

### On the target

```sh
cd /exe/cpu1 && ./core-cpu1
```

You should see cFE startup messages including:

```
CFE_PSP: Starting the cFE with a POWER ON reset.
...
cFE ES Initialized: CFE_ES v7.0.1+dev1 (Draco)
...
Sample App Initialized.Sample App v7.0.0+dev1 (Draco)
...
CFE_ES_Main: CFE_ES_Main entering OPERATIONAL state
```

All bundled apps (SCH_LAB, CI_LAB, TO_LAB, SAMPLE_APP, LC, CF, DS, FM,
HK, HS, MM, SC, MD, CS) will load and initialize. Press Ctrl-C to stop.

### Sending commands to sample app

The image includes `cmdUtil`, a command-line UDP tool for sending cFS
commands to running apps. To exercise the sample app:

1. Start cFS as a background process:
   ```sh
   cd /exe/cpu1
   ./core-cpu1 > /tmp/cfs.log 2>&1 &
   ```

2. Verify cFS started (Ctrl-C to stop tailing):
   ```sh
   tail -f /tmp/cfs.log
   ```

3. Send a NO-OP command to Sample App:
   ```sh
   ./cmdUtil --host=localhost --port=1234 --pktid=0x1882 --cmdcode=0
   ```

4. Confirm the command was received:
   ```sh
   tail /tmp/cfs.log
   ```
   You should see Sample App report receiving a NO-OP command.

## Verifying the build

Check that the `cfs` package was included:

```sh
grep cfs build/tmp-glibc/deploy/images/qemuarm64/core-image-minimal-qemuarm64.rootfs.manifest
```

Expected output: `cfs cortexa57 7.*`

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
- **SGL + clang** — the kas configuration builds on top of the
  [Space Grade Linux](https://github.com/elisa-tech/meta-sgl) distro
  with the [meta-clang](https://github.com/kraj/meta-clang) toolchain.
  This is the default; for a vanilla OE/poky build, you would need a
  custom kas config or manual layer setup.

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

## Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| `No bb files in default matched BBFILE_PATTERN_meta-sgl-core` warning | meta-sgl-core layer provides only distro config, no recipes | Harmless — ignore |
| `do_fetch` fails on a git repo | Network issue or GitHub rate limit | Retry, or set `BB_NUMBER_THREADS = "1"` temporarily |
| `elf2cfetbl: Exec format error` | Host tool built for wrong architecture | Ensure `cfs-hosttools-native` is in DEPENDS (already done in the recipe) |
| Build uses excessive disk | `rm_work` is enabled by default via SGL; downloads are the main consumer | Set `DL_DIR` to a shared location in `local.conf` |

## Compatibility

- Yocto scarthgap (5.0). Layer depends on oe-core only.
- Tested on: Ubuntu 22.04 (x86_64) build host, `qemuarm64` target.
