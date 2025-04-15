#!/bin/bash
sbt clean scalafmt test:scalafmt coverage it/Test/scalafmt it/test test coverageReport