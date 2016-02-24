# ODS Search Appliance [![Build Status](https://travis-ci.org/rm-hull/ods-search-appliance.svg?branch=master)](http://travis-ci.org/rm-hull/ods-search-appliance) [![Coverage Status](https://coveralls.io/repos/github/rm-hull/ods-search-appliance/badge.svg?branch=master)](https://coveralls.io/github/rm-hull/ods-search-appliance?branch=master) [![Dependencies Status](http://jarkeeper.com/rm-hull/ods-search-appliance/status.svg)](http://jarkeeper.com/rm-hull/ods-search-appliance)

A JSON full-text search endpoint API onto NHS ODS data.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Setup / Install](#setup--install)
  - [Docker image](#docker-image)
- [Usage](#usage)
  - [Query Parameters](#query-parameters)
  - [Example API call](#example-api-call)
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

## Usage

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

### Query Parameters

| param  | required  | description                                        |
|--------|-----------|----------------------------------------------------|
| q      | mandatory | search query                                       |
| size   | optional  | the number of results to return (default 20)       |
| offset | optional  | offset in the result-set to start from (default 0) |

### Example API call

Running:

    curl http://localhost:3000/search?q=rotherham%20AND%20NOT%20doncaster\&size=10 | json_pp

gives

```json
{
   "query" : "rotherham AND NOT doncaster",
   "attribution" : [
      {
         "url" : "https://github.com/rm-hull/ods-search-application",
         "title" : "ODS Search Appliance (c) Richard Hull 2016",
         "description" : "In-memory trigram inverted-indexes on HSCIC ODS data.",
         "license" : "MIT"
      },
      {
         "description" : "ODS data is published under the Open Government Licence (OGL) and is openly available to everyone to use.",
         "license" : "Open Government License",
         "url" : "http://www.nationalarchives.gov.uk/doc/open-government-licence/version/2/",
         "title" : "Organisation Data Service, Health and Social Care Information Centre, licenced under the Open Government Licence v2.0"
      }
   ],
   "results" : {
      "showing" : {
         "to" : 9,
         "from" : 0
      },
      "total-count" : 217,
      "data" : [
         {
            "source" : [
               "enonnhs",
               7895
            ],
            "address-line-5" : "SOUTH YORKSHIRE",
            "high-level-health-geography" : "Q72",
            "address-line-1" : "1-6 CRANWORTH CLOSE",
            "amended-record-indicator" : true,
            "organisation-sub-type-code" : "R",
            "national-grouping" : "Y54",
            "name" : "CRANWORTH CLOSE",
            "postcode" : "S65 1LB",
            "address-line-4" : "ROTHERHAM",
            "organisation-code" : "8FV07",
            "open-date" : "1998-04-01"
         },
         {
            "source" : [
               "etrust",
               21852
            ],
            "address-line-5" : "SOUTH YORKSHIRE",
            "high-level-health-geography" : "Q72",
            "amended-record-indicator" : true,
            "address-line-1" : "1-6 CRANWORTH CLOSE",
            "national-grouping" : "Y54",
            "name" : "ROTHERHAM LEARNING DISABILITIES HOME 1",
            "gor-code" : "D",
            "organisation-code" : "RXEC1",
            "postcode" : "S65 1LB",
            "open-date" : "2010-10-01",
            "address-line-4" : "ROTHERHAM"
         },
         {
            "organisation-code" : "8A003",
            "postcode" : "S63 0SN",
            "address-line-4" : "ROTHERHAM",
            "open-date" : "1996-04-01",
            "national-grouping" : "Y54",
            "name" : "THE GROVE NH",
            "high-level-health-geography" : "Q72",
            "amended-record-indicator" : true,
            "organisation-sub-type-code" : "R",
            "address-line-1" : "THURNSCOE BRIDGE LANE",
            "source" : [
               "enonnhs",
               1
            ],
            "address-line-5" : "SOUTH YORKSHIRE",
            "address-line-2" : "THURNSCOE"
         },
         {
            "postcode" : "S63 0LT",
            "organisation-code" : "02PCC",
            "open-date" : "2013-10-01",
            "address-line-4" : "ROTHERHAM",
            "name" : "THURNSCOE LIFT PREMISES",
            "national-grouping" : "Y54",
            "amended-record-indicator" : true,
            "address-line-1" : "JOHN STREET",
            "high-level-health-geography" : "Q72",
            "address-line-5" : "SOUTH YORKSHIRE",
            "address-line-2" : "THURNSCOE",
            "join-provider-date" : "2013-10-01",
            "source" : [
               "eccgsite",
               623
            ]
         },
         {
            "address-line-5" : "SOUTH YORKSHIRE",
            "source" : [
               "etrust",
               4962
            ],
            "amended-record-indicator" : true,
            "address-line-1" : "16 BRIDGEGATE",
            "high-level-health-geography" : "Q72",
            "name" : "ROTHERHAM STOP SMOKING SERVICE",
            "national-grouping" : "Y54",
            "gor-code" : "D",
            "organisation-code" : "RFRRG",
            "postcode" : "S60 1PQ",
            "address-line-4" : "ROTHERHAM",
            "open-date" : "2011-04-01"
         },
         {
            "gor-code" : "D",
            "postcode" : "S61 1EA",
            "organisation-code" : "RFRFC",
            "open-date" : "2005-04-01",
            "address-line-4" : "ROTHERHAM",
            "national-grouping" : "Y54",
            "name" : "FERHAM CLINIC",
            "amended-record-indicator" : true,
            "address-line-1" : "FERHAM ROAD",
            "high-level-health-geography" : "Q72",
            "address-line-5" : "SOUTH YORKSHIRE",
            "source" : [
               "etrust",
               4946
            ]
         },
         {
            "gor-code" : "D",
            "postcode" : "S60 2UD",
            "open-date" : "2010-10-01",
            "organisation-code" : "RFR21",
            "address-line-4" : "ROTHERHAM",
            "national-grouping" : "Y54",
            "name" : "ORAL/MAXILLO-FACIAL",
            "address-line-3" : "OAKWOOD",
            "high-level-health-geography" : "Q72",
            "amended-record-indicator" : true,
            "address-line-1" : "ROTHERHAM GENERAL HOSPITAL",
            "source" : [
               "etrust",
               4904
            ],
            "address-line-2" : "MOORGATE ROAD",
            "address-line-5" : "SOUTH YORKSHIRE"
         },
         {
            "address-line-4" : "ROTHERHAM",
            "postcode" : "S63 7RF",
            "name" : "WATH HEALTH CENTRE",
            "national-grouping" : "Y54",
            "address-line-3" : "WATH-UPON-DEARNE",
            "high-level-health-geography" : "Q51",
            "amended-record-indicator" : true,
            "left-provider-date" : "2014-06-30",
            "source" : [
               "epraccur",
               1883
            ],
            "contact-telephone-number" : "01709 873233",
            "organisation-code" : "C87019",
            "open-date" : "1974-04-01",
            "gor-code" : "03L",
            "address-line-1" : "WATH HEALTH CENTRE",
            "prescribing-setting" : "0",
            "organisation-sub-type-code" : "B",
            "status-code" : "C",
            "close-date" : "2014-06-30",
            "join-provider-date" : "2013-04-01",
            "address-line-2" : "35 CHURCH STREET"
         },
         {
            "postcode" : "S60 2UD",
            "organisation-code" : "RFR35",
            "address-line-4" : "ROTHERHAM",
            "open-date" : "2015-04-01",
            "gor-code" : "D",
            "address-line-3" : "MOORGATE ROAD",
            "name" : "OSTEOPOROSIS SERVICE",
            "national-grouping" : "Y54",
            "address-line-1" : "INTEGRATED MEDICINE",
            "amended-record-indicator" : true,
            "high-level-health-geography" : "Q72",
            "address-line-5" : "SOUTH YORKSHIRE",
            "address-line-2" : "THE ROTHERHAM NHS FOUNDATION TRUST",
            "source" : [
               "etrust",
               4918
            ]
         },
         {
            "address-line-5" : "SOUTH YORKSHIRE",
            "address-line-2" : "MOORHEAD WAY",
            "source" : [
               "eccg",
               60
            ],
            "address-line-1" : "OAK HOUSE",
            "amended-record-indicator" : true,
            "organisation-sub-type-code" : "C",
            "high-level-health-geography" : "Q72",
            "address-line-3" : "BRAMLEY",
            "name" : "NHS ROTHERHAM CCG",
            "national-grouping" : "Y54",
            "postcode" : "S66 1YY",
            "open-date" : "2013-04-01",
            "organisation-code" : "03L",
            "address-line-4" : "ROTHERHAM"
         }
      ]
   }
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
