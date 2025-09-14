#!/bin/bash
USER=postgres
DB=fitmanager

psql -U $USER -d postgres -c "DROP DATABASE IF EXISTS $DB;"
psql -U $USER -d postgres -c "CREATE DATABASE $DB;"
psql -U $USER -d $DB -f Constructor.sql
psql -U $USER -d $DB -f Inserciones.sql