#!/bin/bash

./mill clean
./mill __.compile
./mill __.test
./mill __.checkFormat
./mill __.fix --check
./mill __.publishLocal
