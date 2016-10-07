# ODS Search Appliance 
[![Build Status](https://travis-ci.org/rm-hull/ods-search-appliance.svg?branch=master)](http://travis-ci.org/rm-hull/ods-search-appliance) 
[![Coverage Status](https://coveralls.io/repos/github/rm-hull/ods-search-appliance/badge.svg?branch=master)](https://coveralls.io/github/rm-hull/ods-search-appliance?branch=master) 
[![Dependencies Status](http://jarkeeper.com/rm-hull/ods-search-appliance/status.svg)](http://jarkeeper.com/rm-hull/ods-search-appliance)
[![Docker Pulls](https://img.shields.io/docker/pulls/richardhull/ods-search-appliance.svg?maxAge=2592000)](https://hub.docker.com/r/richardhull/ods-search-appliance/)

A JSON full-text search endpoint API onto NHS ODS data.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Setup / Install](#setup--install)
  - [Docker image](#docker-image)
- [Usage](#usage)
  - [Keyword Text Search](#keyword-text-search)
    - [Query Parameters](#query-parameters)
    - [Example API call](#example-api-call)
  - [By Organisation Code](#by-organisation-code)
    - [Example API call](#example-api-call-1)
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
    $ java -jar target/ods-search-appliance-0.1.0-standalone.jar

In both instances, the webapp starts on http://localhost:3000

### Docker image

A docker image is available as [richardhull/ods-search-appliance](https://hub.docker.com/r/richardhull/ods-search-appliance/), and can be downloaded and started with:

    $ docker pull richardhull/ods-search-appliance
    $ docker run --name ods-search-appliance -d -p 3000:3000 richardhull/ods-search-appliance

## Usage

### Keyword Text Search

Once running, the server will respond to a HTTP request of `GET /search?q=...`,
where the query term is defined by the following BNF grammar:

    searchTerm ::= [NOT] ( singleWord | quotedString | '(' searchExpr ')' )
    searchAnd ::= searchTerm [ AND searchTerm ]...
    searchExpr ::= searchAnd [ OR searchAnd ]...

Conjunctions (AND, OR & NOT) are case sensitive, and must be supplied in
upper-case. Query terms should always be specified in lower case, and
are not case sensitive. Query terms will always match partial words.

For example, the following are valid queries:

* sheffield
* leeds OR york
* "south leeds"
* south leeds
* rotherham AND NOT doncaster
* (leeds OR bradford) hospital
* NOT (carlisle penrith)

Negation can be quite slow on large sets.

The HTTP response type is `application/json`.

#### Query Parameters

| param  | required  | description                                        |
|--------|-----------|----------------------------------------------------|
| q      | mandatory | search query                                       |
| size   | optional  | the number of results to return (default 20)       |
| offset | optional  | offset in the result-set to start from (default 0) |

#### Example API call

Running:

    $ curl -s http://localhost:3000/search?q=rotherham%20AND%20NOT%20doncaster\&size=10 | jq '.'

gives

```json
{
  "query": "rotherham AND NOT doncaster",
  "results": {
    "total-count": 217,
    "showing": {
      "from": 0,
      "to": 9
    },
    "data": [
      {
        "address-line-1": "POPLAR GLADE",
        "address-line-2": "WICKERSLEY",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "gor-code": "D",
        "high-level-health-geography": "Q72",
        "name": "WICKERSLEY HEALTH CENTRE",
        "national-grouping": "Y54",
        "open-date": "2011-04-01",
        "organisation-code": "RFRPW",
        "postcode": "S66 2JQ",
        "source": [
          "etrust",
          5120
        ]
      },
      {
        "address-line-1": "THE VILLAGE SURGERY",
        "address-line-2": "24-28 LAUGHTON ROAD",
        "address-line-3": "THURCROFT",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "gor-code": "D",
        "high-level-health-geography": "Q72",
        "name": "THURCROFT CLINIC",
        "national-grouping": "Y54",
        "open-date": "2011-04-01",
        "organisation-code": "RFRTC",
        "postcode": "S66 9LP",
        "source": [
          "etrust",
          5140
        ]
      },
      {
        "address-line-1": "ROTHERHAM GENERAL HOSPITAL",
        "address-line-2": "MOORGATE ROAD",
        "address-line-3": "OAKWOOD",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "gor-code": "D",
        "high-level-health-geography": "Q72",
        "name": "ORAL/MAXILLO-FACIAL",
        "national-grouping": "Y54",
        "open-date": "2010-10-01",
        "organisation-code": "RFR21",
        "postcode": "S60 2UD",
        "source": [
          "etrust",
          5069
        ]
      },
      {
        "address-line-1": "1-6 CRANWORTH CLOSE",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "high-level-health-geography": "Q72",
        "name": "CRANWORTH CLOSE",
        "national-grouping": "Y54",
        "open-date": "1998-04-01",
        "organisation-code": "8FV07",
        "organisation-sub-type-code": "R",
        "postcode": "S65 1LB",
        "source": [
          "enonnhs",
          7895
        ]
      },
      {
        "address-line-1": "THOROGATE",
        "address-line-2": "RAWMARSH",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "gor-code": "D",
        "high-level-health-geography": "Q72",
        "name": "MONKWOOD CLINIC",
        "national-grouping": "Y54",
        "open-date": "2011-04-01",
        "organisation-code": "RFRRL",
        "postcode": "S62 7HU",
        "source": [
          "etrust",
          5131
        ]
      },
      {
        "address-line-1": "ROTHERHAM GENERAL HOSPITAL",
        "address-line-2": "MOORGATE ROAD",
        "address-line-3": "OAKWOOD",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "gor-code": "D",
        "high-level-health-geography": "Q72",
        "name": "MUSCULAR SKELETAL INTERFACE",
        "national-grouping": "Y54",
        "open-date": "2010-10-01",
        "organisation-code": "RFR27",
        "postcode": "S60 2UD",
        "source": [
          "etrust",
          5075
        ]
      },
      {
        "address-line-1": "THURNSCOE BRIDGE LANE",
        "address-line-2": "THURNSCOE",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "high-level-health-geography": "Q72",
        "name": "THE GROVE NH",
        "national-grouping": "Y54",
        "open-date": "1996-04-01",
        "organisation-code": "8A003",
        "organisation-sub-type-code": "R",
        "postcode": "S63 0SN",
        "source": [
          "enonnhs",
          1
        ]
      },
      {
        "address-line-1": "BRINSWORTH LANE",
        "address-line-2": "BRINSWORTH",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "gor-code": "D",
        "high-level-health-geography": "Q72",
        "name": "BRINSWORTH CLINIC",
        "national-grouping": "Y54",
        "open-date": "2011-04-01",
        "organisation-code": "RFRRE",
        "postcode": "S60 5BX",
        "source": [
          "etrust",
          5125
        ]
      },
      {
        "address-line-1": "PO BOX 741",
        "address-line-3": "ROTHERHAM",
        "amended-record-indicator": true,
        "contact-telephone-number": "0333 2001726",
        "gor-code": "03N",
        "high-level-health-geography": "Q72",
        "join-provider-date": "2013-04-01",
        "name": "SHEFFIELD VP SCHEME (3)",
        "national-grouping": "Y54",
        "open-date": "2009-09-22",
        "organisation-code": "Y02876",
        "organisation-sub-type-code": "B",
        "postcode": "S60 9HE",
        "prescribing-setting": "0",
        "source": [
          "epraccur",
          10676
        ],
        "status-code": "A"
      },
      {
        "address-line-1": "NINE TREES",
        "address-line-2": "NINE TREES TRADING ESTATE",
        "address-line-3": "MORTHEN ROAD",
        "address-line-4": "ROTHERHAM",
        "address-line-5": "SOUTH YORKSHIRE",
        "amended-record-indicator": true,
        "gor-code": "D",
        "high-level-health-geography": "Q72",
        "name": "ROTHERHAM IAPT",
        "national-grouping": "Y54",
        "open-date": "2014-10-01",
        "organisation-code": "RXEDN",
        "postcode": "S66 9JG",
        "source": [
          "etrust",
          22206
        ]
      }
    ]
  },
  "attribution": [
    {
      "license": "MIT",
      "title": "ODS Search Appliance (c) Richard Hull 2016",
      "description": "In-memory trigram inverted-indexes on HSCIC ODS data.",
      "url": "https://github.com/rm-hull/ods-search-application"
    },
    {
      "license": "Open Government License",
      "title": "Organisation Data Service, Health and Social Care Information Centre, licenced under the Open Government Licence v2.0",
      "description": "ODS data is published under the Open Government Licence (OGL) and is openly available to everyone to use.",
      "url": "http://www.nationalarchives.gov.uk/doc/open-government-licence/version/2/"
    }
  ]
}
```

### By Organisation Code

Single records can be retrieved if the org-code is already known: the server
will repsond to  HTTP request of `GET /organisation-code/<ORG_CODE>`

#### Example API call

Running:

    $ curl -s http://localhost:3000/organisation-code/R1A | jq .

gives:

```json
{
  "query": "R1A",
  "data": {
    "address-line-1": "ISAAC MADDOX HOUSE",
    "address-line-2": "SHRUB HILL INDUSTRIAL ESTATE",
    "address-line-4": "WORCESTER",
    "address-line-5": "WORCESTERSHIRE",
    "amended-record-indicator": true,
    "gor-code": "F",
    "high-level-health-geography": "Q77",
    "name": "WORCESTERSHIRE HEALTH AND CARE NHS TRUST",
    "national-grouping": "Y55",
    "open-date": "2011-07-01",
    "organisation-code": "R1A",
    "postcode": "WR4 9RW",
    "source": [
      "etr",
      0
    ]
  },
  "attribution": [
    {
      "license": "MIT",
      "title": "ODS Search Appliance (c) Richard Hull 2016",
      "description": "In-memory trigram inverted-indexes on HSCIC ODS data.",
      "url": "https://github.com/rm-hull/ods-search-application"
    },
    {
      "license": "Open Government License",
      "title": "Organisation Data Service, Health and Social Care Information Centre, licenced under the Open Government Licence v2.0",
      "description": "ODS data is published under the Open Government Licence (OGL) and is openly available to everyone to use.",
      "url": "http://www.nationalarchives.gov.uk/doc/open-government-licence/version/2/"
    }
  ]
}
```

## TODO

* Web search form & table of results
* ~~Parser-combinators for expression language~~
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
