/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;

import Lib.UserData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase; 
import com.mongodb.MongoClient; 
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Iterator;
import org.bson.Document;

/**
 *
 * @author ThayLe
 */
public class AccessDatabase {
    
    public static ArrayList<UserData> userData = new ArrayList<>();
    public static void main(String[] args) {
        AccessDatabase m = new AccessDatabase();
        m.getListUserFromDB();
        m.addUserToDatabase("test", "test");
    }
    public static void getListUserFromDB() {
      MongoClient mongo = new MongoClient( "localhost" , 27017 ); 
      DB db = mongo.getDB( "StreamVideo" );
      DBCollection collection = db.getCollection("Users");
      
      DBCursor cursor = collection.find();
      DBObject dbObject;
      while(cursor.hasNext()) {
          dbObject = cursor.next();
          System.out.println(dbObject.get("_id"));
          System.out.println(dbObject.get("name"));
          System.out.println(dbObject.get("userName"));
          System.out.println(dbObject.get("userPassword"));
          userData.add(new UserData(dbObject.get("_id").toString(),
                  dbObject.get("name").toString(),
                  dbObject.get("userName").toString(), 
                  dbObject.get("userPassword").toString()));
      }
    }
    public static void addUserToDatabase(String userName,String userPassword) {
        MongoClient mongo = new MongoClient( "localhost" , 27017 ); 
        DB db = mongo.getDB( "StreamVideo" );
        DBCollection collection = db.getCollection("Users");
        BasicDBObject document = new BasicDBObject();
        document.put("userID", 1);
        document.put("name", userName);
        document.put("userName", userName);
        document.put("userPassword", userPassword);
        collection.insert(document);
    }
    public static void refreshDatabase(){
        userData.clear();
        getListUserFromDB();
    }
}
