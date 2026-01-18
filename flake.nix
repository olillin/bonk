{
  description = "Bonk development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-25.11";
  };

  outputs = {
    self,
    nixpkgs,
  }: let
    inherit (nixpkgs) lib;
    forAllSystems = lib.genAttrs lib.systems.flakeExposed;
  in {
    devShells = forAllSystems (
      system: let
        pkgs = import nixpkgs {inherit system;};
      in {
        default = pkgs.mkShell {
          packages = with pkgs; [
            jdk21
          ];

          # Ensure Gradle + Loom see Java 21
          JAVA_HOME = pkgs.jdk21.home;

          shellHook = ''
            export NIXSHELL="$NIXSHELL+bonk"

            # Publish Ledger to maven local
            ./publishLedger.sh
          '';
        };
      }
    );
  };
}
