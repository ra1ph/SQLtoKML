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

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;


public class Main {
	
	public static String DB_NAME = "C:\\kml\\database.db";
	public static String KML_FILENAME = "C:\\kml\\database.kml";
	public static String TABLE_NAME = "database_derevnya1";
	public static String PLACEMARK_NAME = "PLace_name";
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		PrintStream out = System.out;
		
		out.println("Input database name:");
		DB_NAME = sc.nextLine();
		out.println("Input table name:");
		TABLE_NAME = sc.nextLine();
		out.println("Input out kml file name:");
		KML_FILENAME = sc.nextLine();
		
		
		Kml kml = new Kml();
		Document doc = kml.createAndSetDocument().withName("MyMarkers.kml");
		
		File dbFile = new File(DB_NAME);
		try {
			SqlJetDb db = SqlJetDb.open(dbFile, false);
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = db.getTable(TABLE_NAME);
			ISqlJetCursor cursor = table.order(table.getPrimaryKeyIndexName());
			double longit=0,latit=0;
			if(!cursor.eof()){
				do{
					if((cursor.getFloat("latitude")!=0)&&(cursor.getFloat("longitude")!=0)&&(cursor.getFloat("level")!=-1)){
					Placemark pp = doc.createAndAddPlacemark();
					pp.withName(Long.toString(cursor.getInteger("level")));
					out.println(Long.toString(cursor.getInteger("level")));

					if((longit!=0)&&(longit!=0)){
					pp.createAndSetLineString()
					.addToCoordinates(longit, latit)
					.addToCoordinates(cursor.getFloat("longitude"), cursor.getFloat("latitude"));
					}
					
					/*pp.createAndSetPoint()
					.addToCoordinates(cursor.getFloat("longitude"),cursor.getFloat("latitude"))
					.setId(Long.toString(cursor.getInteger("level")));*/
					
					longit = cursor.getFloat("longitude");
					latit = cursor.getFloat("latitude");
					}
					
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
