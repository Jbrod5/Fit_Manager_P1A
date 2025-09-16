#!/bin/bash
USER=postgres
DB=fitmanager

psql -U $USER -d postgres -c "DROP DATABASE IF EXISTS $DB;"
psql -U $USER -d postgres -c "CREATE DATABASE $DB;"
psql -U $USER -d $DB -f Constructor.sql
echo "Base de datos creada :D"
echo ""
echo "Insertando en BD con python >:3" 
python3 ./inserciones.py 