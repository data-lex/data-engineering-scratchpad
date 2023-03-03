#!/bin/bash
set -e

## build ARGs
NCPUS=${NCPUS:--1}

# always set this for scripts but don't declare as ENV..
export DEBIAN_FRONTEND=noninteractive

# install paws - Package for Amazon Web Services
install2.r --error --skipmissing --deps TRUE --skipinstalled -n "$NCPUS" \
    paws

# Clean up
rm -rf /tmp/downloaded_packages
rm -rf /var/lib/apt/lists/*

## Strip binary installed lybraries from RSPM
## https://github.com/rocker-org/rocker-versioned2/issues/340
strip /usr/local/lib/R/site-library/*/libs/*.so
