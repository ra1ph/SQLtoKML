package com.ra1ph.sqltokml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Scanner;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;


public class Main {
	
	public static String DB_NAME = "C:\\kml\\database.db";
	public static String KML_FILENAME = "C:\\kml\\database.kml";
	public static String TABLE_NAME = "database_univertest2";
	public static String PLACEMARK_NAME = "PLace_name";
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		PrintStream out = System.out;
		
		Kml kml = new Kml();
		Placemark pp = kml.createAndSetPlacemark();
		pp.withName(PLACEMARK_NAME);
		
		File dbFile = new File(DB_NAME);
		try {
			SqlJetDb db = SqlJetDb.open(dbFile, false);
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = db.getTable(TABLE_NAME);
			ISqlJetCursor cursor = table.order(table.getPrimaryKeyIndexName());
			if(!cursor.eof()){
				do{
					out.println(Long.toString(cursor.getInteger("level")));

					pp.createAndSetPoint()
					.addToCoordinates(cursor.getFloat("latitude"),cursor.getFloat("longitude"))
					.setId(Long.toString(cursor.getInteger("_id")));
					
				}while(cursor.next());
			}

			kml.marshal(new File(KML_FILENAME));
			
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
