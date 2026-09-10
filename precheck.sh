#!/bin/bash
sbt clean scalafmt scalafmtSbt Test/scalafmt it/Test/scalafmt coverage test it/test coverageReport