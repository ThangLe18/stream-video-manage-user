/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
/**
 *
 * @author ThayLe
 */
public class Test {
    public static void main(String[] args) {
        //insertData();
        findData();
    }
    
    public static void findData(){
        try {
            System.out.println("mongoDB test");
            MongoClient mongoClient = new MongoClient("localhost",27017);
            DB db = mongoClient.getDB("res");
            System.out.println("Connected to database");
            DBCollection coll = db.getCollection("lib");
            DBCursor cursor = coll.find();
            while(cursor.hasNext()){
                System.out.println(cursor.next());
            }
        } catch (Exception e) {
            System.out.println("can not connect to database");
        }
    }
    
    
    public static void insertData(){
        try {
            System.out.println("mongoDB test");
            MongoClient mongoClient = new MongoClient("localhost",27017);
            DB db = mongoClient.getDB("res");
            System.out.println("Connected to database");
            DBCollection coll = db.getCollection("lib");
            //DBCursor cursor = coll.find();
            
            BasicDBObject document = new BasicDBObject();
            document.put("name", "mkyongDB");
        
        
            coll.insert(document);
        } catch (Exception e) {
            System.out.println("can not connect to database");
        }
    }
}






















