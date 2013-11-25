package com.eastime.paycar.util;


import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper{

	private static DatabaseHelper mDbHelper;
	public static SQLiteDatabase mDb;
	//private static SQLiteDatabase mDh;
	private static final String DATABASE_NAME = "gps_sqlite";
	private static final int DATABASE_VERSION = 1;
	private Context mCtx;
	public static DBHelper db;
	
	public static DBHelper getInstance(){
		if(null == db){
			db = new DBHelper();
		}
		
		return db;
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//建立表结构
			String gpsCreateSql ="CREATE TABLE "+Contants.table1+" (id integer primary key autoincrement,username varchar,a1 varchar,a2 varchar,a3 varchar,result varchar,writetime varchar,number varchar)";
			String danhaoCreateSql = "CREATE TABLE "+Contants.table2+" (id integer primary key autoincrement,number varchar,imgpath varchar,succinfo varchar,failinfo varchar,isdel integer,issucc integer,isupload integer,paytime varchar,createtime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))";
			String jizhanCreateSql = "CREATE TABLE "+Contants.table3+"(id integer primary key autoincrement,username varchar,mcc varchar,mnc varchar,nid varchar,sid varchar,cid varchar,bid varchar,lac varchar,createtime varchar,number varchar)";
/*			if(!isTableExist(Contants.table1)){
				db.execSQL(gpsCreateSql);
			}
			if(!isTableExist(Contants.table2)){
				db.execSQL(danhaoCreateSql);
			}
			if(!isTableExist(Contants.table3)){
				db.execSQL(jizhanCreateSql);
			}*/
			db.execSQL(gpsCreateSql);
			db.execSQL(danhaoCreateSql);
			db.execSQL(jizhanCreateSql);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
	//封装类的构造
/*	public DBHelper(Context ctx){
		//this.mCtx = ctx;
		this.mCtx = getApplicationContext();
		
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();  //可读写
		//mDh = mDbHelper.getReadableDatabase();  //只读的
	}*/
	private DBHelper(){
		//this.mCtx = ctx;
		this.mCtx = AppContext.getInstance();
		
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();  //可读写
		//mDh = mDbHelper.getReadableDatabase();  //只读的
	}
	
	//关闭数据源
	public void closeConnection(){
		if(mDb != null && mDb.isOpen()){
			mDb.close();
		}
		if(mDbHelper != null ){
			mDbHelper.close();
		}
	}
	
	/**
	 * 插入数据
	 * @param tableName  表名
	 * @param initValues 所要插入的表值
	 * @return  返回新插入的行号 如果失败 返回-1
	 */

	public long insert(String tableName,ContentValues initValues){
		
		return mDb.insert(tableName, null, initValues);
	}
	
	/**
	 * 删除数据
	 * @param tableName 表名
	 * @param whereClause 条件
	 * @param whereArgs 条件对应的值（如果deleteCondition中有“？”号，
	 * 将用此数组中的值替换，一一对应）
	 * @return 
	 */
	//the number of rows affected if a whereClause is passed in, 0 otherwise. 
	//To remove all rows and get a count pass "1" as the whereClause.
	public boolean delete(String tableName,String whereClause,String[] whereArgs){
		return mDb.delete(tableName, whereClause, whereArgs) > 0;
	}
	
	/**
	 * 更新数据
	 * @param tableName 表名
	 * @param initialValues 要更新的列
	 * @param whereClause 更新的条件
	 * @param whereArgs 更新条件中的“？”对应的值 
	 * @return
	 */
	public boolean update(String tableName,ContentValues values,String whereClause,String[] whereArgs){
		return mDb.update(tableName, values, whereClause, whereArgs) > 0;
	}
	
	/**
	 * 查询数据库 返回一个列表
	 * @param distinct 是否去重复
	 * @param tableName 表名
	 * @param columns 要返回的列
	 * @param selection 条件
	 * @param selectionArgs 条件中“？”的参数值
	 * @param groupBy 分组
	 * @param having 分组过滤条件
	 * @param orderBy 排序
	 * @param limit 分组用的
	 * @return 
	 */
	public Cursor findList(boolean distinct, String tableName){
		return findList(distinct, tableName, null, null, null, null, null, null, null);
	}
	public Cursor findList(boolean distinct, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit){
		return mDb.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}
	
	/**
	 * 获取一条数据
	 * @param distinct 是否去重复
	 * @param tableName 表名
	 * @param columns 要返回的列
	 * @param selection 条件
	 * @param selectionArgs 条件中“？”的参数值
	 * @param groupBy 分组
	 * @param having 分组过滤条件
	 * @param orderBy 排序
	 * @param limit 分组用的
	 * @return
	 * @throws SQLException
	 */
	public Cursor findOne(boolean distinct, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) throws SQLException{
		
		Cursor mCursor = findList(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * 执行带参数的sql语句
	 * @param sql  sql语句 里面带有  ?
	 * @param args  替换 sql里面的  ？
	 */
	public void execSQL(String sql,Object[] args){
		mDb.execSQL(sql,args);
	}
	
	/**
	 * 执行sql语句
	 * @param sql
	 */
	public void execSQL(String sql){
		mDb.execSQL(sql);
	}
	
	
	/**
	 * 查询该表是否存在
	 * @param tableName
	 * @return
	 */
	public static boolean isTableExist(String tableName){
		
		boolean result = false;
		
		if(tableName == null){
			return result;
		}
		
		try {
			Cursor cursor;
			String sql = "select count(1) from sqlite_master where type='table' and name='"+ tableName.trim() +"'";
			cursor = mDb.rawQuery(sql, null);
			
			if(cursor.moveToNext()){
				int count = cursor.getInt(0);
				if(count > 0){
					result = true;
				}
			}
			
			cursor.close();
			
		} catch (Exception e) {
			//TODO
			e.printStackTrace();
		}

		return result;
		
	}
	
	
	/**
	 * 查询表中是否有某字段
	 * @param tableName
	 * @param columnName  字段名
	 * @return
	 */
	public boolean isColumnExist(String tableName,String columnName){
		
		boolean result = false;
		
		//如果表不存在怎返回false
		if(!isTableExist(tableName)){
			return false;
		}
		
		try {
			Cursor cursor;
			String sql = "select count(1) from sqlite_master where type = 'table' and name = '" +tableName.trim()+"' and sql like '%"+columnName.trim() +"%'";
			cursor = mDb.rawQuery(sql, null);
			if(cursor.moveToNext()){
				int count = cursor.getInt(0);
				if(count > 0){
					result = true;
				}
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return result;
	}
	
}
