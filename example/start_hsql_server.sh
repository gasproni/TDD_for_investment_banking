#!/bin/bash

java -cp tools/hsqldb_2_3_0/lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:hsql/server/eventdb --dbname.0 eventdb