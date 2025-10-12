#!/bin/bash
USER=postgres
DB=ecommercegt

#Limpiar la bd
echo "Limpiando base de datos"
psql -U $USER -d postgres -c "DROP DATABASE IF EXISTS $DB;"
psql -U $USER -d postgres -c "CREATE DATABASE $DB;"
echo "BD eliminada y creada de nuevo"

echo ""
echo ""

#Crear tablass
echo "Creando tablas"
psql -U $USER -d $DB -f DDL.sql
echo "Tablas creadas :D"

echo ""
echo ""

echo "Insertando en BD" 
psql -U $USER -d $DB -f DML.sql 
echo "Inserciones terminadas :3"

echo ""
echo ""