package com.zzb.qinzhusocial.db;

/**
 * Created by zzb on 2017/4/13.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zzb.qinzhusocial.bean.NoticeBean;

import java.util.ArrayList;

/**
 *DBManager是建立在DBHelper之上，封装了常用的业务方法
 */
public class DBManager {

    private DBHelper helper;
    private SQLiteDatabase db;
    public DBManager(Context context){
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 增
     */
    public void add(String name,String notice) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("reason", notice);
        cv.put("agree", "none");
        db.insert("notice", null, cv);
    }

    /**
     * 通过name来删除数据
     *
     * @param name
     */
    public void delData(String name) {
        // ExecSQL("DELETE FROM info WHERE name ="+"'"+name+"'");
        String[] args = { name };
        db.delete("notice", "name=?", args);

    }

    /**
     * 清空数据
     */
    public void clearData() {
        ExecSQL("DELETE FROM notice");
    }

    public void clearUser() {
        String sql = "DELETE FROM user";
        try {
            db.execSQL(sql);
            revertSeq(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void revertSeq(SQLiteDatabase db) {
        String sql = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'user'";
        db.execSQL(sql);
    }



    /**
     * 通过名字查询信息,返回所有的数据
     *
     * @param name
     */
    public ArrayList<NoticeBean> searchData(final String name) {
        String sql = "SELECT * FROM notice WHERE name =" + "'" + name +"'";
        return ExecSQLForNoticeBean(sql);
    }

    //查询整张表
    public ArrayList<NoticeBean> searchData() {
        String sql = "SELECT * FROM notice";
        return ExecSQLForNoticeBean(sql);
    }

    public ArrayList<NoticeBean> searchDataWithId(final String _id) {
        String sql = "SELECT * FROM notice WHERE _id =" + "'" + _id +"'" ;
        return ExecSQLForNoticeBean(sql);
    }


    /**
     * 执行SQL命令返回list
     * @param sql
     * @return
     */
    private ArrayList<NoticeBean> ExecSQLForNoticeBean(String sql) {
        ArrayList<NoticeBean> list = new ArrayList<NoticeBean>();
        Cursor c = ExecSQLForCursor(sql);
        while (c.moveToNext()) {
            NoticeBean bean = new NoticeBean();
            bean._id = c.getString(c.getColumnIndex("_id"));
            bean.name = c.getString(c.getColumnIndex("name"));
            bean.reason = c.getString(c.getColumnIndex("reason"));
            bean.isAgree = c.getString(c.getColumnIndex("agree"));
            list.add(bean);
        }
        c.close();
        return list;
    }




    /**
     * 执行一个SQL语句
     *
     * @param sql
     */
    private void ExecSQL(String sql) {
        try {
            db.execSQL(sql);
            Log.i("execSql: ", sql);
        } catch (Exception e) {
            Log.e("ExecSQL Exception", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行SQL，返回一个游标
     *
     * @param sql
     * @return
     */
    private Cursor ExecSQLForCursor(String sql) {
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public void closeDB() {
        db.close();
    }

    //改
    public void upDate(String isAgree,String _id) {
        String sql = "UPDATE notice SET agree ="+ "'" +isAgree+ "'"  + " WHERE _id =" + "'" + _id+ "'";
        ExecSQL(sql);
    }

}
