#!/bin/bash

java -classpath tools/joda-time-2.2/joda-time-2.2.jar:tools/hsqldb_2_3_0/lib/hsqldb.jar:tools/Ice-3_5_0/lib/Ice.jar:out/production com.asprotunity.exchange.server.main.Main $@