#!/bin/sh

port=3000

set -x

curl -X DELETE localhost:$port/store -w "\n"

curl -X GET localhost:$port/store/data/foo/average -w "\n"
curl -X GET localhost:$port/store/average_of_averages -w "\n"

curl -X POST localhost:$port/store/data/foo/1 -w "\n"
curl -X POST localhost:$port/store/data/foo/2 -w "\n"
curl -X POST localhost:$port/store/data/foo/3 -w "\n"
curl -X POST localhost:$port/store/data/foo/4 -w "\n"

curl -X GET localhost:$port/store/data/foo/average -w "\n"

curl -X POST localhost:$port/store/data/baz/20 -w "\n"
curl -X POST localhost:$port/store/data/baz/40 -w "\n"
curl -X POST localhost:$port/store/data/baz/0 -w "\n"
curl -X POST localhost:$port/store/data/baz/1 -w "\n"

curl -X GET localhost:$port/store/data/baz/average -w "\n"

curl -X GET localhost:$port/store/average_of_averages -w "\n"
