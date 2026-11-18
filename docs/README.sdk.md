<!--
SPDX-License-Identifier: MIT
-->

# Quick Start Guide: Building NASA cFS with a Yocto SDK

This guide walks through generating a Yocto SDK using `kas` for a qemu target
(`qemuarm64` or `qemuriscv64`), installing the SDK, and configuring NASA Core
File System (cFS) to build against the SDK toolchain.

---

## Prerequisites & Setup

### Create the Workspace Directory

Prepare a clean working directory for the `meta-aerospace` project.

```bash
mkdir -p ~/aerospace-wg
cd ~/aerospace-wg

```

### Host packages & tools (Debian/Ubuntu)

If not already done, install the [Yocto Project required host packages](https://docs.yoctoproject.org/ref-manual/system-requirements.html#required-packages-for-the-build-host) and `kas`.

```sh
sudo apt install -y gawk wget git diffstat unzip texinfo gcc build-essential \
    chrpath socat cpio python3 python3-pip python3-pexpect python3-venv \
    debianutils iputils-ping python3-git python3-jinja2 python3-subunit \
    zstd lz4 file locales libacl1 xz-utils rpcsvc-proto
```

Generate `en_US.UTF-8` locales as needed by the Yocto project.

```sh
sudo dpkg-reconfigure locales
```

Create and activate an isolated Python virtual environment to install `kas`:

```sh
python3 -m venv venv
source venv/bin/activate
pip3 install kas
```

### Clone the `meta-aerospace` Layer

Fetch the `meta-aerospace` repository which contains the Kas configuration files.

```bash
git clone https://github.com/elisa-tech/meta-aerospace.git
cd meta-aerospace

```

---

### Choose or create the target configuration file

The directory `kas/` contains the existing target configurations. If your planned target isn't available,
copy an existing Kas configuration file to create a new target build configuration.

As an example:

```bash
cd kas
cp cfs-sgl-qemuriscv64.yml cfs-sgl-beaglev-fire.yml
vim cfs-sgl-beaglev-fire.yml
cd ~/aerospace-wg

```
*Modify `cfs-sgl-beaglev-fire.yml` as needed for the RISC-V 64 architecture parameters.*


---

## Yocto Image & SDK Build

### Build the Minimal Image

Use Kas to checkout the specified configuration layer and drop into the
OpenEmbedded build environment.  This will put you in the build directory with
`$BUILDDIR` set so you can run `bitbake` commands.

```bash
cd ~/aerospace-wg/
kas checkout meta-aerospace/kas/cfs-sgl-qemuriscv64.yml
kas shell meta-aerospace/kas/cfs-sgl-qemuriscv64.yml
```

Compile the baseline Yocto image for the RISC-V target.

```bash
# check
bitbake --dry-run core-image-minimal

# run build
bitbake core-image-minimal

```

This produces the image for the target in the build/tmp-glibc/deploy/images/qemuriscv64/.

### Generate the Cross-Development SDK

The `populate_sdk` task creates a self-extracting SDK installer script. The script
contains the cross toolchain together with a sysroot that matches the image you
just built.

```bash
bitbake --dry-run core-image-minimal -c populate_sdk
bitbake core-image-minimal -c populate_sdk

```

The installer is written to `~/aerospace-wg/build/tmp-glibc/deploy/sdk/`.

---

## SDK Installation

### Install the Target SDK

Leave the kas shell (`exit`) or open a new shell. You can also copy the
installer to another machine and install the SDK there; that machine does not
need any of the Yocto build tools.

Execute the self-extracting SDK installer script to install into your designated target directory (here: ~/sgl-sdk).

```bash
cd ~/aerospace-wg/

# Check if target files are present:
ls ~/aerospace-wg/build/tmp-glibc/deploy/sdk/

# Do the actual action:
~/aerospace-wg/build/tmp-glibc/deploy/sdk/sgl-glibc-x86_64-core-image-minimal-riscv64-qemuriscv64-toolchain-0.1.sh -d ~/sgl-sdk -y

```

---

## NASA cFS Setup & Toolchain Configuration

### Prepare the cFS Workspace

Starting from here, these commands should run in a new shell, as these are based on the SDK just created.
This is the preferred way!

Create a separate workspace directory to clone and compile NASA cFS.

```bash
mkdir -p ~/sgl-cfs-workspace
cd ~/sgl-cfs-workspace

```

> **Important Note:** Do **not** manually `source` the SDK `environment-setup-*` script (e.g., `~/sgl-sdk/environment-setup-riscv64-sgl-linux`). The cFS CMake configuration manages paths directly using the settings configured in the next steps.

### 11. Clone NASA cFS and Submodules

Retrieve the NASA cFS core repository and initialize all required submodules.

```bash
git clone https://github.com/nasa/cFS.git
cd cFS
git submodule init
git submodule update
```

### 12. Configure Architecture and Target Toolchain Settings

Update `target-configs.mk` in `~/sgl-cfs-workspace/cFS/` and create custom
CMake definitions matching your Yocto SDK properties.

1. **Set target architecture** in `target-configs.mk`:

| Variable | Value |
| --- | --- |
| `ARCH_qemu_yocto_linux` | `riscv64-sgl-linux` |

1.a. **Set system architecture** in `sample_defs/targets.cmake`:

| Variable | Value | complete line |
| --- | --- | --- |
| `cpu1_SYSTEM` | `riscv64-sgl-linux` | `SET(cpu1_SYSTEM riscv64-sgl-linux)`  |
| `cpu2_SYSTEM` | `riscv64-sgl-linux` | `SET(cpu2_SYSTEM riscv64-sgl-linux)`  |



2. **Create custom toolchain definition:**

```bash
cp sample_defs/toolchain-riscv64-poky-linux.cmake sample_defs/toolchain-riscv64-sgl-linux.cmake

```


3. **Update `sample_defs/toolchain-riscv64-sgl-linux.cmake` with the following variables:**

| Variable | Value |
| --- | --- |
| `POKY_SYSROOT_ID` | `riscv64-sgl-linux` |
| `TOOLCHAIN_TOP` | `~/sgl-sdk` *(Absolute path to installed SDK)* |
| `SDKHOSTBINDIR` | `${TOOLCHAIN_TOP}/sysroots/x86_64-sglsdk-linux/usr/bin/${CMAKE_SYSTEM_PROCESSOR}-sgl-linux` |
| `TARGETPREFIX` | `${CMAKE_SYSTEM_PROCESSOR}-sgl-linux-` |


4. **Copy OS configuration file:**

```bash
cp ./sample_defs/riscv64-poky-linux_osconfig.cmake ./sample_defs/riscv64-sgl-linux_osconfig.cmake

```

---

## Building and Verification

### 13. Prepare, Compile, and Install cFS

In case you're not building on the same machine as the SDK where created on,
install necessary packages:

```bash
sudo apt install make cmake

```

Run the native cFS make sequence targets configured for the Yocto Linux target.

```bash
make qemu_yocto_linux.prep
make qemu_yocto_linux.compile
make qemu_yocto_linux.install

```

Notes:
- You don't have to compile separately. Install calls compile.
- You can compile (or install) with `-j$(nproc)`. It should be faster.

### 14. Verify Compiled Executables

Inspect the target binaries using the `file` utility to verify correct ELF architecture header flags.

```bash
file build-qemu_yocto_linux/exe/cpu*/core-cpu*
file build-qemu_yocto_linux/exe/host/*

```

### 15. Verify principal Functionality

Using the native and target sysroots in the SDK, the target binaries can be run:

```bash
# set the environment
source ~/sgl-sdk/environment-setup-riscv64-sgl-linux

# change into the exe/cpu1 directory
cd ~/sgl-cfs-workspace/cFS/build-qemu_yocto_linux/exe/cpu1/

# run the core-cpu1

sudo "$OECORE_NATIVE_SYSROOT/usr/bin/qemu-riscv64" -L "$OECORE_TARGET_SYSROOT"  ./core-cpu1
```

Open a second shell to send a NOOP command like this:

Change to the folder `build-qemu_yocto_linux/exe/host/`, run

```bash
# change directory
cd ~/sgl-cfs-workspace/cFS/build-qemu_yocto_linux/exe/host/

# Send a command
./cmd_send --pktid=0x1882 --pktfc=0

```

Expected output should include SAMPLE: NOOP command, similar to:

```text
EVS Port1 1980-012-14:08:44.50306 66/1/SAMPLE_APP 3: SAMPLE: NOOP command v<cFS version>
```
