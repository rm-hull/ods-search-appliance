# ODS Search Appliance [![Build Status](https://travis-ci.org/rm-hull/ods-search-appliance.svg?branch=master)](http://travis-ci.org/rm-hull/ods-search-appliance) [![Coverage Status](https://coveralls.io/repos/github/rm-hull/ods-search-appliance/badge.svg?branch=master)](https://coveralls.io/github/rm-hull/ods-search-appliance?branch=master) [![Dependencies Status](http://jarkeeper.com/rm-hull/ods-search-appliance/status.svg)](http://jarkeeper.com/rm-hull/ods-search-appliance)

A JSON full-text search endpoint API onto NHS ODS data.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Setup / Install](#setup--install)
  - [Docker image](#docker-image)
- [TODO](#todo)
- [References](#references)
- [Licenses](#licenses)
  - [OGL attribution](#ogl-attribution)
  - [The MIT License (MIT)](#the-mit-license-mit)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Setup / Install

For local development, ensure [leiningen](http://leiningen.org/#install) is installed
and available on the path.

Inside the project directory:

    $ cd ods-search-appliance
    $ lein deps
    $ lein ring server-headless

To build and run a standalone jar:

    $ lein ring uberjar
    $ java -jar target/ods-search-appliance-0.0.1-SNAPSHOT-standalone.jar

In both instances, the webapp starts on http://localhost:3000

### Docker image

A docker image is available as [richardhull/ods-search-appliance](https://hub.docker.com/r/richardhull/ods-search-appliance/), and can be downloaded and started with:

    $ docker pull richardhull/ods-search-appliance
    $ docker run --name ods-search-appliance -d -p 3000:3000 richardhull/ods-search-appliance

## TODO

* Web search form & table of results
* Parser-combinators for expression language
* Stop-lists
* Profiling / performance improvements

## References

* http://systems.hscic.gov.uk/data/ods/datadownloads
* https://swtch.com/~rsc/regexp/regexp4.html

## Licenses

### OGL attribution

This appliance makes use of publicly available ODS data from [HSCIC](http://systems.hscic.gov.uk/data/ods/guidance/responsibility),
licensed under the [Open Government License](http://www.nationalarchives.gov.uk/doc/open-government-licence/version/2/).

### The MIT License (MIT)

Copyright (c) 2016 Richard Hull

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
