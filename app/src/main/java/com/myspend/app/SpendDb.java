package com.myspend.app;
import android.content.*; import android.database.sqlite.*;
public class SpendDb extends SQLiteOpenHelper {
 public SpendDb(Context c){super(c,"myspend.db",null,1);}
 public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE tx(id INTEGER PRIMARY KEY AUTOINCREMENT, ts INTEGER, amount REAL, kind TEXT, merchant TEXT, category TEXT, source TEXT, ref TEXT UNIQUE)");}
 public void onUpgrade(SQLiteDatabase d,int a,int b){}
 public boolean add(long ts,double amt,String kind,String merchant,String cat,String src,String ref){ContentValues v=new ContentValues();v.put("ts",ts);v.put("amount",amt);v.put("kind",kind);v.put("merchant",merchant);v.put("category",cat);v.put("source",src);v.put("ref",ref);return getWritableDatabase().insertWithOnConflict("tx",null,v,SQLiteDatabase.CONFLICT_IGNORE)>0;}
 public android.database.Cursor all(){return getReadableDatabase().rawQuery("SELECT * FROM tx ORDER BY ts DESC",null);}
 public double sum(String kind){android.database.Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(amount),0) FROM tx WHERE kind=?",new String[]{kind});c.moveToFirst();double x=c.getDouble(0);c.close();return x;}
 public double sumCategory(String cat){android.database.Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(amount),0) FROM tx WHERE kind='expense' AND category=?",new String[]{cat});c.moveToFirst();double x=c.getDouble(0);c.close();return x;}
}
