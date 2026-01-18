# Contributing

Thank you for your interest in contributing to Bonk!

## How to develop

Besides normal Fabric mod practices, this mod also has a soft dependency on
[Ledger](https://www.quiltservertools.net/Ledger/latest/). Per [the Ledger documentation](https://www.quiltservertools.net/Ledger/latest/api/extension_development/)
it must be built and pushed to mavenLocal. This can be done with the provided
`publishLedger` script (Linux only):

```console
./publishLedger.sh
```

This will be done automatically if using the [Nix](https://nixos.org/download/)
development flake.
