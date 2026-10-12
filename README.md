# Sactake Bag Wheel

Client-side Fabric mod for Minecraft Java Edition **1.21.11**. The goal is a bright pastel radial menu for selecting items stored in vanilla bundles.

## Build

The GitHub Actions workflow builds the project on pushes, pull requests, and manual dispatches. Download the `sactake-bag-wheel-...` artifact from the successful workflow run.

## Controls and design

- Default key: **G**
- Bright pastel pink, peach, yellow, mint, sky-blue, and lavender UI
- Uses Minecraft's vanilla bundle item; no custom bag item is added
- Client-only mod; Java 21

## Current milestone

The repository now contains the Fabric/Loom build setup, mod metadata, automated GitHub Actions build, and an initial radial bundle-selection UI. Contents browsing and safe server-synchronized use of food/potions from inside bundles are not implemented yet; the initial UI should not be treated as a complete gameplay release.
