#!/bin/bash

java -classpath tools/joda-time-2.2/joda-time-2.2.jar:tools/hsqldb_2_3_0/lib/hsqldb.jar:tools/joker/joker-0.1.0-uberjar.jar:build/libs/example.jar com.asprotunity.exchange.server.main.Main $@
