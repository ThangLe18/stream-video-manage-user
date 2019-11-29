/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;

import Lib.UserData;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase; 
import com.mongodb.MongoClient; 
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.Iterator;
import org.bson.Document;

/**
 *
 * @author ThayLe
 */
public class AccessDatabase {
    public static void main(String[] args) {
      MongoClient mongo = new MongoClient( "localhost" , 27017 ); 
      DB db = mongo.getDB( "StreamVideo" );
      DBCollection collection = db.getCollection("Users");
      
      DBCursor cursor = collection.find();
      DBObject dbObject;
        while(cursor.hasNext()) {
           dbObject = cursor.next();
           //int marks = ((Number)dbObject.get("userID")).intValue();
            System.out.println(dbObject.get("_id"));
        }
    }
}
