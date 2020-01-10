{ nixpkgs ? import <nixpkgs> {  } }:

let
  pkgs = [
    /* Core runtime libraries */
    nixpkgs.nodejs
    nixpkgs.yarn
    nixpkgs.clojure
    nixpkgs.jdk11
    nixpkgs.leiningen

    /* Dev Tools */
    nixpkgs.ack
    nixpkgs.git
    nixpkgs.gnupg
    nixpkgs.which
  ];

in
  nixpkgs.stdenv.mkDerivation {
    name = "env";
    buildInputs = pkgs;
  }
