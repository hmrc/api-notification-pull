#!/bin/bash

sbt clean scalastyle coverage test it:test coverageReport dependencyUpdates
