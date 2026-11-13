# Quick Start Guide: Building NASA cFS with a Yocto SDK (RISC-V 64)

This guide walks through generating a Yocto SDK using `kas` for a RISC-V 64
target (`qemuriscv64`), installing the SDK, and configuring NASA Core File
System (cFS) to build against the SDK toolchain.

---

## Prerequisites & Setup

### 1. Create the Workspace Directory

Prepare a clean working directory for the `meta-aerospace` project.

```bash
mkdir -p ~/aerospace-wg
cd ~/aerospace-wg

```

### 2. Clone the `meta-aerospace` Layer

Fetch the `meta-aerospace` repository which contains the Kas configuration files.

```bash
git clone https://github.com/elisa-tech/meta-aerospace.git
cd meta-aerospace

```

---

## Configuration & Environment Setup

### 3. Create the Target Configuration File (only for targets other than ARM, like RISC)

Copy an existing Kas configuration file to create a target build configuration for `qemuriscv64`.

```bash
cd kas
cp cfs-sgl-qemuarm64.yml cfs-sgl-qemuriscv64.yml
vim cfs-sgl-qemuriscv64.yml
cfs-sgl-qemuarm64.yml
fprime-sgl-qemuarm64.yml
cd ../..

```

*Modify `cfs-sgl-qemuriscv64.yml` as needed for the RISC-V 64 architecture parameters.*

### 4. Set Up the Python Virtual Environment

Create and activate a isolated Python virtual environment to manage build dependencies like `kas`.

```bash
python3 -m venv .venv
source .venv/bin/activate

```

### 5. Initialize the Kas Build Workspace

Prepare the Kas workspace directory and export the location for the build output.

```bash
mkdir -p cfs-sgl-qemuriscv64
export KAS_WORK_DIR=$PWD/cfs-sgl-qemuriscv64

mkdir -p cfs-sgl-qemuarm64
export KAS_WORK_DIR=$PWD/cfs-sgl-qemuarm64

```

---

## Yocto Image & SDK Build

### 6. Checkout and Shell Entry via Kas

Use Kas to checkout the specified configuration layer and drop into the OpenEmbedded build environment.

```bash
kas checkout meta-aerospace/kas/cfs-sgl-qemuriscv64.yml
kas shell meta-aerospace/kas/cfs-sgl-qemuriscv64.yml

kas checkout meta-aerospace/kas/cfs-sgl-qemuarm64.yml
kas shell meta-aerospace/kas/cfs-sgl-qemuarm64.yml

```

sudo sed -i 's/# \(en_US.UTF-8\)/\1/' /etc/locale.gen
sudo /usr/sbin/locale-gen
sudo apt-get install zstd lz4 chrpath diffstat gawk cpio

### 7. Build the Minimal Image

Compile the baseline Yocto image for the RISC-V target.

This will put you in the build directory with $BUILDDIR set so you can run bitbake -c build core-image-minimal

```bash
bitbake --dry-run core-image-minimal
bitbake core-image-minimal

```

### 8. Generate the Cross-Development SDK

Populate and export the standalone target SDK installer script.

```bash
bitbake --dry-run core-image-minimal -c populate_sdk
bitbake core-image-minimal -c populate_sdk

```

---

## SDK Installation

### 9. Install the Target SDK

Execute the self-extracting SDK installer script into your designated target directory.

```bash
# Path target example: ~/sgl-sdk
cd $BUILDDIR
# Check target files are present:
ls tmp-glibc/deploy/sdk/, check for the file present
# Do the actual action:
tmp-glibc/deploy/sdk/sgl-glibc-x86_64-core-image-minimal-riscv64-qemuriscv64-toolchain-0.1.sh -d ~/sgl-sdk -y

tmp-glibc/deploy/sdk/sgl-glibc-aarch64-core-image-minimal-cortexa57-qemuarm64-toolchain-0.1.sh -d ~/sgl-sdk -y

```

---

## NASA cFS Setup & Toolchain Configuration

### 10. Prepare the cFS Workspace

Starting from here, these commands could run in a new shell, as these are based onm the SDK just created.
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

Update `target-configs.mk` and create custom CMake definitions matching your Yocto SDK properties.

1. **Set target architecture** in `target-configs.mk`:
* Set architecture target to `ARCH_qemu_yocto_linux`
* Set tuple/variant to `riscv64-sgl-linux`

Example: For aarch64 change:
target-configs.mk


2. **Create custom toolchain definition:**
```bash
cp sample_defs/toolchain-riscv64-poky-linux.cmake sample_defs/toolchain-riscv64-sgl-linux.cmake


cp sample_defs/toolchain-aarch64-poky-linux.cmake sample_defs/toolchain-aarch64-sgl-linux.cmake
```


3. **Update `sample_defs/toolchain-riscv64-sgl-linux.cmake` with the following variables:**
| Variable | Value |
| --- | --- |
| `POKY_SYSROOT_ID` | `aarch64-sgl-linux` |
| `TOOLCHAIN_TOP` | `~/sgl-sdk` *(Absolute path to installed SDK)* |
| `SDKHOSTBINDIR` | `${TOOLCHAIN_TOP}/sysroots/aarch64-sglsdk-linux/usr/bin/${CMAKE_SYSTEM_PROCESSOR}-sgl-linux` |
| `TARGETPREFIX` | `${CMAKE_SYSTEM_PROCESSOR}-sgl-linux-` |


4. **Copy OS configuration file:**
```bash
cp ./sample_defs/riscv64-poky-linux_osconfig.cmake ./sample_defs/riscv64-sgl-linux_osconfig.cmake

cp ./sample_defs/aarch64-poky-linux_osconfig.cmake ./sample_defs/aarch64-sgl-linux_osconfig.cmake

```



---

## Building and Verification

### 13. Prepare, Compile, and Install cFS

Run the native cFS make sequence targets configured for the Yocto Linux target.

```bash
make qemu_yocto_linux.prep
make qemu_yocto_linux.compile
make qemu_yocto_linux.install

```

Notes:
- You don't have to compile separately. Install calls compile.
- You can compile (or install) with -j$(nproc). It should be faster.

### 14. Verify Compiled Executables

Inspect the target binaries using the `file` utility to verify correct ELF architecture header flags.

```bash
file build-qemu_yocto_linux/exe/cpu*/core-cpu*
file build-qemu_yocto_linux/exe/host/*

```

### 15. Verify principal Functionality

If you go into the folder build-qemu_yocto_linux/exe/cpu, run the command to see if all is working as sudo:
> sudo ./core-cpu1

Open a second shell to send a NOOOP command lie, this:
Change to the folder build-qemu_yocto_linux/exe/host/, run
> ./cmd_send --pktid=0x1882 --pktfc=0

Expected output should include SAMPLE: NOOP command, similar to:
EVS Port1 1980-012-14:08:44.50306 66/1/SAMPLE_APP 3: SAMPLE: NOOP command v7.0.0+dev1
